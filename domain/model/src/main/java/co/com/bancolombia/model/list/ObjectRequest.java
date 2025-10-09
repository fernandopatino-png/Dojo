package co.com.bancolombia.model.list;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ObjectRequest {

    private Long id;
    private String name;
    private String type;
    private String number;

}
