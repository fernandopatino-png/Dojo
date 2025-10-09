package co.com.bancolombia.usecase.list;

import co.com.bancolombia.model.list.ObjectRequest;
import co.com.bancolombia.model.list.gateway.ListRepository;
import co.com.bancolombia.model.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ListUseCase {

    private final ListRepository listRepository;
    private final ObjectMapper objectMapper;

    public Mono<User> listarCuentas(User user){

        return listRepository.testPost(user)
                .flatMap(objectResponse ->{
                    var type = objectResponse.path("data").path("account").get(0).path("type").asText();
                    var number = objectResponse.path("data").path("account").get(0).path("number").asText();
                        return  Mono.just(User.builder()
                                .type(type)
                                .name(number)
                                .build() );
                });

    }
}
