package co.com.bancolombia.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * MODELO DE TRANSACCIÓN
 *
 * Representa una operación realizada en una cuenta (depósito, retiro, transferencia).
 *
 * ESTRUCTURA DE DATOS: Se usará en una Deque (cola de doble extremo)
 * para mantener un historial de las últimas transacciones.
 *
 * Ejemplo de transacción:
 * - ID: "trans-001"
 * - Cuenta: 123
 * - Tipo: DEPOSIT (depósito)
 * - Monto: $500
 * - Fecha: 2025-11-10 14:30:00
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Transaction {

    /** Identificador único de la transacción */
    private String id;

    /** ID de la cuenta donde se realizó la transacción */
    private Long accountId;

    /** Monto de la transacción (positivo para depósitos, negativo para retiros) */
    private Double amount;

    /** Tipo de transacción: DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT */
    private TransactionType type;

    /** Fecha y hora en que se realizó la transacción */
    private LocalDateTime timestamp;

    /** Descripción opcional de la transacción */
    private String description;
}
