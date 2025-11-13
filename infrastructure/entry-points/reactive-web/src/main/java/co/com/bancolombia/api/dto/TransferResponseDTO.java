package co.com.bancolombia.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponseDTO {
    private String transferId;
    private Long fromAccountId;
    private Long toAccountId;
    private Double amount;
    private boolean success;
    private String message;
    private LocalDateTime timestamp;
}

