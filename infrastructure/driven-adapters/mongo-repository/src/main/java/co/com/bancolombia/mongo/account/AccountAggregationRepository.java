package co.com.bancolombia.mongo.account;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountAggregationRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public Mono<AccountSummary> getAccountSummaryByUser(Long userId) {
        Aggregation aggregation = Aggregation.newAggregation(
                //Filtrar por usuario
                Aggregation.match(Criteria.where("ownerId").is(userId)),
                //Agrupar y calcular estadísticas
                Aggregation.group("ownerId")
                        .sum("balance").as("totalBalance")
                        .avg("balance").as("averageBalance")
                        .min("balance").as("minBalance")
                        .max("balance").as("maxBalance")
                        .count().as("accountCount")
        );

        return mongoTemplate.aggregate(aggregation, AccountData.class, AccountSummary.class)
                .next()
                .doOnSuccess(summary -> log.info("Account summary for user {}: {}", userId, summary));
    }

    public Mono<TotalBalanceResult> getTotalBalance() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group()
                        .sum("balance").as("totalBalance")
                        .count().as("totalAccounts")
        );

        return mongoTemplate.aggregate(aggregation, AccountData.class, TotalBalanceResult.class)
                .next();
    }

    public Mono<AccountData> getTopAccountsByBalance(int limit) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "balance")),
                Aggregation.limit(limit)
        );
        return mongoTemplate.aggregate(aggregation, AccountData.class, AccountData.class)
                .next();
    }

    public Mono<AccountsByBalanceRange> countAccountsByBalanceRange() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group()
                        .count().as("total"),
                Aggregation.project()
                        .andExpression("balance >= 0 && balance < 1000").as("range1")
                        .andExpression("balance >= 1000 && balance < 5000").as("range2")
                        .andExpression("balance >= 5000").as("range3")
        );
        return mongoTemplate.aggregate(aggregation, AccountData.class, AccountsByBalanceRange.class)
                .next();
    }
}

