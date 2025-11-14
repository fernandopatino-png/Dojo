package co.com.bancolombia.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResult {

    private String transferId;
    private Long fromAccountId;
    private Long toAccountId;
    private Double amount;
    private boolean success;
    private String message;
    private LocalDateTime timestamp;

    public static TransferResult success(String transferId, Long fromAccountId, Long toAccountId, Double amount) {
        return TransferResult.builder()
            .transferId(transferId)
            .fromAccountId(fromAccountId)
            .toAccountId(toAccountId)
            .amount(amount)
            .success(true)
            .message("Transferencia completada exitosamente")
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static TransferResult failure(Long fromAccountId, Long toAccountId, Double amount, String reason) {
        return TransferResult.builder()
            .fromAccountId(fromAccountId)
            .toAccountId(toAccountId)
            .amount(amount)
            .success(false)
            .message(reason)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
