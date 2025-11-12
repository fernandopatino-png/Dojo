package co.com.bancolombia.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RESULTADO DE UNA TRANSFERENCIA
 *
 * Esta clase encapsula el resultado de una operación de transferencia.
 *
 * ¿Por qué usar esta clase?
 * - Permite retornar múltiples datos de una vez
 * - Indica si la operación fue exitosa o falló
 * - Incluye un mensaje descriptivo
 *
 * Es mejor retornar esto que solo "true/false" porque tenemos más información.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResult {

    /** ID único de la transferencia */
    private String transferId;

    /** ID de la cuenta origen (de donde sale el dinero) */
    private Long fromAccountId;

    /** ID de la cuenta destino (a donde llega el dinero) */
    private Long toAccountId;

    /** Monto transferido */
    private Double amount;

    /** true si la transferencia fue exitosa, false si falló */
    private boolean success;

    /** Mensaje descriptivo (éxito o razón del error) */
    private String message;

    /** Fecha y hora de la transferencia */
    private LocalDateTime timestamp;

    /**
     * MÉTODO FACTORY PARA TRANSFERENCIA EXITOSA
     *
     * Un método "factory" es un método que crea objetos de forma conveniente.
     * Este crea un TransferResult para indicar éxito.
     *
     * @param transferId ID de la transferencia
     * @param fromAccountId Cuenta origen
     * @param toAccountId Cuenta destino
     * @param amount Monto transferido
     * @return TransferResult indicando éxito
     */
    public static TransferResult success(String transferId, Long fromAccountId, Long toAccountId, Double amount) {
        return TransferResult.builder()
            .transferId(transferId)
            .fromAccountId(fromAccountId)
            .toAccountId(toAccountId)
            .amount(amount)
            .success(true)
            .message("Transferencia completada exitosamente")
            .timestamp(LocalDateTime.now())
            .build();
    }

    /**
     * MÉTODO FACTORY PARA TRANSFERENCIA FALLIDA
     *
     * Crea un TransferResult para indicar que la transferencia falló.
     *
     * @param fromAccountId Cuenta origen
     * @param toAccountId Cuenta destino
     * @param amount Monto que se intentó transferir
     * @param reason Razón por la que falló
     * @return TransferResult indicando falla
     */
    public static TransferResult failure(Long fromAccountId, Long toAccountId, Double amount, String reason) {
        return TransferResult.builder()
            .fromAccountId(fromAccountId)
            .toAccountId(toAccountId)
            .amount(amount)
            .success(false)
            .message(reason)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
