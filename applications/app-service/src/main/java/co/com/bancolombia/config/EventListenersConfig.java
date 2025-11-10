package co.com.bancolombia.config;

import co.com.bancolombia.model.account.events.AuditListener;
import co.com.bancolombia.model.account.events.NotificationListener;
import co.com.bancolombia.usecase.account.AccountEventUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * Configuración que registra los listeners de eventos al iniciar la aplicación
 * Demuestra Observer Pattern en acción
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class EventListenersConfig {

    private final AccountEventUseCase accountEventUseCase;

    @EventListener(ApplicationReadyEvent.class)
    public void registerEventListeners() {
        log.info("Registering account event listeners...");

        // Registrar listener de notificaciones
        accountEventUseCase.addListener(new NotificationListener());
        log.info("NotificationListener registered");

        // Registrar listener de auditoría
        accountEventUseCase.addListener(new AuditListener());
        log.info("AuditListener registered");

        log.info("All event listeners registered successfully");
    }
}

