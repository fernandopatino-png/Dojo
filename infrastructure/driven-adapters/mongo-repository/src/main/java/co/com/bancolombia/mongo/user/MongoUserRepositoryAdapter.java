package co.com.bancolombia.mongo.user;

import co.com.bancolombia.model.account.Account;
import co.com.bancolombia.model.user.User;
import co.com.bancolombia.model.user.gateways.UserRepository;
import co.com.bancolombia.mongo.account.AccountData;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class MongoUserRepositoryAdapter implements UserRepository {

    private final ReactiveMongoTemplate template;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<User> registerUser(User user) {
        return template.save(toUserData(user))
                .map(this::toUserIdentity);
    }

    @Override
    public Mono<User> findUserById(String id) {
        return template.findById(id, UserData.class)
                .map(this::toUserIdentity);
    }

    @Override
    public Mono<Boolean> validateUserExists(String id) {
        return template.exists(Query.query(Criteria.where("_id").is(id)), UserData.class);
    }

    protected UserData toUserData(User user) {
        return objectMapper.map(user, UserData.class);
    }

    protected AccountData toAccountData(Account account) {
        return objectMapper.map(account, AccountData.class);
    }

    protected User toUserIdentity(UserData userData) {
        return objectMapper.mapBuilder(userData, User.Builder.class).build();
    }

    protected Account toAccountIdentity(AccountData accountData) {
        return objectMapper.mapBuilder(accountData, Account.AccountBuilder.class).build();
    }
}
