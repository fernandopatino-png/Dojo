package co.com.bancolombia.mongo.config;

import com.mongodb.reactivestreams.client.MongoClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@AllArgsConstructor
@Configuration
public class ReactiveMongoConfig {

    private final Environment environment;
    private final MongoClient mongoClient;

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        var database = environment.getProperty("spring.data.mongodb.database");
        assert database != null;
        return new ReactiveMongoTemplate(mongoClient, database);
    }
}
