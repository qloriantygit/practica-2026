package ru.practica2026.admin.directory.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import ru.practica2026.admin.common.exception.ResourceNotFoundException;
import ru.practica2026.admin.directory.dto.request.CreateDirectoryItemRequest;
import ru.practica2026.admin.directory.dto.response.DirectoryImportResponse;
import ru.practica2026.admin.directory.entity.DirectoryItem;
import ru.practica2026.admin.directory.entity.DirectoryVersion;
import ru.practica2026.admin.directory.file.DirectoryFileFormat;
import ru.practica2026.admin.directory.repository.DirectoryItemRepository;
import ru.practica2026.admin.directory.repository.DirectoryVersionRepository;
import ru.practica2026.admin.security.service.CurrentActorService;
import ru.practica2026.admin.storage.service.MinioStorageService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class DirectoryFileService {

    private static final DateTimeFormatter FILE_TIME =
            DateTimeFormatter
                    .ofPattern(
                            "yyyyMMdd-HHmmss-SSS"
                    )
                    .withZone(
                            ZoneOffset.UTC
                    );

    private final DirectoryService directoryService;
    private final DirectoryVersionRepository directoryVersionRepository;
    private final DirectoryItemRepository directoryItemRepository;
    private final MinioStorageService storageService;
    private final CurrentActorService currentActorService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    private final CsvMapper csvMapper =
            new CsvMapper();

    public DirectoryFileService(
            DirectoryService directoryService,
            DirectoryVersionRepository directoryVersionRepository,
            DirectoryItemRepository directoryItemRepository,
            MinioStorageService storageService,
            CurrentActorService currentActorService,
            ObjectMapper objectMapper,
            Validator validator
    ) {
        this.directoryService = directoryService;
        this.directoryVersionRepository =
                directoryVersionRepository;
        this.directoryItemRepository =
                directoryItemRepository;
        this.storageService = storageService;
        this.currentActorService =
                currentActorService;
        this.objectMapper = objectMapper;
        this.validator = validator;
    }

    @Transactional
    public DirectoryImportResponse importItems(
            UUID versionBusinessKey,
            DirectoryFileFormat format,
            MultipartFile file
    ) {
        if (
                file == null
                ||
                file.isEmpty()
        ) {
            throw badRequest(
                    "Import file is empty"
            );
        }

        DirectoryVersion version =
                getVersion(
                        versionBusinessKey
                );

        try {
            byte[] content =
                    file.getBytes();

            List<CreateDirectoryItemRequest> rows =
                    switch (format) {
                        case JSON ->
                                parseJson(content);

                        case CSV ->
                                parseCsv(content);
                    };

            if (rows.isEmpty()) {
                throw badRequest(
                        "Import file does not contain any items"
                );
            }

            for (
                    CreateDirectoryItemRequest row
                    : rows
            ) {
                validateRow(row);

                directoryService.createItem(
                        versionBusinessKey,
                        row
                );
            }

            String storageObject =
                    storageService.store(
                            buildImportObjectName(
                                    version,
                                    file,
                                    format
                            ),
                            content
                    );

            return new DirectoryImportResponse(
                    versionBusinessKey,
                    format,
                    rows.size(),
                    storageObject,
                    currentActorService
                            .getCurrentActor()
            );
        }
        catch (ResponseStatusException exception) {
            throw exception;
        }
        catch (RuntimeException exception) {
            throw exception;
        }
        catch (Exception exception) {
            throw badRequest(
                    "Failed to parse import file: "
                            + exception.getMessage()
            );
        }
    }

    @Transactional(readOnly = true)
    public ExportResult exportItems(
            UUID versionBusinessKey,
            DirectoryFileFormat format
    ) {
        DirectoryVersion version =
                getVersion(
                        versionBusinessKey
                );

        List<DirectoryItem> items =
                directoryItemRepository
                        .findAllByDirectoryVersionOrderBySortOrderAscCodeAsc(
                                version
                        );

        try {
            byte[] content =
                    switch (format) {
                        case JSON ->
                                buildJson(items);

                        case CSV ->
                                buildCsv(items);
                    };

            String fileName =
                    version.getDirectory()
                            .getCode()
                            .toLowerCase(Locale.ROOT)
                            + "-v"
                            + version.getVersionNumber()
                            + "."
                            + format.getExtension();

            String objectName =
                    "exports/"
                            + version.getDirectory()
                                    .getCode()
                            + "/v"
                            + version.getVersionNumber()
                            + "/"
                            + FILE_TIME.format(
                                    Instant.now()
                            )
                            + "-"
                            + fileName;

            String storageObject =
                    storageService.store(
                            objectName,
                            content
                    );

            return new ExportResult(
                    content,
                    fileName,
                    format.getContentType(),
                    storageObject
            );
        }
        catch (RuntimeException exception) {
            throw exception;
        }
        catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to export directory version",
                    exception
            );
        }
    }

    private List<CreateDirectoryItemRequest> parseJson(
            byte[] content
    ) throws Exception {

        JsonNode root =
                objectMapper.readTree(
                        content
                );

        if (!root.isArray()) {
            throw badRequest(
                    "JSON import root must be an array"
            );
        }

        List<CreateDirectoryItemRequest> result =
                new ArrayList<>();

        int rowNumber = 0;

        for (JsonNode node : root) {
            rowNumber++;

            result.add(
                    toRequestFromJson(
                            node,
                            rowNumber
                    )
            );
        }

        return result;
    }

    private List<CreateDirectoryItemRequest> parseCsv(
            byte[] content
    ) throws Exception {

        CsvSchema schema =
                CsvSchema
                        .emptySchema()
                        .withHeader();

        List<CreateDirectoryItemRequest> result =
                new ArrayList<>();

        try (
                var iterator =
                        csvMapper
                                .readerFor(
                                        JsonNode.class
                                )
                                .with(schema)
                                .readValues(
                                        new ByteArrayInputStream(
                                                content
                                        )
                                )
        ) {
            int rowNumber = 0;

            while (iterator.hasNext()) {
                rowNumber++;

                JsonNode node =
                        (JsonNode) iterator.next();

                result.add(
                        toRequestFromCsv(
                                node,
                                rowNumber
                        )
                );
            }
        }

        return result;
    }

    private CreateDirectoryItemRequest toRequestFromJson(
            JsonNode node,
            int rowNumber
    ) {
        if (!node.isObject()) {
            throw badRequest(
                    "JSON row "
                            + rowNumber
                            + " must be an object"
            );
        }

        Map<String, Object> attributes =
                Map.of();

        if (
                node.has("attributes")
                &&
                !node.get("attributes").isNull()
        ) {
            if (!node.get("attributes").isObject()) {
                throw badRequest(
                        "JSON row "
                                + rowNumber
                                + ": attributes must be an object"
                );
            }

            attributes =
                    objectMapper.convertValue(
                            node.get("attributes"),
                            new TypeReference<
                                    Map<String, Object>
                                    >() {
                            }
                    );
        }

        return new CreateDirectoryItemRequest(
                requiredText(
                        node,
                        "code",
                        rowNumber
                ),
                requiredText(
                        node,
                        "name",
                        rowNumber
                ),
                nullableText(
                        node,
                        "description"
                ),
                node.hasNonNull("enabled")
                        ? node.get("enabled")
                                .asBoolean()
                        : null,
                node.hasNonNull("sortOrder")
                        ? node.get("sortOrder")
                                .asInt()
                        : null,
                attributes
        );
    }

    private CreateDirectoryItemRequest toRequestFromCsv(
            JsonNode node,
            int rowNumber
    ) throws Exception {

        String attributesText =
                nullableText(
                        node,
                        "attributes"
                );

        Map<String, Object> attributes =
                Map.of();

        if (
                attributesText != null
                &&
                !attributesText.isBlank()
        ) {
            JsonNode attributesNode =
                    objectMapper.readTree(
                            attributesText
                    );

            if (!attributesNode.isObject()) {
                throw badRequest(
                        "CSV row "
                                + rowNumber
                                + ": attributes must contain a JSON object"
                );
            }

            attributes =
                    objectMapper.convertValue(
                            attributesNode,
                            new TypeReference<
                                    Map<String, Object>
                                    >() {
                            }
                    );
        }

        return new CreateDirectoryItemRequest(
                requiredText(
                        node,
                        "code",
                        rowNumber
                ),
                requiredText(
                        node,
                        "name",
                        rowNumber
                ),
                nullableText(
                        node,
                        "description"
                ),
                parseBoolean(
                        nullableText(
                                node,
                                "enabled"
                        ),
                        rowNumber
                ),
                parseInteger(
                        nullableText(
                                node,
                                "sortOrder"
                        ),
                        rowNumber
                ),
                attributes
        );
    }

    private byte[] buildJson(
            List<DirectoryItem> items
    ) throws Exception {

        ArrayNode array =
                objectMapper.createArrayNode();

        for (DirectoryItem item : items) {

            ObjectNode node =
                    objectMapper.createObjectNode();

            node.put(
                    "code",
                    item.getCode()
            );

            node.put(
                    "name",
                    item.getName()
            );

            if (item.getDescription() != null) {
                node.put(
                        "description",
                        item.getDescription()
                );
            }
            else {
                node.putNull("description");
            }

            node.put(
                    "enabled",
                    item.isEnabled()
            );

            node.put(
                    "sortOrder",
                    item.getSortOrder()
            );

            node.set(
                    "attributes",
                    objectMapper.valueToTree(
                            item.getAttributes()
                    )
            );

            array.add(node);
        }

        return objectMapper
                .writerWithDefaultPrettyPrinter()
                .writeValueAsBytes(array);
    }

    private byte[] buildCsv(
            List<DirectoryItem> items
    ) throws Exception {

        CsvSchema schema =
                CsvSchema.builder()
                        .addColumn("code")
                        .addColumn("name")
                        .addColumn("description")
                        .addColumn("enabled")
                        .addColumn("sortOrder")
                        .addColumn("attributes")
                        .setUseHeader(true)
                        .build();

        ByteArrayOutputStream output =
                new ByteArrayOutputStream();

        try (
                SequenceWriter writer =
                        csvMapper
                                .writer(schema)
                                .writeValues(output)
        ) {
            for (DirectoryItem item : items) {

                Map<String, Object> row =
                        new LinkedHashMap<>();

                row.put(
                        "code",
                        item.getCode()
                );

                row.put(
                        "name",
                        item.getName()
                );

                row.put(
                        "description",
                        item.getDescription()
                );

                row.put(
                        "enabled",
                        item.isEnabled()
                );

                row.put(
                        "sortOrder",
                        item.getSortOrder()
                );

                row.put(
                        "attributes",
                        objectMapper.writeValueAsString(
                                item.getAttributes()
                        )
                );

                writer.write(row);
            }
        }

        return output.toByteArray();
    }

    private void validateRow(
            CreateDirectoryItemRequest request
    ) {
        Set<
                ConstraintViolation<
                        CreateDirectoryItemRequest
                        >
                > violations =
                validator.validate(request);

        if (!violations.isEmpty()) {
            ConstraintViolation<
                    CreateDirectoryItemRequest
                    > violation =
                    violations.iterator()
                            .next();

            throw badRequest(
                    violation.getPropertyPath()
                            + " "
                            + violation.getMessage()
            );
        }
    }

    private String requiredText(
            JsonNode node,
            String field,
            int rowNumber
    ) {
        String value =
                nullableText(
                        node,
                        field
                );

        if (
                value == null
                ||
                value.isBlank()
        ) {
            throw badRequest(
                    "Row "
                            + rowNumber
                            + ": field '"
                            + field
                            + "' is required"
            );
        }

        return value;
    }

    private String nullableText(
            JsonNode node,
            String field
    ) {
        JsonNode value =
                node.get(field);

        if (
                value == null
                ||
                value.isNull()
        ) {
            return null;
        }

        String text =
                value.asText();

        return text.isBlank()
                ? null
                : text;
    }

    private Boolean parseBoolean(
            String value,
            int rowNumber
    ) {
        if (value == null) {
            return null;
        }

        if ("true".equalsIgnoreCase(value)) {
            return true;
        }

        if ("false".equalsIgnoreCase(value)) {
            return false;
        }

        throw badRequest(
                "CSV row "
                        + rowNumber
                        + ": enabled must be true or false"
        );
    }

    private Integer parseInteger(
            String value,
            int rowNumber
    ) {
        if (value == null) {
            return null;
        }

        try {
            return Integer.valueOf(value);
        }
        catch (NumberFormatException exception) {
            throw badRequest(
                    "CSV row "
                            + rowNumber
                            + ": sortOrder must be an integer"
            );
        }
    }

    private String buildImportObjectName(
            DirectoryVersion version,
            MultipartFile file,
            DirectoryFileFormat format
    ) {
        String originalName =
                file.getOriginalFilename();

        if (
                originalName == null
                ||
                originalName.isBlank()
        ) {
            originalName =
                    "import."
                            + format.getExtension();
        }

        String safeName =
                originalName.replaceAll(
                        "[^A-Za-z0-9._-]",
                        "_"
                );

        return "imports/"
                + version.getDirectory()
                        .getCode()
                + "/v"
                + version.getVersionNumber()
                + "/"
                + FILE_TIME.format(
                        Instant.now()
                )
                + "-"
                + safeName;
    }

    private DirectoryVersion getVersion(
            UUID businessKey
    ) {
        return directoryVersionRepository
                .findByBusinessKey(
                        businessKey
                )
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Directory version not found: "
                                                + businessKey
                                )
                );
    }

    private ResponseStatusException badRequest(
            String message
    ) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    public record ExportResult(

            byte[] content,

            String fileName,

            String contentType,

            String storageObject
    ) {
    }
}
