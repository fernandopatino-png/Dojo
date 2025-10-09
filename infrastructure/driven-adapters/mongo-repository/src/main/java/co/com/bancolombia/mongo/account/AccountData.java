package co.com.bancolombia.mongo.account;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
public class AccountData {

    @Id
    private Long id;
    private Long ownerId;
    private Double balance;
}
