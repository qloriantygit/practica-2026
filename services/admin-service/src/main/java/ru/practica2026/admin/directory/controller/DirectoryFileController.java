package ru.practica2026.admin.directory.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import ru.practica2026.admin.directory.dto.response.DirectoryImportResponse;
import ru.practica2026.admin.directory.file.DirectoryFileFormat;
import ru.practica2026.admin.directory.service.DirectoryFileService;

import java.util.UUID;

@RestController
@RequestMapping(
        "/api/v1/directory-versions"
)
public class DirectoryFileController {

    private final DirectoryFileService directoryFileService;

    public DirectoryFileController(
            DirectoryFileService directoryFileService
    ) {
        this.directoryFileService =
                directoryFileService;
    }

    @PostMapping(
            value = "/{versionBusinessKey}/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public DirectoryImportResponse importItems(
            @PathVariable
            UUID versionBusinessKey,

            @RequestParam
            DirectoryFileFormat format,

            @RequestPart("file")
            MultipartFile file
    ) {
        return directoryFileService.importItems(
                versionBusinessKey,
                format,
                file
        );
    }

    @GetMapping(
            "/{versionBusinessKey}/export"
    )
    public ResponseEntity<byte[]> exportItems(
            @PathVariable
            UUID versionBusinessKey,

            @RequestParam
            DirectoryFileFormat format
    ) {
        DirectoryFileService.ExportResult result =
                directoryFileService.exportItems(
                        versionBusinessKey,
                        format
                );

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                result.contentType()
                        )
                )
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition
                                .attachment()
                                .filename(
                                        result.fileName()
                                )
                                .build()
                                .toString()
                )
                .header(
                        "X-Storage-Object",
                        result.storageObject()
                )
                .body(
                        result.content()
                );
    }
}
