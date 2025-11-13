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

/**
 * REST Controller para operaciones de cuentas
 * Demuestra Clean Architecture: Entry Point que usa Use Cases
 */
@Slf4j
@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountManagementUseCase accountManagementUseCase;
    private final TransferUseCase transferUseCase;
    private final AccountSearchUseCase accountSearchUseCase;
    private final TransactionHistoryUseCase transactionHistoryUseCase;

    /**
     * Crear una nueva cuenta
     * POST /api/accounts
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        log.info("Creating account: {}", accountDTO);
        Account account = toAccount(accountDTO);
        return accountManagementUseCase.createAccount(account)
            .map(this::toDTO);
    }

    /**
     * Obtener cuenta por ID
     * GET /api/accounts/{id}
     */
    @GetMapping("/{id}")
    public Mono<AccountDTO> getAccountById(@PathVariable ("id")  Long id) {
        log.info("Getting account by id: {}", id);
        return accountManagementUseCase.getAccountById(id)
            .map(this::toDTO);
    }

    /**
     * Obtener cuenta por ID con cache (optimizado O(1))
     * GET /api/accounts/{id}/cached
     */
    @GetMapping("/{id}/cached")
    public Mono<AccountDTO> getAccountByIdCached(@PathVariable ("id")  Long id) {
        log.info("Getting account by id with cache: {}", id);
        return accountSearchUseCase.findByIdWithCache(id)
            .map(this::toDTO);
    }

    /**
     * Obtener cuentas por propietario
     * GET /api/accounts/owner/{ownerId}
     */
    @GetMapping("/owner/{ownerId}")
    public Flux<AccountDTO> getAccountsByOwner(@PathVariable ("ownerId") Long ownerId) {
        log.info("Getting accounts by owner: {}", ownerId);
        return accountManagementUseCase.getAccountsByOwner(ownerId)
            .map(this::toDTO);
    }

    /**
     * Listar todas las cuentas
     * GET /api/accounts
     */
    @GetMapping
    public Flux<AccountDTO> getAllAccounts() {
        log.info("Getting all accounts");
        return accountManagementUseCase.getAllAccounts()
            .map(this::toDTO);
    }

    /**
     * Actualizar saldo de cuenta
     * PUT /api/accounts/{id}/balance
     */
    @PutMapping("/{id}/balance")
    public Mono<AccountDTO> updateBalance(
            @PathVariable ("id") Long id,
            @RequestParam ("newBalance") Double newBalance) {
        log.info("Updating balance for account {}: {}", id, newBalance);
        return accountManagementUseCase.updateBalance(id, newBalance)
            .map(this::toDTO);
    }

    /**
     * Realizar transferencia entre cuentas
     * POST /api/accounts/transfer
     */
    @PostMapping("/transfer")
    public Mono<TransferResponseDTO> transfer(@RequestBody TransferRequestDTO request) {
        log.info("Processing transfer: {}", request);
        return transferUseCase.transfer(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount())
            .map(this::toTransferDTO);
    }

    /**
     * Eliminar cuenta
     * DELETE /api/accounts/{id}
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAccount(@PathVariable ("id") Long id) {
        log.info("Deleting account: {}", id);
        return accountManagementUseCase.deleteAccount(id);
    }

    /**
     * Limpiar cache de búsquedas
     * POST /api/accounts/cache/clear
     */
    @PostMapping("/cache/clear")
    public Mono<Void> clearCache() {
        log.info("Clearing account search cache");
        return accountSearchUseCase.clearCache();
    }

    // Mappers
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

