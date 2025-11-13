package co.com.bancolombia.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MODELO DE USUARIO
 *
 * Representa un usuario del sistema bancario.
 *
 * PATRÓN DE DISEÑO: Builder Pattern
 * ¿Por qué? Permite crear usuarios de forma segura con validaciones.
 *
 * Ejemplo de uso:
 * User user = User.builder()
 *     .name("Juan Perez")
 *     .email("juan@mail.com")
 *     .build();
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /** Identificador único del usuario */
    private String id;

    /** Nombre completo del usuario */
    private String name;

    /** Tipo de usuario (BASIC, PREMIUM, VIP) */
    private String type;

    /** Número de documento del usuario */
    private String number;

    /** Email del usuario */
    private String email;

    /** Indica si el usuario está activo en el sistema */
    private boolean active;

    /**
     * Constructor privado usado por el Builder
     * Solo el Builder puede crear instancias de User
     */
    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.number = builder.number;
        this.email = builder.email;
        this.active = builder.active;
    }

    /**
     * PATRÓN BUILDER - Clase interna para construir usuarios
     *
     * Ventajas:
     * 1. Código más legible
     * 2. Validaciones antes de crear el objeto
     * 3. Valores por defecto automáticos
     * 4. No necesitas recordar el orden de parámetros
     */
    public static class Builder {
        private String id;
        private String name;
        private String type;
        private String number;
        private String email;
        private boolean active = true; // Valor por defecto

        /**
         * Asigna el ID del usuario
         * @param id Identificador único
         * @return El mismo builder para encadenar métodos
         */
        public Builder id(String id) {
            this.id = id;
            return this;
        }

        /**
         * Asigna el nombre del usuario
         * @param name Nombre completo
         * @return El mismo builder
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * Asigna el tipo de usuario
         * @param type BASIC, PREMIUM o VIP
         * @return El mismo builder
         */
        public Builder type(String type) {
            this.type = type;
            return this;
        }

        /**
         * Asigna el número de documento
         * @param number Número de identificación
         * @return El mismo builder
         */
        public Builder number(String number) {
            this.number = number;
            return this;
        }

        /**
         * Asigna el email del usuario
         * @param email Correo electrónico
         * @return El mismo builder
         */
        public Builder email(String email) {
            this.email = email;
            return this;
        }

        /**
         * Asigna si el usuario está activo
         * @param active true = activo, false = inactivo
         * @return El mismo builder
         */
        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        /**
         * Construye el objeto User después de validar los datos
         *
         * VALIDACIONES:
         * - El nombre no puede estar vacío
         * - El email debe contener @
         *
         * @return Usuario creado
         * @throws IllegalArgumentException si los datos son inválidos
         */
        public User build() {
            // Validar nombre
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }

            // Validar email
            if (email == null || !email.contains("@")) {
                throw new IllegalArgumentException("El email debe tener formato válido (contener @)");
            }

            // Si todo está bien, crear el usuario
            return new User(this);
        }
    }

    /**
     * Método estático para iniciar la construcción de un usuario
     * @return Nueva instancia del Builder
     */
    public static Builder builder() {
        return new Builder();
    }
}
