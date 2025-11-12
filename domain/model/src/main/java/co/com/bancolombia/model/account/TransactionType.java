package co.com.bancolombia.model.account;

/**
 * TIPOS DE TRANSACCIONES
 *
 * Enum que define los diferentes tipos de operaciones que se pueden realizar en una cuenta.
 *
 * Un ENUM es como una lista fija de opciones posibles.
 * En este caso, una transacción solo puede ser de estos 4 tipos:
 */
public enum TransactionType {

    /** Depósito de dinero en la cuenta */
    DEPOSIT,

    /** Retiro de dinero de la cuenta */
    WITHDRAWAL,

    /** Dinero que ENTRA a la cuenta por una transferencia */
    TRANSFER_IN,

    /** Dinero que SALE de la cuenta por una transferencia */
    TRANSFER_OUT
}
