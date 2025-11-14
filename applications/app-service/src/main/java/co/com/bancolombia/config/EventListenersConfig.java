package co.com.bancolombia.config;

import co.com.bancolombia.model.account.events.AuditListener;
import co.com.bancolombia.model.account.events.NotificationListener;
import co.com.bancolombia.usecase.account.AccountEventUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class EventListenersConfig {

    private final AccountEventUseCase accountEventUseCase;

    @EventListener(ApplicationReadyEvent.class)
    public void registerEventListeners() {
        log.info("📢 Registrando listeners de eventos de cuenta...");
        accountEventUseCase.addListener(new NotificationListener());
        log.info("   ✓ NotificationListener registrado");
        accountEventUseCase.addListener(new AuditListener());
        log.info("   ✓ AuditListener registrado");
        log.info("✅ Total de listeners activos: {}", accountEventUseCase.getListenerCount());
    }
}
