package com.digitalsignage.domain.user.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * User aggregate root - represents a user in the system.
 * Immutable domain model following DDD principles.
 */
@Getter
@Builder
public class User {
    
    private final Long id;
    private final String username;
    private final String email;
    private final String passwordHash;
    private final UserRole role;
    private final boolean isActive;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * User roles following principle of least privilege.
     */
    public enum UserRole {
        ADMIN,      // Full system access
        CONTENT_MANAGER, // Can manage content and playlists
        VIEWER      // Read-only access
    }

    /**
     * Creates a new user with default values.
     * Factory method ensuring valid state.
     */
    public static User createNew(String username, String email, String passwordHash, UserRole role) {
        Instant now = Instant.now();
        return User.builder()
            .username(username)
            .email(email)
            .passwordHash(passwordHash)
            .role(role != null ? role : UserRole.VIEWER)
            .isActive(true)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    /**
     * Updates user email.
     * Returns new instance (immutability).
     */
    public User withEmail(String newEmail) {
        return User.builder()
            .id(this.id)
            .username(this.username)
            .email(newEmail)
            .passwordHash(this.passwordHash)
            .role(this.role)
            .isActive(this.isActive)
            .createdAt(this.createdAt)
            .updatedAt(Instant.now())
            .build();
    }

    /**
     * Updates user role.
     * Only admins can change roles (enforced at service layer).
     */
    public User withRole(UserRole newRole) {
        return User.builder()
            .id(this.id)
            .username(this.username)
            .email(this.email)
            .passwordHash(this.passwordHash)
            .role(newRole)
            .isActive(this.isActive)
            .createdAt(this.createdAt)
            .updatedAt(Instant.now())
            .build();
    }

    /**
     * Activates or deactivates user.
     */
    public User withActiveStatus(boolean active) {
        return User.builder()
            .id(this.id)
            .username(this.username)
            .email(this.email)
            .passwordHash(this.passwordHash)
            .role(this.role)
            .isActive(active)
            .createdAt(this.createdAt)
            .updatedAt(Instant.now())
            .build();
    }

    /**
     * Checks if user has admin role.
     */
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.role);
    }

    /**
     * Checks if user has content manager or admin role.
     */
    public boolean canManageContent() {
        return UserRole.ADMIN.equals(this.role) || UserRole.CONTENT_MANAGER.equals(this.role);
    }
}
