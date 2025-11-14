package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.AccountDTO;
import co.com.bancolombia.api.dto.TransferRequestDTO;
import co.com.bancolombia.api.dto.TransferResponseDTO;
import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.TransferResult;
import co.com.bancolombia.usecase.account.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountManagementUseCase accountManagementUseCase;
    private final TransferUseCase transferUseCase;
    private final AccountSearchUseCase accountSearchUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        log.info("Creating account: {}", accountDTO);
        Account account = toAccount(accountDTO);
        return accountManagementUseCase.createAccount(account)
                .map(this::toDTO);
    }

    @GetMapping("/{id}")
    public Mono<AccountDTO> getAccountById(@PathVariable Long id) {
        log.info("Getting account by id: {}", id);
        return accountManagementUseCase.getAccountById(id)
                .map(this::toDTO);
    }

    @GetMapping("/{id}/cached")
    public Mono<AccountDTO> getAccountByIdCached(@PathVariable Long id) {
        log.info("Getting account by id with cache: {}", id);
        return accountSearchUseCase.findByIdWithCache(id)
                .map(this::toDTO);
    }

    @GetMapping("/owner/{ownerId}")
    public Flux<AccountDTO> getAccountsByOwner(@PathVariable Long ownerId) {
        log.info("Getting accounts by owner: {}", ownerId);
        return accountManagementUseCase.getAccountsByOwner(ownerId)
                .map(this::toDTO);
    }

    @GetMapping
    public Flux<AccountDTO> getAllAccounts() {
        log.info("Getting all accounts");
        return accountManagementUseCase.getAllAccounts()
                .map(this::toDTO);
    }

    @PutMapping("/{id}/balance")
    public Mono<AccountDTO> updateBalance(
            @PathVariable Long id,
            @RequestParam Double newBalance) {
        log.info("Updating balance for account {}: {}", id, newBalance);
        return accountManagementUseCase.updateBalance(id, newBalance)
                .map(this::toDTO);
    }

    @PostMapping("/transfer")
    public Mono<TransferResponseDTO> transfer(@RequestBody TransferRequestDTO request) {
        log.info("Processing transfer: {}", request);
        return transferUseCase.transfer(
                        request.getFromAccountId(),
                        request.getToAccountId(),
                        request.getAmount())
                .map(this::toTransferDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAccount(@PathVariable Long id) {
        log.info("Deleting account: {}", id);
        return accountManagementUseCase.deleteAccount(id);
    }

    @PostMapping("/cache/clear")
    public Mono<Void> clearCache() {
        log.info("Clearing account search cache");
        return accountSearchUseCase.clearCache();
    }

    //Mappers
    private Account toAccount(AccountDTO dto) {
        return Account.builder()
                .id(dto.getId())
                .ownerId(dto.getOwnerId())
                .balance(dto.getBalance())
                .build();
    }

    private AccountDTO toDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .ownerId(account.getOwnerId())
                .balance(account.getBalance())
                .build();
    }

    private TransferResponseDTO toTransferDTO(TransferResult result) {
        return TransferResponseDTO.builder()
                .transferId(result.getTransferId())
                .fromAccountId(result.getFromAccountId())
                .toAccountId(result.getToAccountId())
                .amount(result.getAmount())
                .success(result.isSuccess())
                .message(result.getMessage())
                .timestamp(result.getTimestamp())
                .build();
    }
}

