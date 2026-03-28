package com.digitalsignage.domain.user.port;

import com.digitalsignage.domain.user.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository port for User aggregate.
 * Defines the contract for user data access following Hexagonal Architecture.
 */
public interface UserRepositoryPort {
    
    /**
     * Saves a user (create or update).
     */
    Mono<User> save(User user);
    
    /**
     * Finds user by ID.
     */
    Mono<User> findById(Long id);
    
    /**
     * Finds user by username.
     */
    Mono<User> findByUsername(String username);
    
    /**
     * Finds user by email.
     */
    Mono<User> findByEmail(String email);
    
    /**
     * Checks if username exists.
     */
    Mono<Boolean> existsByUsername(String username);
    
    /**
     * Checks if email exists.
     */
    Mono<Boolean> existsByEmail(String email);
    
    /**
     * Finds all users.
     */
    Flux<User> findAll();
    
    /**
     * Finds users by role.
     */
    Flux<User> findByRole(User.UserRole role);
    
    /**
     * Finds active users.
     */
    Flux<User> findActiveUsers();
    
    /**
     * Deletes user by ID.
     */
    Mono<Void> deleteById(Long id);
}
