package com.digitalsignage.domain.screen.domain;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Screen aggregate root - represents a physical display device.
 * Immutable domain model following DDD principles.
 */
@Getter
@Builder
public class Screen {
    
    private final Long id;
    private final String name;
    private final String serialNumber;
    private final Long regionId;
    private final String locationAddress;
    private final BigDecimal latitude;
    private final BigDecimal longitude;
    private final ScreenType screenType;
    private final Integer resolutionWidth;
    private final Integer resolutionHeight;
    private final Orientation orientation;
    private final ScreenStatus status;
    private final Instant lastSeenAt;
    private final String firmwareVersion;
    private final String ipAddress;
    private final String macAddress;
    private final boolean isActive;
    private final Instant createdAt;
    private final Instant updatedAt;

    /**
     * Screen hardware types.
     */
    public enum ScreenType {
        TV, PROJECTOR, LED_WALL
    }

    /**
     * Screen orientation modes.
     */
    public enum Orientation {
        LANDSCAPE, PORTRAIT
    }

    /**
     * Screen operational status.
     */
    public enum ScreenStatus {
        ONLINE, OFFLINE, ERROR, MAINTENANCE
    }

    /**
     * Creates a new screen with default values.
     * Factory method ensuring valid state.
     */
    public static Screen createNew(
            String name, 
            String serialNumber, 
            ScreenType screenType,
            String macAddress) {
        Instant now = Instant.now();
        return Screen.builder()
            .name(name)
            .serialNumber(serialNumber)
            .screenType(screenType != null ? screenType : ScreenType.TV)
            .orientation(Orientation.LANDSCAPE)
            .resolutionWidth(1920)
            .resolutionHeight(1080)
            .status(ScreenStatus.OFFLINE)
            .macAddress(macAddress)
            .isActive(true)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }

    /**
     * Updates screen status (called by screen heartbeat).
     */
    public Screen withStatus(ScreenStatus newStatus) {
        return Screen.builder()
            .id(this.id)
            .name(this.name)
            .serialNumber(this.serialNumber)
            .regionId(this.regionId)
            .locationAddress(this.locationAddress)
            .latitude(this.latitude)
            .longitude(this.longitude)
            .screenType(this.screenType)
            .resolutionWidth(this.resolutionWidth)
            .resolutionHeight(this.resolutionHeight)
            .orientation(this.orientation)
            .status(newStatus)
            .lastSeenAt(Instant.now())
            .firmwareVersion(this.firmwareVersion)
            .ipAddress(this.ipAddress)
            .macAddress(this.macAddress)
            .isActive(this.isActive)
            .createdAt(this.createdAt)
            .updatedAt(Instant.now())
            .build();
    }

    /**
     * Updates screen configuration.
     */
    public Screen withConfiguration(
            Long regionId,
            String locationAddress,
            BigDecimal latitude,
            BigDecimal longitude) {
        return Screen.builder()
            .id(this.id)
            .name(this.name)
            .serialNumber(this.serialNumber)
            .regionId(regionId)
            .locationAddress(locationAddress)
            .latitude(latitude)
            .longitude(longitude)
            .screenType(this.screenType)
            .resolutionWidth(this.resolutionWidth)
            .resolutionHeight(this.resolutionHeight)
            .orientation(this.orientation)
            .status(this.status)
            .lastSeenAt(this.lastSeenAt)
            .firmwareVersion(this.firmwareVersion)
            .ipAddress(this.ipAddress)
            .macAddress(this.macAddress)
            .isActive(this.isActive)
            .createdAt(this.createdAt)
            .updatedAt(Instant.now())
            .build();
    }

    /**
     * Updates network information from heartbeat.
     */
    public Screen withNetworkInfo(String ipAddress, String firmwareVersion) {
        return Screen.builder()
            .id(this.id)
            .name(this.name)
            .serialNumber(this.serialNumber)
            .regionId(this.regionId)
            .locationAddress(this.locationAddress)
            .latitude(this.latitude)
            .longitude(this.longitude)
            .screenType(this.screenType)
            .resolutionWidth(this.resolutionWidth)
            .resolutionHeight(this.resolutionHeight)
            .orientation(this.orientation)
            .status(this.status)
            .lastSeenAt(Instant.now())
            .firmwareVersion(firmwareVersion)
            .ipAddress(ipAddress)
            .macAddress(this.macAddress)
            .isActive(this.isActive)
            .createdAt(this.createdAt)
            .updatedAt(Instant.now())
            .build();
    }

    /**
     * Checks if screen is online.
     */
    public boolean isOnline() {
        return ScreenStatus.ONLINE.equals(this.status);
    }

    /**
     * Gets screen resolution as string (e.g., "1920x1080").
     */
    public String getResolutionString() {
        return resolutionWidth + "x" + resolutionHeight;
    }
}
