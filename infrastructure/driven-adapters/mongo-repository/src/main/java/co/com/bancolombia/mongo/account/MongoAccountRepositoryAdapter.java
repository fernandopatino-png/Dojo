package co.com.bancolombia.mongo.account;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.gateways.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Implementación completa del AccountRepository usando MongoDB Reactivo
 * Demuestra operaciones CRUD optimizadas con índices
 */
@Repository
@RequiredArgsConstructor
public class MongoAccountRepositoryAdapter implements AccountRepository {

    private final ReactiveMongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Account> getAccountById(Long id) {
        return mongoTemplate.findById(id, AccountData.class)
            .map(this::toAccount);
    }

    @Override
    public Mono<Account> getAccountByOwner(Long ownerId) {
        Query query = Query.query(Criteria.where("ownerId").is(ownerId));
        return mongoTemplate.findOne(query, AccountData.class)
            .map(this::toAccount);
    }

    @Override
    public Mono<Account> save(Account account) {
        return mongoTemplate.save(toAccountData(account))
            .map(this::toAccount);
    }

    @Override
    public Mono<Account> update(Account account) {
        return mongoTemplate.save(toAccountData(account))
            .map(this::toAccount);
    }

    @Override
    public Mono<Void> delete(Long id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.remove(query, AccountData.class)
            .then();
    }

    @Override
    public Flux<Account> findAll() {
        return mongoTemplate.findAll(AccountData.class)
            .map(this::toAccount);
    }

    @Override
    public Flux<Account> findByOwnerId(Long ownerId) {
        Query query = Query.query(Criteria.where("ownerId").is(ownerId));
        return mongoTemplate.find(query, AccountData.class)
            .map(this::toAccount);
    }

    @Override
    public Mono<Boolean> exists(Long id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        return mongoTemplate.exists(query, AccountData.class);
    }

    // Mappers
    private AccountData toAccountData(Account account) {
        return objectMapper.map(account, AccountData.class);
    }

    private Account toAccount(AccountData data) {
        return objectMapper.mapBuilder(data, Account.AccountBuilder.class).build();
    }
}

