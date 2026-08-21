package ru.practica2026.admin.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practica2026.admin.common.response.PageResponse;
import ru.practica2026.admin.user.dto.request.AssignRoleRequest;
import ru.practica2026.admin.user.dto.request.ChangeUserStatusRequest;
import ru.practica2026.admin.user.dto.request.CreateUserRequest;
import ru.practica2026.admin.user.dto.request.UpdateUserRequest;
import ru.practica2026.admin.user.dto.response.UserDetailResponse;
import ru.practica2026.admin.user.dto.response.IdpSyncResponse;
import ru.practica2026.admin.user.dto.response.UserResponse;
import ru.practica2026.admin.user.entity.UserStatus;
import ru.practica2026.admin.user.service.UserService;
import ru.practica2026.admin.security.service.LocalUserIdentityService;
import ru.practica2026.admin.user.entity.UserAccount;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final UserService userService;
    private final LocalUserIdentityService localUserIdentityService;

    public UserController(
            UserService userService,
            LocalUserIdentityService localUserIdentityService
    ) {
        this.userService = userService;
        this.localUserIdentityService = localUserIdentityService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(
            @Valid
            @RequestBody
            CreateUserRequest request
    ) {
        UserResponse response =
                userService.create(request);

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/users/" +
                                response.businessKey()
                        )
                )
                .body(response);
    }

    @GetMapping("/{businessKey}")
    public UserDetailResponse get(
            @PathVariable
            UUID businessKey
    ) {
        return userService.get(businessKey);
    }

    @GetMapping
    public PageResponse<UserResponse> findAll(

            @RequestParam(required = false)
            String search,

            @RequestParam(required = false)
            UserStatus status,

            @RequestParam(required = false)
            UUID organizationBusinessKey,

            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @RequestParam(defaultValue = "20")
            @Min(1)
            @Max(100)
            int size,

            @RequestParam(defaultValue = "username")
            String sortBy,

            @RequestParam(defaultValue = "ASC")
            Sort.Direction direction
    ) {
        return userService.findAll(
                search,
                status,
                organizationBusinessKey,
                page,
                size,
                sortBy,
                direction
        );
    }

    @PutMapping("/{businessKey}")
    public UserResponse update(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            UpdateUserRequest request
    ) {
        return userService.update(
                businessKey,
                request
        );
    }

    @PatchMapping("/{businessKey}/status")
    public UserResponse changeStatus(
            @PathVariable
            UUID businessKey,

            @Valid
            @RequestBody
            ChangeUserStatusRequest request
    ) {
        return userService.changeStatus(
                businessKey,
                request
        );
    }

    @PostMapping(
            "/{userBusinessKey}/roles/{roleBusinessKey}"
    )
    public UserDetailResponse assignRole(

            @PathVariable
            UUID userBusinessKey,

            @PathVariable
            UUID roleBusinessKey,

            @Valid
            @RequestBody(required = false)
            AssignRoleRequest request
    ) {
        return userService.assignRole(
                userBusinessKey,
                roleBusinessKey,
                request
        );
    }

    @DeleteMapping(
            "/{userBusinessKey}/roles/{roleBusinessKey}"
    )
    public ResponseEntity<Void> removeRole(

            @PathVariable
            UUID userBusinessKey,

            @PathVariable
            UUID roleBusinessKey
    ) {
        userService.removeRole(
                userBusinessKey,
                roleBusinessKey
        );

        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/sync-idp")
    public IdpSyncResponse synchronizeWithIdp(
            @AuthenticationPrincipal
            Jwt jwt
    ) {
        UserAccount user =
                localUserIdentityService
                        .synchronizeCurrentIdentity(jwt);

        return new IdpSyncResponse(
                user.getBusinessKey(),
                user.getUsername(),
                user.getExternalId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                true
        );
    }
}
