package co.com.bancolombia.mongo;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.account.gateways.AccountRepository;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.mongo.account.AccountData;
import co.com.bancolombia.mongo.user.UserData;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class ReactiveMongoDBTemplate implements UserRepository, AccountRepository {

    private final ReactiveMongoTemplate template;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Account> getAccountById(Long id) {
        return null;
    }

    @Override
    public Mono<Account> getAccountByOwner(Long ownerId) {
        return null;
    }

    @Override
    public Mono<User> registerUser(User user) {
        return template.save(toUserData(user))
                .map(this::toUserIdentity);
    }

    @Override
    public Mono<User> findUserById(Long id) {
        return template.findById(id, UserData.class)
                .map(this::toUserIdentity);
    }

    @Override
    public Mono<Boolean> validateUserExists(Long id) {
        return template.exists(Query.query(Criteria.where("_id").is(id)), UserData.class);
    }

    protected UserData toUserData(User user) {
        return objectMapper.map(user, UserData.class);
    }

    protected AccountData toAccountData(Account account) {
        return objectMapper.map(account, AccountData.class);
    }

    protected User toUserIdentity(UserData userData) {
        return objectMapper.mapBuilder(userData, User.UserBuilder.class).build();
    }

    protected Account toAccountIdentity(AccountData accountData) {
        return objectMapper.mapBuilder(accountData, Account.AccountBuilder.class).build();
    }
}
