package com.digitalsignage.domain.user.api;

import com.digitalsignage.domain.user.application.UserService;
import com.digitalsignage.domain.user.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive REST controller for User operations.
 * Non-blocking endpoints following WebFlux patterns.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user.
     * POST /api/v1/users
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return userService.createUser(
                request.username(),
                request.email(),
                request.password(),
                request.role() != null ? request.role() : User.UserRole.VIEWER
            )
            .map(UserResponse::fromDomain);
    }

    /**
     * Gets user by ID.
     * GET /api/v1/users/{id}
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponse>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
            .map(UserResponse::fromDomain)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    /**
     * Gets all users.
     * GET /api/v1/users
     */
    @GetMapping
    public Flux<UserResponse> getAllUsers() {
        return userService.getAllUsers()
            .map(UserResponse::fromDomain);
    }

    /**
     * Gets active users only.
     * GET /api/v1/users/active
     */
    @GetMapping("/active")
    public Flux<UserResponse> getActiveUsers() {
        return userService.getActiveUsers()
            .map(UserResponse::fromDomain);
    }

    /**
     * Updates user email.
     * PUT /api/v1/users/{id}/email
     */
    @PutMapping("/{id}/email")
    public Mono<ResponseEntity<UserResponse>> updateUserEmail(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEmailRequest request) {
        return userService.updateUserEmail(id, request.email())
            .map(UserResponse::fromDomain)
            .map(ResponseEntity::ok);
    }

    /**
     * Updates user role (admin only).
     * PUT /api/v1/users/{id}/role
     */
    @PutMapping("/{id}/role")
    public Mono<ResponseEntity<UserResponse>> updateUserRole(
            @PathVariable Long id,
            @RequestParam Long currentUserId,
            @Valid @RequestBody UpdateRoleRequest request) {
        return userService.updateUserRole(id, request.role(), currentUserId)
            .map(UserResponse::fromDomain)
            .map(ResponseEntity::ok);
    }

    /**
     * Activates/deactivates user (admin only).
     * PATCH /api/v1/users/{id}/status
     */
    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<UserResponse>> updateUserStatus(
            @PathVariable Long id,
            @RequestParam Long currentUserId,
            @RequestParam boolean active) {
        return userService.updateUserActiveStatus(id, active, currentUserId)
            .map(UserResponse::fromDomain)
            .map(ResponseEntity::ok);
    }

    /**
     * Deletes user (admin only).
     * DELETE /api/v1/users/{id}
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteUser(
            @PathVariable Long id,
            @RequestParam Long currentUserId) {
        return userService.deleteUser(id, currentUserId)
            .thenReturn(ResponseEntity.noContent().build());
    }

    // ==================== DTO Records ====================

    /**
     * Request to create a user.
     */
    public record CreateUserRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        User.UserRole role
    ) {}

    /**
     * Request to update email.
     */
    public record UpdateEmailRequest(
        @NotBlank @Email @Size(max = 100) String email
    ) {}

    /**
     * Request to update role.
     */
    public record UpdateRoleRequest(
        User.UserRole role
    ) {}

    /**
     * User response DTO.
     */
    public record UserResponse(
        Long id,
        String username,
        String email,
        User.UserRole role,
        boolean isActive,
        java.time.Instant createdAt,
        java.time.Instant updatedAt
    ) {
        public static UserResponse fromDomain(User user) {
            return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt()
            );
        }
    }
}
