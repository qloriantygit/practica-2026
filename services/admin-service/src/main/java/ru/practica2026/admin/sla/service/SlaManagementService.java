package ru.practica2026.admin.sla.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practica2026.admin.common.exception.ConflictException;
import ru.practica2026.admin.common.exception.ResourceNotFoundException;
import ru.practica2026.admin.security.service.CurrentActorService;
import ru.practica2026.admin.sla.dto.request.CreateCalendarExceptionRequest;
import ru.practica2026.admin.sla.dto.request.CreateSlaPolicyRequest;
import ru.practica2026.admin.sla.dto.request.CreateWorkCalendarRequest;
import ru.practica2026.admin.sla.dto.response.CalendarDayResponse;
import ru.practica2026.admin.sla.dto.response.CalendarExceptionResponse;
import ru.practica2026.admin.sla.dto.response.SlaPolicyPageResponse;
import ru.practica2026.admin.sla.dto.response.SlaPolicyResponse;
import ru.practica2026.admin.sla.dto.response.WorkCalendarPageResponse;
import ru.practica2026.admin.sla.dto.response.WorkCalendarResponse;
import ru.practica2026.admin.sla.entity.SlaPolicy;
import ru.practica2026.admin.sla.entity.WorkCalendar;
import ru.practica2026.admin.sla.entity.WorkCalendarException;
import ru.practica2026.admin.sla.repository.SlaPolicyRepository;
import ru.practica2026.admin.sla.repository.WorkCalendarExceptionRepository;
import ru.practica2026.admin.sla.repository.WorkCalendarRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class SlaManagementService {

    private final WorkCalendarRepository calendarRepository;
    private final WorkCalendarExceptionRepository exceptionRepository;
    private final SlaPolicyRepository slaRepository;
    private final CurrentActorService currentActorService;

    public SlaManagementService(
            WorkCalendarRepository calendarRepository,
            WorkCalendarExceptionRepository exceptionRepository,
            SlaPolicyRepository slaRepository,
            CurrentActorService currentActorService
    ) {
        this.calendarRepository = calendarRepository;
        this.exceptionRepository = exceptionRepository;
        this.slaRepository = slaRepository;
        this.currentActorService = currentActorService;
    }

    @Transactional
    public WorkCalendarResponse createCalendar(
            CreateWorkCalendarRequest request
    ) {
        String code =
                normalizeCode(request.code());

        if (
                calendarRepository
                        .existsByCodeIgnoreCase(code)
        ) {
            throw new ConflictException(
                    "Work calendar with code '"
                            + code
                            + "' already exists"
            );
        }

        validateTimezone(
                request.timezone()
        );

        validateHours(
                request.workdayStart(),
                request.workdayEnd()
        );

        String actor =
                currentActorService.getCurrentActor();

        WorkCalendar calendar =
                new WorkCalendar();

        calendar.setCode(code);
        calendar.setName(
                request.name().trim()
        );
        calendar.setDescription(
                normalizeNullable(
                        request.description()
                )
        );
        calendar.setTimezone(
                request.timezone().trim()
        );
        calendar.setWorkingDays(
                request.workingDays()
        );
        calendar.setWorkdayStart(
                request.workdayStart()
        );
        calendar.setWorkdayEnd(
                request.workdayEnd()
        );
        calendar.setActive(
                request.active() == null
                        || request.active()
        );
        calendar.setCreatedBy(actor);
        calendar.setUpdatedBy(actor);

        WorkCalendar saved =
                calendarRepository
                        .saveAndFlush(calendar);

        return buildCalendarResponse(saved);
    }

    @Transactional(readOnly = true)
    public WorkCalendarPageResponse findCalendars(
            String search,
            Boolean active,
            int page,
            int size
    ) {
        Page<WorkCalendar> result =
                calendarRepository.search(
                        normalizeSearch(search),
                        active,
                        PageRequest.of(
                                Math.max(page, 0),
                                Math.min(
                                        Math.max(size, 1),
                                        100
                                ),
                                Sort.by(
                                        Sort.Direction.ASC,
                                        "code"
                                )
                        )
                );

        return new WorkCalendarPageResponse(
                result.getContent()
                        .stream()
                        .map(
                                this::buildCalendarResponse
                        )
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public WorkCalendarResponse findCalendar(
            UUID businessKey
    ) {
        return buildCalendarResponse(
                getCalendar(businessKey)
        );
    }

    @Transactional
    public CalendarExceptionResponse addException(
            UUID calendarBusinessKey,
            CreateCalendarExceptionRequest request
    ) {
        WorkCalendar calendar =
                getCalendar(
                        calendarBusinessKey
                );

        if (
                exceptionRepository
                        .existsByCalendarAndExceptionDate(
                                calendar,
                                request.date()
                        )
        ) {
            throw new ConflictException(
                    "Calendar exception already exists for "
                            + request.date()
            );
        }

        LocalTime start =
                request.workingDay()
                        ? (
                            request.workdayStart() != null
                                ? request.workdayStart()
                                : calendar.getWorkdayStart()
                        )
                        : null;

        LocalTime end =
                request.workingDay()
                        ? (
                            request.workdayEnd() != null
                                ? request.workdayEnd()
                                : calendar.getWorkdayEnd()
                        )
                        : null;

        if (request.workingDay()) {
            validateHours(start, end);
        }

        String actor =
                currentActorService.getCurrentActor();

        WorkCalendarException exception =
                new WorkCalendarException();

        exception.setCalendar(calendar);
        exception.setExceptionDate(
                request.date()
        );
        exception.setWorkingDay(
                request.workingDay()
        );
        exception.setWorkdayStart(start);
        exception.setWorkdayEnd(end);
        exception.setDescription(
                normalizeNullable(
                        request.description()
                )
        );
        exception.setCreatedBy(actor);
        exception.setUpdatedBy(actor);

        WorkCalendarException saved =
                exceptionRepository
                        .saveAndFlush(exception);

        return toExceptionResponse(saved);
    }

    @Transactional
    public void deleteException(
            UUID calendarBusinessKey,
            UUID exceptionBusinessKey
    ) {
        WorkCalendar calendar =
                getCalendar(
                        calendarBusinessKey
                );

        WorkCalendarException exception =
                exceptionRepository
                        .findByBusinessKey(
                                exceptionBusinessKey
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Calendar exception not found: "
                                                        + exceptionBusinessKey
                                        )
                        );

        if (
                !exception.getCalendar()
                        .getId()
                        .equals(calendar.getId())
        ) {
            throw new ResourceNotFoundException(
                    "Calendar exception does not belong to specified calendar"
            );
        }

        exceptionRepository.delete(exception);
        exceptionRepository.flush();
    }

    @Transactional(readOnly = true)
    public CalendarDayResponse resolveDay(
            UUID calendarBusinessKey,
            LocalDate date
    ) {
        WorkCalendar calendar =
                getCalendar(
                        calendarBusinessKey
                );

        WorkCalendarException exception =
                exceptionRepository
                        .findByCalendarAndExceptionDate(
                                calendar,
                                date
                        )
                        .orElse(null);

        if (exception != null) {
            return new CalendarDayResponse(
                    calendar.getBusinessKey(),
                    date,
                    exception.isWorkingDay(),
                    exception.getWorkdayStart(),
                    exception.getWorkdayEnd(),
                    "EXCEPTION",
                    exception.getDescription()
            );
        }

        boolean workingDay =
                calendar.getWorkingDays()
                        .contains(
                                date.getDayOfWeek()
                        );

        return new CalendarDayResponse(
                calendar.getBusinessKey(),
                date,
                workingDay,
                workingDay
                        ? calendar.getWorkdayStart()
                        : null,
                workingDay
                        ? calendar.getWorkdayEnd()
                        : null,
                "DEFAULT",
                null
        );
    }

    @Transactional
    public SlaPolicyResponse createSla(
            CreateSlaPolicyRequest request
    ) {
        String code =
                normalizeCode(request.code());

        if (
                slaRepository
                        .existsByCodeIgnoreCase(code)
        ) {
            throw new ConflictException(
                    "SLA policy with code '"
                            + code
                            + "' already exists"
            );
        }

        validateSla(
                request.responseMinutes(),
                request.resolutionMinutes()
        );

        WorkCalendar calendar =
                getCalendar(
                        request.calendarBusinessKey()
                );

        if (!calendar.isActive()) {
            throw new ConflictException(
                    "SLA policy cannot reference inactive calendar"
            );
        }

        String actor =
                currentActorService.getCurrentActor();

        SlaPolicy policy =
                new SlaPolicy();

        policy.setCode(code);
        policy.setName(
                request.name().trim()
        );
        policy.setDescription(
                normalizeNullable(
                        request.description()
                )
        );
        policy.setResponseMinutes(
                request.responseMinutes()
        );
        policy.setResolutionMinutes(
                request.resolutionMinutes()
        );
        policy.setCalendar(calendar);
        policy.setActive(
                request.active() == null
                        || request.active()
        );
        policy.setCreatedBy(actor);
        policy.setUpdatedBy(actor);

        SlaPolicy saved =
                slaRepository
                        .saveAndFlush(policy);

        return toSlaResponse(saved);
    }

    @Transactional(readOnly = true)
    public SlaPolicyPageResponse findSlaPolicies(
            String search,
            Boolean active,
            int page,
            int size
    ) {
        Page<SlaPolicy> result =
                slaRepository.search(
                        normalizeSearch(search),
                        active,
                        PageRequest.of(
                                Math.max(page, 0),
                                Math.min(
                                        Math.max(size, 1),
                                        100
                                ),
                                Sort.by(
                                        Sort.Direction.ASC,
                                        "code"
                                )
                        )
                );

        return new SlaPolicyPageResponse(
                result.getContent()
                        .stream()
                        .map(this::toSlaResponse)
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public SlaPolicyResponse findSla(
            UUID businessKey
    ) {
        return toSlaResponse(
                slaRepository
                        .findByBusinessKey(
                                businessKey
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "SLA policy not found: "
                                                        + businessKey
                                        )
                        )
        );
    }

    private WorkCalendarResponse buildCalendarResponse(
            WorkCalendar calendar
    ) {
        List<CalendarExceptionResponse> exceptions =
                exceptionRepository
                        .findAllByCalendarOrderByExceptionDateAsc(
                                calendar
                        )
                        .stream()
                        .map(
                                this::toExceptionResponse
                        )
                        .toList();

        return new WorkCalendarResponse(
                calendar.getBusinessKey(),
                calendar.getCode(),
                calendar.getName(),
                calendar.getDescription(),
                calendar.getTimezone(),
                new LinkedHashSet<>(
                        calendar.getWorkingDays()
                ),
                calendar.getWorkdayStart(),
                calendar.getWorkdayEnd(),
                calendar.isActive(),
                exceptions,
                calendar.getVersion(),
                calendar.getCreatedAt(),
                calendar.getUpdatedAt(),
                calendar.getCreatedBy(),
                calendar.getUpdatedBy()
        );
    }

    private CalendarExceptionResponse toExceptionResponse(
            WorkCalendarException exception
    ) {
        return new CalendarExceptionResponse(
                exception.getBusinessKey(),
                exception.getExceptionDate(),
                exception.isWorkingDay(),
                exception.getWorkdayStart(),
                exception.getWorkdayEnd(),
                exception.getDescription(),
                exception.getCreatedBy()
        );
    }

    private SlaPolicyResponse toSlaResponse(
            SlaPolicy policy
    ) {
        return new SlaPolicyResponse(
                policy.getBusinessKey(),
                policy.getCode(),
                policy.getName(),
                policy.getDescription(),
                policy.getResponseMinutes(),
                policy.getResolutionMinutes(),
                policy.getCalendar()
                        .getBusinessKey(),
                policy.getCalendar().getCode(),
                policy.isActive(),
                policy.getVersion(),
                policy.getCreatedAt(),
                policy.getUpdatedAt(),
                policy.getCreatedBy(),
                policy.getUpdatedBy()
        );
    }

    private WorkCalendar getCalendar(
            UUID businessKey
    ) {
        return calendarRepository
                .findByBusinessKey(businessKey)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Work calendar not found: "
                                                + businessKey
                                )
                );
    }

    private void validateTimezone(
            String timezone
    ) {
        try {
            ZoneId.of(timezone.trim());
        }
        catch (Exception exception) {
            throw new ConflictException(
                    "Invalid timezone: "
                            + timezone
            );
        }
    }

    private void validateHours(
            LocalTime start,
            LocalTime end
    ) {
        if (
                start == null
                ||
                end == null
                ||
                !end.isAfter(start)
        ) {
            throw new ConflictException(
                    "workdayEnd must be later than workdayStart"
            );
        }
    }

    private void validateSla(
            Integer responseMinutes,
            Integer resolutionMinutes
    ) {
        if (
                responseMinutes == null
                ||
                resolutionMinutes == null
                ||
                responseMinutes <= 0
                ||
                resolutionMinutes <= 0
                ||
                resolutionMinutes
                        < responseMinutes
        ) {
            throw new ConflictException(
                    "resolutionMinutes must be greater than or equal to responseMinutes"
            );
        }
    }

    private String normalizeCode(
            String value
    ) {
        return value
                .trim()
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeNullable(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        return normalized.isEmpty()
                ? null
                : normalized;
    }

    private String normalizeSearch(
            String value
    ) {
        if (value == null) {
            return null;
        }

        String normalized =
                value.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        return "%"
                + normalized
                        .toLowerCase(Locale.ROOT)
                + "%";
    }
}
