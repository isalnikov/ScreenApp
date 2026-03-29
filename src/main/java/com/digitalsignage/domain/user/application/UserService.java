package com.digitalsignage.domain.user.application;

import com.digitalsignage.common.exception.AuthenticationException;
import com.digitalsignage.common.exception.ResourceNotFoundException;
import com.digitalsignage.common.exception.ValidationException;
import com.digitalsignage.domain.user.domain.User;
import com.digitalsignage.domain.user.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Application service for User operations.
 * Contains business logic following DDD and SOLID principles.
 * Single Responsibility: handles user-related use cases.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user.
     * Validates uniqueness of username and email.
     */
    public Mono<User> createUser(String username, String email, String rawPassword, User.UserRole role) {
        return Mono.zip(
                userRepository.existsByUsername(username),
                userRepository.existsByEmail(email)
            )
            .flatMap(tuple -> {
                if (tuple.getT1()) {
                    return Mono.error(new ValidationException("username", "Username already exists"));
                }
                if (tuple.getT2()) {
                    return Mono.error(new ValidationException("email", "Email already exists"));
                }
                
                String passwordHash = passwordEncoder.encode(rawPassword);
                User newUser = User.createNew(username, email, passwordHash, role);
                return userRepository.save(newUser);
            });
    }

    /**
     * Finds user by ID.
     */
    public Mono<User> getUserById(Long id) {
        return userRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", id)));
    }

    /**
     * Finds user by username.
     */
    public Mono<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", username)));
    }

    /**
     * Gets all users.
     */
    public Flux<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Gets active users only.
     */
    public Flux<User> getActiveUsers() {
        return userRepository.findActiveUsers();
    }

    /**
     * Updates user email.
     */
    public Mono<User> updateUserEmail(Long userId, String newEmail) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", userId)))
            .flatMap(user -> 
                userRepository.existsByEmail(newEmail)
                    .flatMap(exists -> {
                        if (exists) {
                            return Mono.error(new ValidationException("email", "Email already exists"));
                        }
                        User updated = user.withEmail(newEmail);
                        return userRepository.save(updated);
                    })
            );
    }

    /**
     * Updates user role (admin only).
     */
    public Mono<User> updateUserRole(Long userId, User.UserRole newRole, Long currentUserId) {
        return userRepository.findById(currentUserId)
            .flatMap(currentUser -> {
                if (!currentUser.isAdmin()) {
                    return Mono.error(new AuthenticationException("Only admins can change roles"));
                }
                return userRepository.findById(userId);
            })
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", userId)))
            .flatMap(user -> {
                User updated = user.withRole(newRole);
                return userRepository.save(updated);
            });
    }

    /**
     * Activates or deactivates user.
     */
    public Mono<User> updateUserActiveStatus(Long userId, boolean active, Long currentUserId) {
        return userRepository.findById(currentUserId)
            .flatMap(currentUser -> {
                if (!currentUser.isAdmin()) {
                    return Mono.error(new AuthenticationException("Only admins can change user status"));
                }
                return userRepository.findById(userId);
            })
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", userId)))
            .flatMap(user -> {
                User updated = user.withActiveStatus(active);
                return userRepository.save(updated);
            });
    }

    /**
     * Deletes user (admin only).
     */
    public Mono<Void> deleteUser(Long userId, Long currentUserId) {
        return userRepository.findById(currentUserId)
            .flatMap(currentUser -> {
                if (!currentUser.isAdmin()) {
                    return Mono.error(new AuthenticationException("Only admins can delete users"));
                }
                // Prevent self-deletion
                if (userId.equals(currentUserId)) {
                    return Mono.error(new ValidationException("Cannot delete yourself"));
                }
                return userRepository.deleteById(userId);
            });
    }

    /**
     * Authenticates user by username and password.
     */
    public Mono<User> authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
            .switchIfEmpty(Mono.error(new AuthenticationException("Invalid credentials")))
            .flatMap(user -> {
                if (!user.isActive()) {
                    return Mono.error(new AuthenticationException("User account is deactivated"));
                }
                if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                    return Mono.error(new AuthenticationException("Invalid credentials"));
                }
                return Mono.just(user);
            });
    }

    /**
     * Changes user password.
     */
    public Mono<User> changePassword(Long userId, String oldPassword, String newPassword) {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", userId)))
            .flatMap(user -> {
                if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
                    return Mono.error(new AuthenticationException("Current password is incorrect"));
                }
                String newHash = passwordEncoder.encode(newPassword);
                // Create new user instance with updated password hash
                User updated = User.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .passwordHash(newHash)
                    .role(user.getRole())
                    .isActive(user.isActive())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(java.time.Instant.now())
                    .build();
                return userRepository.save(updated);
            });
    }
}
