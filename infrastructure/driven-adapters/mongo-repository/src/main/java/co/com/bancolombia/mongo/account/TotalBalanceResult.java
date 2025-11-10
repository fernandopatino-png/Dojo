package co.com.bancolombia.mongo.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalBalanceResult {
    private Double totalBalance;
    private Long totalAccounts;
}

