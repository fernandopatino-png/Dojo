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

    private User(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.type = builder.type;
        this.number = builder.number;
        this.email = builder.email;
        this.active = builder.active;
    }

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
            //Validar nombre
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }
            //Validar email
            if (email == null || !email.contains("@")) {
                throw new IllegalArgumentException("El email debe tener formato válido (contener @)");
            }
            return new User(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
