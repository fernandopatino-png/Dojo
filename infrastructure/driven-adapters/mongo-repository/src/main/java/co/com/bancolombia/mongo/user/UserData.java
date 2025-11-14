package co.com.bancolombia.mongo.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
public class UserData {

    @Id
    private Long id;
    private String name;
    private String type;
    private String number;
    private String email;
    private boolean active;
}
