package co.com.bancolombia.mongo.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MODELO DE DATOS: Usuario en MongoDB
 *
 * Esta clase representa cómo se guarda un usuario en la base de datos.
 * Es diferente de la clase User del dominio porque:
 * - Esta tiene anotaciones de MongoDB (@Document, @Id)
 * - El dominio no debe conocer detalles de la BD
 *
 * @Document: Indica que esto se guarda como un documento en MongoDB
 * @Id: Marca el campo que será el identificador único (_id en MongoDB)
 */
@Data
@Document
@NoArgsConstructor
public class UserData {

    /** ID único del usuario (se mapea a _id en MongoDB) */
    @Id
    private Long id;

    /** Nombre completo del usuario */
    private String name;

    /** Tipo de usuario (BASIC, PREMIUM, VIP) */
    private String type;

    /** Número de documento */
    private String number;

    /** Email del usuario */
    private String email;

    /** Indica si el usuario está activo */
    private boolean active;
}
