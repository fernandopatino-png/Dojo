package co.com.bancolombia.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountCategory {

    private String name;
    private Double minBalance;
    private Double maxBalance;
    @Builder.Default
    private List<AccountCategory> subcategories = new ArrayList<>();

    public AccountCategory findOptimalCategory(Double balance) {
        //Paso 1: Verificar si el saldo está en el rango de esta categoría
        boolean inRange = balance >= minBalance && balance <= maxBalance;

        if (inRange) {
            //Paso 2: Buscar en las subcategorías (más específicas)
            for (AccountCategory subcategory : subcategories) {
                AccountCategory found = subcategory.findOptimalCategory(balance);
                if (found != null) {
                    return found; //Encontramos una subcategoría que coincide
                }
            }
            //Paso 3: Si no hay subcategorías que coincidan, esta es la categoría correcta
            return this;
        }
        //El saldo no está en el rango de esta categoría
        return null;
    }

    public void addSubcategory(AccountCategory category) {
        this.subcategories.add(category);
    }
}
