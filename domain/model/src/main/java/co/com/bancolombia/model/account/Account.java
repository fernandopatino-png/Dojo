package co.com.bancolombia.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MODELO DE CUENTA BANCARIA
 *
 * Esta clase representa una cuenta bancaria básica.
 * Es parte del DOMINIO (capa más interna de Clean Architecture).
 *
 * ¿Qué contiene?
 * - id: Identificador único de la cuenta
 * - ownerId: ID del usuario dueño de la cuenta
 * - balance: Saldo actual de la cuenta
 *
 * Usa anotaciones de Lombok para generar automáticamente:
 * - @Getter: Crea los métodos getId(), getOwnerId(), getBalance()
 * - @Builder: Permite crear objetos fácilmente
 * - @NoArgsConstructor: Constructor sin parámetros
 * - @AllArgsConstructor: Constructor con todos los parámetros
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Account {

    /**
     * ID único de la cuenta
     * Ejemplo: 1, 2, 3, etc.
     */
    private Long id;

    /**
     * ID del usuario dueño de esta cuenta
     * Ejemplo: Si ownerId = 5, esta cuenta pertenece al usuario con id 5
     */
    private Long ownerId;

    /**
     * Saldo actual en la cuenta
     * Ejemplo: 1500.50 significa $1,500.50
     */
    private Double balance;
}
