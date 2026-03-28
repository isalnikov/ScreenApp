package com.digitalsignage.domain.user.infrastructure;

import com.digitalsignage.domain.user.domain.User;
import com.digitalsignage.domain.user.port.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * R2DBC implementation of UserRepositoryPort.
 * Reactive database access following Hexagonal Architecture.
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryPort {

    private final DatabaseClient databaseClient;

    @Override
    public Mono<User> save(User user) {
        if (user.getId() == null) {
            return create(user);
        } else {
            return update(user);
        }
    }

    private Mono<User> create(User user) {
        String sql = """
            INSERT INTO users (username, email, password_hash, role, is_active, created_at, updated_at)
            VALUES (:username, :email, :passwordHash, :role, :isActive, :createdAt, :updatedAt)
            RETURNING id, username, email, password_hash, role, is_active, created_at, updated_at
            """;

        Map<String, Object> params = new HashMap<>();
        params.put("username", user.getUsername());
        params.put("email", user.getEmail());
        params.put("passwordHash", user.getPasswordHash());
        params.put("role", user.getRole().name());
        params.put("isActive", user.isActive());
        params.put("createdAt", user.getCreatedAt());
        params.put("updatedAt", user.getUpdatedAt());

        return databaseClient.sql(sql)
            .bindValues(params)
            .map(this::mapRowToUser)
            .one();
    }

    private Mono<User> update(User user) {
        String sql = """
            UPDATE users 
            SET username = :username, email = :email, password_hash = :passwordHash, 
                role = :role, is_active = :isActive, updated_at = :updatedAt
            WHERE id = :id
            RETURNING id, username, email, password_hash, role, is_active, created_at, updated_at
            """;

        Map<String, Object> params = new HashMap<>();
        params.put("id", user.getId());
        params.put("username", user.getUsername());
        params.put("email", user.getEmail());
        params.put("passwordHash", user.getPasswordHash());
        params.put("role", user.getRole().name());
        params.put("isActive", user.isActive());
        params.put("updatedAt", user.getUpdatedAt());

        return databaseClient.sql(sql)
            .bindValues(params)
            .map(this::mapRowToUser)
            .one();
    }

    @Override
    public Mono<User> findById(Long id) {
        String sql = """
            SELECT id, username, email, password_hash, role, is_active, created_at, updated_at
            FROM users WHERE id = :id
            """;
        return databaseClient.sql(sql)
            .bind("id", id)
            .map(this::mapRowToUser)
            .one();
    }

    @Override
    public Mono<User> findByUsername(String username) {
        String sql = """
            SELECT id, username, email, password_hash, role, is_active, created_at, updated_at
            FROM users WHERE username = :username
            """;
        return databaseClient.sql(sql)
            .bind("username", username)
            .map(this::mapRowToUser)
            .one();
    }

    @Override
    public Mono<User> findByEmail(String email) {
        String sql = """
            SELECT id, username, email, password_hash, role, is_active, created_at, updated_at
            FROM users WHERE email = :email
            """;
        return databaseClient.sql(sql)
            .bind("email", email)
            .map(this::mapRowToUser)
            .one();
    }

    @Override
    public Mono<Boolean> existsByUsername(String username) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE username = :username)";
        return databaseClient.sql(sql)
            .bind("username", username)
            .map((row, metadata) -> row.get(0, Boolean.class))
            .one();
    }

    @Override
    public Mono<Boolean> existsByEmail(String email) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)";
        return databaseClient.sql(sql)
            .bind("email", email)
            .map((row, metadata) -> row.get(0, Boolean.class))
            .one();
    }

    @Override
    public Flux<User> findAll() {
        String sql = """
            SELECT id, username, email, password_hash, role, is_active, created_at, updated_at
            FROM users ORDER BY id
            """;
        return databaseClient.sql(sql)
            .map(this::mapRowToUser)
            .all();
    }

    @Override
    public Flux<User> findByRole(User.UserRole role) {
        String sql = """
            SELECT id, username, email, password_hash, role, is_active, created_at, updated_at
            FROM users WHERE role = :role ORDER BY id
            """;
        return databaseClient.sql(sql)
            .bind("role", role.name())
            .map(this::mapRowToUser)
            .all();
    }

    @Override
    public Flux<User> findActiveUsers() {
        String sql = """
            SELECT id, username, email, password_hash, role, is_active, created_at, updated_at
            FROM users WHERE is_active = true ORDER BY id
            """;
        return databaseClient.sql(sql)
            .map(this::mapRowToUser)
            .all();
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id = :id";
        return databaseClient.sql(sql)
            .bind("id", id)
            .then();
    }

    private User mapRowToUser(org.springframework.r2dbc.core.Row row, org.springframework.r2dbc.core.RowMetadata metadata) {
        return User.builder()
            .id(row.get("id", Long.class))
            .username(row.get("username", String.class))
            .email(row.get("email", String.class))
            .passwordHash(row.get("password_hash", String.class))
            .role(User.UserRole.valueOf(row.get("role", String.class)))
            .isActive(row.get("is_active", Boolean.class))
            .createdAt(getInstant(row.get("created_at")))
            .updatedAt(getInstant(row.get("updated_at")))
            .build();
    }

    private Instant getInstant(Object obj) {
        if (obj == null) return null;
        if (obj instanceof java.time.LocalDateTime ldt) {
            return ldt.atZone(java.time.ZoneId.systemDefault()).toInstant();
        }
        if (obj instanceof java.time.Instant instant) {
            return instant;
        }
        return null;
    }
}
