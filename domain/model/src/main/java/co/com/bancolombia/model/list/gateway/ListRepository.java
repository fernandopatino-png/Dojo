package co.com.bancolombia.model.list.gateway;

import co.com.bancolombia.model.list.ObjectRequest;
import co.com.bancolombia.model.list.ObjectResponse;
import co.com.bancolombia.model.user.User;
import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

public interface ListRepository {

  Mono<JsonNode> testPost(User requestTest);
}
