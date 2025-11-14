package co.com.bancolombia.mongo.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountsByBalanceRange {
    private Long range1; //0 - 1000
    private Long range2; //1000 - 5000
    private Long range3; //5000+
}

