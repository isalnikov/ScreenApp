# Объектная модель (Java 21, Spring Boot 3.0)

## Пакет: com.screenapp.model

### 1. User.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false, length = 255)
    private String passwordHash;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "uploadedBy", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Content> uploadedContents = new HashSet<>();
    
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Playlist> createdPlaylists = new HashSet<>();
    
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Schedule> createdSchedules = new HashSet<>();
    
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ScreenGroup> createdGroups = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum Role {
        ADMIN, CONTENT_MANAGER, VIEWER
    }
}
```

### 2. Region.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "regions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(unique = true, nullable = false, length = 10)
    private String code;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_region_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Region parentRegion;
    
    @OneToMany(mappedBy = "parentRegion", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Region> childRegions = new HashSet<>();
    
    @OneToMany(mappedBy = "region", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Screen> screens = new HashSet<>();
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### 3. Screen.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "screens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Screen {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(unique = true, nullable = false, length = 50)
    private String serialNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Region region;
    
    @Column(columnDefinition = "TEXT")
    private String locationAddress;
    
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ScreenType screenType = ScreenType.TV;
    
    private Integer resolutionWidth = 1920;
    
    private Integer resolutionHeight = 1080;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Orientation orientation = Orientation.LANDSCAPE;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private ScreenStatus status = ScreenStatus.OFFLINE;
    
    private LocalDateTime lastSeenAt;
    
    @Column(length = 20)
    private String firmwareVersion;
    
    @Column(columnDefinition = "INET")
    private InetAddress ipAddress;
    
    @Column(length = 17)
    private String macAddress;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "screen", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ScreenSchedule> screenSchedules = new HashSet<>();
    
    @OneToMany(mappedBy = "screen", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ScreenGroupMember> groupMemberships = new HashSet<>();
    
    @OneToMany(mappedBy = "screen", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ContentLog> contentLogs = new HashSet<>();
    
    @OneToMany(mappedBy = "screen", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ScreenStat> statistics = new HashSet<>();
    
    @OneToMany(mappedBy = "screen", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ApiToken> apiTokens = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum ScreenType {
        TV, PROJECTOR, LED_WALL
    }
    
    public enum Orientation {
        LANDSCAPE, PORTRAIT
    }
    
    public enum ScreenStatus {
        ONLINE, OFFLINE, ERROR, MAINTENANCE
    }
}
```

### 4. ContentType.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "content_types")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true, nullable = false, length = 50)
    private String name;
    
    @Column(length = 255)
    private String mimeTypePattern;
    
    @OneToMany(mappedBy = "contentType", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Content> contents = new HashSet<>();
    
    public enum ContentTypeEnum {
        IMAGE, VIDEO, TEXT, HTML, STREAM
    }
}
```

### 5. Content.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "content")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_type_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ContentType contentType;
    
    @Column(nullable = false, length = 500)
    private String filePath;
    
    private Long fileSize;
    
    private Integer durationSeconds;
    
    @Column(columnDefinition = "TEXT")
    private String textContent;
    
    @Column(columnDefinition = "TEXT")
    private String htmlContent;
    
    @Column(length = 500)
    private String thumbnailPath;
    
    @Column(length = 500)
    private String url;
    
    @Column(length = 64)
    private String checksum;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User uploadedBy;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<PlaylistItem> playlistItems = new HashSet<>();
    
    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ContentLog> contentLogs = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

### 6. Playlist.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "playlists")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Playlist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    private Integer totalDurationSeconds = 0;
    
    private Integer version = 1;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "playlist", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<PlaylistItem> items = new ArrayList<>();
    
    @OneToMany(mappedBy = "playlist", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Schedule> schedules = new HashSet<>();
    
    @OneToMany(mappedBy = "playlist", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ContentLog> contentLogs = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void addItem(PlaylistItem item) {
        items.add(item);
        item.setPlaylist(this);
        recalculateDuration();
    }
    
    public void removeItem(PlaylistItem item) {
        items.remove(item);
        item.setPlaylist(null);
        recalculateDuration();
    }
    
    private void recalculateDuration() {
        this.totalDurationSeconds = items.stream()
            .mapToInt(item -> item.getDurationSeconds() != null ? 
                item.getDurationSeconds() : 
                (item.getContent() != null ? item.getContent().getDurationSeconds() : 0))
            .sum();
    }
}
```

### 7. PlaylistItem.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "playlist_items", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"playlist_id", "position"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaylistItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Playlist playlist;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Content content;
    
    @Column(nullable = false)
    private Integer position;
    
    private Integer durationSeconds;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TransitionType transitionType = TransitionType.NONE;
    
    private Integer transitionDuration = 0;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum TransitionType {
        NONE, FADE, SLIDE
    }
}
```

### 8. Schedule.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Playlist playlist;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    @Column(nullable = false)
    private LocalTime startTime = LocalTime.MIN;
    
    @Column(nullable = false)
    private LocalTime endTime = LocalTime.MAX;
    
    @Column(length = 20)
    private String daysOfWeek; // bitmask или '*'
    
    private Integer priority = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<ScreenSchedule> screenSchedules = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public boolean isActiveOn(LocalDate date) {
        if (!isActive) return false;
        if (date.isBefore(startDate)) return false;
        if (endDate != null && date.isAfter(endDate)) return false;
        
        if ("*".equals(daysOfWeek)) return true;
        
        int dayOfWeek = date.getDayOfWeek().getValue(); // 1-7 (Пн-Вс)
        return daysOfWeek != null && daysOfWeek.contains(String.valueOf(dayOfWeek));
    }
}
```

### 9. ScreenSchedule.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "screen_schedules",
       uniqueConstraints = @UniqueConstraint(columnNames = {"screen_id", "schedule_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Screen screen;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Schedule schedule;
    
    @Column(updatable = false)
    private LocalDateTime assignedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User assignedBy;
    
    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
    }
}
```

### 10. ScreenGroup.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screen_groups")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenGroup {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User createdBy;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<ScreenGroupMember> members = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public void addScreen(Screen screen) {
        ScreenGroupMember member = ScreenGroupMember.builder()
            .group(this)
            .screen(screen)
            .build();
        members.add(member);
    }
    
    public void removeScreen(Screen screen) {
        members.removeIf(m -> m.getScreen().equals(screen));
    }
}
```

### 11. ScreenGroupMember.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "screen_group_members",
       uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "screen_id"}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenGroupMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ScreenGroup group;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Screen screen;
    
    @Column(updatable = false)
    private LocalDateTime addedAt;
    
    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
    }
}
```

### 12. ContentLog.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "content_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Screen screen;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Content content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playlist_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Playlist playlist;
    
    @Column(nullable = false)
    private LocalDateTime startedAt;
    
    private LocalDateTime endedAt;
    
    private Integer durationSeconds;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private LogStatus status = LogStatus.PLAYED;
    
    @Column(columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public enum LogStatus {
        PLAYED, ERROR, SKIPPED, INTERRUPTED
    }
}
```

### 13. ScreenStat.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "screen_stats")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScreenStat {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Screen screen;
    
    @Column(nullable = false)
    private LocalDateTime reportedAt;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal cpuUsage;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal memoryUsage;
    
    private Integer storageFreeGb;
    
    @Column(precision = 8, scale = 2)
    private BigDecimal networkUploadMbps;
    
    @Column(precision = 8, scale = 2)
    private BigDecimal networkDownloadMbps;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal temperature;
    
    private Long uptimeSeconds;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_playlist_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Playlist currentPlaylist;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_content_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Content currentContent;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PlaybackStatus playbackStatus;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> additionalData = new HashMap<>();
    
    public enum PlaybackStatus {
        PLAYING, PAUSED, STOPPED, BUFFERING
    }
}
```

### 14. AuditLog.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "audit_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;
    
    @Column(nullable = false, length = 50)
    private String action;
    
    @Column(nullable = false, length = 50)
    private String entityType;
    
    private Long entityId;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> oldValues;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> newValues;
    
    @Column(columnDefinition = "INET")
    private InetAddress ipAddress;
    
    @Column(columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### 15. ApiToken.java
```java
package com.screenapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Screen screen;
    
    @Column(unique = true, nullable = false, length = 64)
    private String tokenHash;
    
    @Column(length = 200)
    private String description;
    
    private LocalDateTime expiresAt;
    
    private LocalDateTime lastUsedAt;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isValid() {
        return isActive && !isExpired();
    }
}
```

## DTO классы (com.screenapp.dto)

Для каждого entity создаются соответствующие DTO для API:
- UserDTO
- ScreenDTO
- ContentDTO
- PlaylistDTO
- PlaylistItemDTO
- ScheduleDTO
- RegionDTO
- ScreenGroupDTO
- ContentLogDTO
- ScreenStatDTO
- ApiTokenDTO

## Исключения (com.screenapp.exception)

```java
// ResourceNotFoundException.java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

// BusinessException.java
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}

// ValidationException.java
public class ValidationException extends RuntimeException {
    private final Map<String, String> errors;
    
    public ValidationException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }
}
```
