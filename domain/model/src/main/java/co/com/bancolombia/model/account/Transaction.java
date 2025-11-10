
    private String id;
    private Long accountId;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime timestamp;
    private String description;
}
package co.com.bancolombia.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Transaction {

