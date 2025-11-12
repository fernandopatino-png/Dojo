package co.com.bancolombia.model.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * CATEGORÍA DE CUENTA - ESTRUCTURA DE DATOS: ÁRBOL
 *
 * Esta clase representa una jerarquía de categorías de cuentas bancarias.
 * Usa una ESTRUCTURA DE ÁRBOL para organizar las categorías.
 *
 * ¿Qué es un ÁRBOL en programación?
 * - Es como un árbol genealógico
 * - Tiene un nodo raíz (categoría principal)
 * - Cada nodo puede tener hijos (subcategorías)
 *
 * EJEMPLO DE JERARQUÍA:
 *
 * Cuentas
 * ├── Básica ($0 - $1,000)
 * │   ├── Básica Joven
 * │   └── Básica Estudiante
 * ├── Premium ($1,000 - $5,000)
 * └── Elite ($5,000+)
 *
 * COMPLEJIDAD ALGORÍTMICA:
 * - Búsqueda en árbol balanceado: O(log n) - MUY RÁPIDO
 * - Búsqueda lineal simple: O(n) - más lento
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountCategory {

    /** Nombre de la categoría (ej: "Premium", "Básica") */
    private String name;

    /** Saldo mínimo para esta categoría */
    private Double minBalance;

    /** Saldo máximo para esta categoría */
    private Double maxBalance;

    /** Lista de subcategorías (hijos del árbol) */
    @Builder.Default
    private List<AccountCategory> subcategories = new ArrayList<>();

    /**
     * BUSCA LA CATEGORÍA ÓPTIMA SEGÚN EL SALDO
     *
     * Algoritmo de búsqueda recursiva en árbol.
     *
     * ¿Cómo funciona?
     * 1. Verifica si el saldo está en el rango de esta categoría
     * 2. Si sí, busca en las subcategorías (más específicas)
     * 3. Si encuentra una subcategoría que coincida, la retorna
     * 4. Si no, retorna esta categoría
     *
     * COMPLEJIDAD: O(log n) en árbol balanceado
     *
     * Ejemplo:
     * Si tengo $1,500, buscará:
     * 1. ¿Está en Básica ($0-$1,000)? NO
     * 2. ¿Está en Premium ($1,000-$5,000)? SÍ
     * 3. Busca en subcategorías de Premium
     * 4. Retorna "Premium"
     *
     * @param balance Saldo de la cuenta
     * @return Categoría que corresponde al saldo, o null si no encuentra
     */
    public AccountCategory findOptimalCategory(Double balance) {
        // Paso 1: Verificar si el saldo está en el rango de esta categoría
        boolean inRange = balance >= minBalance && balance <= maxBalance;

        if (inRange) {
            // Paso 2: Buscar en las subcategorías (más específicas)
            for (AccountCategory subcategory : subcategories) {
                AccountCategory found = subcategory.findOptimalCategory(balance);
                if (found != null) {
                    return found; // Encontramos una subcategoría que coincide
                }
            }
            // Paso 3: Si no hay subcategorías que coincidan, esta es la categoría correcta
            return this;
        }

        // El saldo no está en el rango de esta categoría
        return null;
    }

    /**
     * AGREGA UNA SUBCATEGORÍA
     *
     * Permite construir el árbol agregando categorías hijas.
     *
     * Ejemplo:
     * CategoryAccount premium = new CategoryAccount("Premium", 1000.0, 5000.0);
     * CategoryAccount premiumPlus = new CategoryAccount("Premium Plus", 3000.0, 5000.0);
     * premium.addSubcategory(premiumPlus);
     *
     * @param category Subcategoría a agregar
     */
    public void addSubcategory(AccountCategory category) {
        this.subcategories.add(category);
    }
}
