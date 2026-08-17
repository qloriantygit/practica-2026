package ru.practica2026.admin.sla.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.admin.sla.dto.request.CreateCalendarExceptionRequest;
import ru.practica2026.admin.sla.dto.request.CreateSlaPolicyRequest;
import ru.practica2026.admin.sla.dto.request.CreateWorkCalendarRequest;
import ru.practica2026.admin.sla.dto.response.CalendarDayResponse;
import ru.practica2026.admin.sla.dto.response.CalendarExceptionResponse;
import ru.practica2026.admin.sla.dto.response.SlaPolicyPageResponse;
import ru.practica2026.admin.sla.dto.response.SlaPolicyResponse;
import ru.practica2026.admin.sla.dto.response.WorkCalendarPageResponse;
import ru.practica2026.admin.sla.dto.response.WorkCalendarResponse;
import ru.practica2026.admin.sla.service.SlaManagementService;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class SlaManagementController {

    private final SlaManagementService service;

    public SlaManagementController(
            SlaManagementService service
    ) {
        this.service = service;
    }

    @PostMapping("/calendars")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkCalendarResponse createCalendar(
            @Valid
            @RequestBody
            CreateWorkCalendarRequest request
    ) {
        return service.createCalendar(request);
    }

    @GetMapping("/calendars")
    public WorkCalendarPageResponse findCalendars(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            Boolean active,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        return service.findCalendars(
                search,
                active,
                page,
                size
        );
    }

    @GetMapping("/calendars/{businessKey}")
    public WorkCalendarResponse findCalendar(
            @PathVariable
            UUID businessKey
    ) {
        return service.findCalendar(
                businessKey
        );
    }

    @PostMapping(
            "/calendars/{businessKey}/exceptions"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public CalendarExceptionResponse addException(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            CreateCalendarExceptionRequest request
    ) {
        return service.addException(
                businessKey,
                request
        );
    }

    @DeleteMapping(
            "/calendars/{businessKey}/exceptions/{exceptionBusinessKey}"
    )
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteException(
            @PathVariable
            UUID businessKey,

            @PathVariable
            UUID exceptionBusinessKey
    ) {
        service.deleteException(
                businessKey,
                exceptionBusinessKey
        );
    }

    @GetMapping(
            "/calendars/{businessKey}/days/{date}"
    )
    public CalendarDayResponse resolveDay(
            @PathVariable
            UUID businessKey,

            @PathVariable
            LocalDate date
    ) {
        return service.resolveDay(
                businessKey,
                date
        );
    }

    @PostMapping("/sla-policies")
    @ResponseStatus(HttpStatus.CREATED)
    public SlaPolicyResponse createSla(
            @Valid
            @RequestBody
            CreateSlaPolicyRequest request
    ) {
        return service.createSla(request);
    }

    @GetMapping("/sla-policies")
    public SlaPolicyPageResponse findSlaPolicies(
            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            Boolean active,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "20")
            int size
    ) {
        return service.findSlaPolicies(
                search,
                active,
                page,
                size
        );
    }

    @GetMapping(
            "/sla-policies/{businessKey}"
    )
    public SlaPolicyResponse findSla(
            @PathVariable
            UUID businessKey
    ) {
        return service.findSla(
                businessKey
        );
    }
}
