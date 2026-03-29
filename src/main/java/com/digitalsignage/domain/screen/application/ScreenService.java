package com.digitalsignage.domain.screen.application;

import com.digitalsignage.common.exception.ResourceNotFoundException;
import com.digitalsignage.common.exception.ValidationException;
import com.digitalsignage.domain.screen.domain.Screen;
import com.digitalsignage.domain.screen.port.ScreenRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Application service for Screen operations.
 * Contains business logic for screen management.
 */
@Service
@RequiredArgsConstructor
public class ScreenService {

    private final ScreenRepositoryPort screenRepository;

    /**
     * Registers a new screen.
     */
    public Mono<Screen> registerScreen(
            String name,
            String serialNumber,
            Screen.ScreenType screenType,
            String macAddress) {
        return screenRepository.existsBySerialNumber(serialNumber)
            .flatMap(exists -> {
                if (exists) {
                    return Mono.error(new ValidationException("serialNumber", "Screen with this serial number already exists"));
                }
                Screen newScreen = Screen.createNew(name, serialNumber, screenType, macAddress);
                return screenRepository.save(newScreen);
            });
    }

    /**
     * Gets screen by ID.
     */
    public Mono<Screen> getScreenById(Long id) {
        return screenRepository.findById(id)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Screen", id)));
    }

    /**
     * Gets screen by serial number.
     */
    public Mono<Screen> getScreenBySerialNumber(String serialNumber) {
        return screenRepository.findBySerialNumber(serialNumber)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Screen", serialNumber)));
    }

    /**
     * Gets all screens.
     */
    public Flux<Screen> getAllScreens() {
        return screenRepository.findAll();
    }

    /**
     * Gets screens by region.
     */
    public Flux<Screen> getScreensByRegion(Long regionId) {
        return screenRepository.findByRegionId(regionId);
    }

    /**
     * Gets online screens only.
     */
    public Flux<Screen> getOnlineScreens() {
        return screenRepository.findOnlineScreens();
    }

    /**
     * Gets active screens only.
     */
    public Flux<Screen> getActiveScreens() {
        return screenRepository.findActiveScreens();
    }

    /**
     * Updates screen status (heartbeat).
     */
    public Mono<Screen> updateScreenStatus(Long screenId, Screen.ScreenStatus status) {
        return screenRepository.findById(screenId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Screen", screenId)))
            .flatMap(screen -> {
                Screen updated = screen.withStatus(status);
                return screenRepository.save(updated);
            });
    }

    /**
     * Updates screen network info from heartbeat.
     */
    public Mono<Screen> updateScreenNetworkInfo(Long screenId, String ipAddress, String firmwareVersion) {
        return screenRepository.findById(screenId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Screen", screenId)))
            .flatMap(screen -> {
                Screen updated = screen.withNetworkInfo(ipAddress, firmwareVersion);
                return screenRepository.save(updated);
            });
    }

    /**
     * Updates screen configuration.
     */
    public Mono<Screen> updateScreenConfiguration(
            Long screenId,
            Long regionId,
            String locationAddress,
            java.math.BigDecimal latitude,
            java.math.BigDecimal longitude) {
        return screenRepository.findById(screenId)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Screen", screenId)))
            .flatMap(screen -> {
                Screen updated = screen.withConfiguration(regionId, locationAddress, latitude, longitude);
                return screenRepository.save(updated);
            });
    }

    /**
     * Deletes screen.
     */
    public Mono<Void> deleteScreen(Long screenId) {
        return screenRepository.deleteById(screenId);
    }

    /**
     * Authenticates screen by serial number and MAC address.
     */
    public Mono<Screen> authenticateScreen(String serialNumber, String macAddress) {
        return screenRepository.findBySerialNumber(serialNumber)
            .switchIfEmpty(Mono.error(new ResourceNotFoundException("Screen", serialNumber)))
            .flatMap(screen -> {
                if (!screen.isActive()) {
                    return Mono.error(new ValidationException("Screen is deactivated"));
                }
                if (!screen.getMacAddress().equals(macAddress)) {
                    return Mono.error(new ValidationException("MAC address mismatch"));
                }
                return Mono.just(screen);
            });
    }
}
