package com.digitalsignage.domain.screen.port;

import com.digitalsignage.domain.screen.domain.Screen;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository port for Screen aggregate.
 * Defines the contract for screen data access.
 */
public interface ScreenRepositoryPort {
    
    Mono<Screen> save(Screen screen);
    Mono<Screen> findById(Long id);
    Mono<Screen> findBySerialNumber(String serialNumber);
    Mono<Screen> findByMacAddress(String macAddress);
    Mono<Boolean> existsBySerialNumber(String serialNumber);
    Flux<Screen> findAll();
    Flux<Screen> findByRegionId(Long regionId);
    Flux<Screen> findByStatus(Screen.ScreenStatus status);
    Flux<Screen> findActiveScreens();
    Flux<Screen> findOnlineScreens();
    Mono<Void> deleteById(Long id);
}
