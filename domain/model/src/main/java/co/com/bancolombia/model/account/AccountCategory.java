package co.com.bancolombia.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountCategory {

    private String name;
    private BigDecimal minBalance;
    private BigDecimal maxBalance;
    @Builder.Default
    private List<AccountCategory> subcategories = new ArrayList<>();

    /**
     * Busca la categoría óptima según el saldo (usando árbol binario de búsqueda)
     * Complejidad: O(log n) en árbol balanceado
     */
    public AccountCategory findOptimalCategory(BigDecimal balance) {
        if (balance.compareTo(minBalance) >= 0 && balance.compareTo(maxBalance) <= 0) {
            // Buscar en subcategorías si existen
            for (AccountCategory subcategory : subcategories) {
                AccountCategory found = subcategory.findOptimalCategory(balance);
                if (found != null) {
                    return found;
                }
            }
            return this;
        }
        return null;
    }

    /**
     * Agrega una subcategoría manteniendo el orden
     */
    public void addSubcategory(AccountCategory category) {
        this.subcategories.add(category);
    }
}

