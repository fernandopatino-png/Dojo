package co.com.bancolombia.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;
    private String name;
    private String type;
    private String number;
    private String email;
    private boolean active;

    // Constructor privado para el Builder
    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.number = builder.number;
        this.email = builder.email;
        this.active = builder.active;
    }

    /**
     * Builder Pattern personalizado con validaciones
     */
    public static class Builder {
        private Long id;
        private String name;
        private String type;
        private String number;
        private String email;
        private boolean active = true;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder number(String number) {
            this.number = number;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public User build() {
            // Validaciones antes de construir
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be empty");
            }
            if (email == null || !email.contains("@")) {
                throw new IllegalArgumentException("Invalid email format");
            }
            return new User(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
