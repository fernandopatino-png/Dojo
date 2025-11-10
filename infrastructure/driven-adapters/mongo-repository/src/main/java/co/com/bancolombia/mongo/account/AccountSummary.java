package co.com.bancolombia.mongo.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummary {
    private Long ownerId;
    private Double totalBalance;
    private Double averageBalance;
    private Double minBalance;
    private Double maxBalance;
    private Long accountCount;
}

