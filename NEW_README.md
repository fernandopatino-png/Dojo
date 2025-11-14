# 🏦 Sistema Bancario - Proyecto Educativo

Este proyecto es un **sistema bancario simplificado** diseñado para enseñar conceptos fundamentales de programación.

## 📚 ¿Qué aprenderás con este proyecto?

### 1️⃣ Estructuras de Datos
### 2️⃣ Complejidad Algorítmica
### 3️⃣ Patrones de Diseño
### 4️⃣ Clean Architecture
### 5️⃣ Base de Datos (MongoDB)

---

## 🎯 ¿Qué hace este proyecto?

Es un sistema bancario que permite:
- ✅ Crear usuarios y cuentas bancarias
- ✅ Transferir dinero entre cuentas
- ✅ Consultar saldos e historial
- ✅ Validar operaciones automáticamente
- ✅ Recibir notificaciones de cambios
- ✅ Mantener auditoría de todas las operaciones

---

## 🏗️ Arquitectura del Proyecto (Clean Architecture)

```
┌─────────────────────────────────────────────┐
│  🌐 CAPA 4: Entry Points (APIs REST)        │
│  Endpoints HTTP para interactuar            │
│  AccountController, UserController          │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│  💼 CAPA 3: Use Cases (Lógica de Negocio)   │
│  Reglas de negocio del banco                │
│  TransferUseCase, AccountManagementUseCase  │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│  📦 CAPA 2: Domain (Modelos Puros)          │
│  Entidades sin dependencias externas        │
│  Account, User, Transaction                 │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│  🔧 CAPA 1: Infrastructure (Adaptadores)    │
│  Conexión con MongoDB, configuración        │
│  MongoAccountRepositoryAdapter              │
└─────────────────────────────────────────────┘
```

### ✨ Ventajas de Clean Architecture

1. **Independencia de Frameworks**: La lógica no depende de Spring o MongoDB
2. **Testeable**: Fácil hacer pruebas unitarias
3. **Independencia de UI**: Puede usarse con REST, GraphQL, CLI, etc.
4. **Independencia de BD**: Cambiar de MongoDB a PostgreSQL es fácil

---

## 📂 Estructura del Proyecto

```
domain/
├── model/                          # 📦 Modelos del dominio
│   ├── Account.java               # Cuenta bancaria
│   ├── User.java                  # Usuario (con Builder Pattern)
│   ├── Transaction.java           # Transacción
│   ├── AccountCategory.java       # Estructura de árbol
│   ├── validation/                # Patrón Strategy
│   │   ├── ValidationStrategy.java
│   │   ├── MinimumBalanceValidation.java
│   │   └── ...
│   └── events/                    # Patrón Observer
│       ├── AccountEventListener.java
│       ├── NotificationListener.java
│       └── AuditListener.java
│
└── usecase/                       # 💼 Casos de uso (lógica)
    ├── AccountManagementUseCase.java    # CRUD básico
    ├── TransferUseCase.java             # Transferencias
    ├── AccountSearchUseCase.java        # Búsquedas (complejidad)
    ├── TransactionHistoryUseCase.java   # Historial (Deque)
    ├── AccountValidationUseCase.java    # Validaciones (Strategy)
    └── AccountEventUseCase.java         # Eventos (Observer)

infrastructure/
├── driven-adapters/
│   └── mongo-repository/          # 🔧 Adaptadores MongoDB
│       ├── MongoAccountRepositoryAdapter.java
│       └── config/
│           ├── MongoIndexConfig.java    # Índices para rendimiento
│           └── MongoTransactionConfig.java
│
└── entry-points/
    └── reactive-web/              # 🌐 APIs REST
        ├── AccountController.java
        └── UserController.java

applications/
└── app-service/
    └── config/                    # ⚙️ Configuración Spring
        ├── UseCaseBeansConfig.java
        └── EventListenersConfig.java
```

---

## 🎓 TEMA 1: Estructuras de Datos

### 🔹 Deque (Cola de Doble Extremo)

**Archivo**: `TransactionHistoryUseCase.java`

**¿Qué es?** Una cola donde puedes agregar/quitar elementos por AMBOS lados.

**Uso en el proyecto**: Mantener las últimas 100 transacciones por cuenta.

```java
// Agregar al final: O(1)
history.addLast(transaction);

// Si excede 100, eliminar del inicio: O(1)
if (history.size() > 100) {
    history.removeFirst();
}
```

**Complejidad**:
- Agregar: O(1) - instantáneo
- Eliminar: O(1) - instantáneo
- Consultar últimas N: O(n)

### 🔹 HashMap (Cache)

**Archivo**: `AccountSearchUseCase.java`

**¿Qué es?** Un diccionario clave-valor con acceso ultra rápido.

**Uso en el proyecto**: Cachear cuentas buscadas frecuentemente.

```java
// Primera búsqueda: va a la BD (lento)
// Búsquedas siguientes: lee del cache (O(1) - instantáneo)
Account cached = accountCache.get(id);
```

### 🔹 Árbol

**Archivo**: `AccountCategory.java`

**¿Qué es?** Estructura jerárquica como un árbol genealógico.

**Uso en el proyecto**: Categorizar cuentas por saldo.

```
Cuentas
├── Básica ($0 - $1,000)
├── Premium ($1,000 - $5,000)
└── Elite ($5,000+)
```

**Búsqueda en árbol**: O(log n) - muy eficiente

---

## ⚡ TEMA 2: Complejidad Algorítmica

**Archivo**: `AccountSearchUseCase.java`

### Notación Big O - ¿Qué tan rápido es el algoritmo?

| Complejidad | Nombre | Ejemplo | Con 1,000,000 elementos |
|-------------|--------|---------|------------------------|
| O(1) | Constante | Acceso a HashMap | 1 operación |
| O(log n) | Logarítmica | Búsqueda binaria | ~20 operaciones |
| O(n) | Lineal | Recorrer lista | 1,000,000 operaciones |
| O(n log n) | Casi lineal | Ordenamiento eficiente | ~20,000,000 operaciones |

### Implementaciones en el proyecto:

#### 1. Cache - O(1)
```java
// Súper rápido: tiempo constante
public Mono<Account> findByIdWithCache(Long id) {
    Account cached = accountCache.get(id); // O(1)
    if (cached != null) return Mono.just(cached);
    // ...
}
```

#### 2. Búsqueda Binaria - O(log n)
```java
// Divide y vencerás: elimina la mitad en cada paso
public Mono<Account> binarySearchById(List<Account> sorted, Long id) {
    // Con 1,000,000 elementos → solo ~20 comparaciones
}
```

#### 3. Búsqueda Lineal - O(n)
```java
// Revisa uno por uno
public Mono<Account> linearSearchByOwner(List<Account> accounts, Long ownerId) {
    // Con 1,000 elementos → hasta 1,000 comparaciones
}
```

#### 4. Ordenamiento - O(n log n)
```java
// Ordena eficientemente
public Flux<Account> sortAccountsByBalance(List<Account> accounts) {
    list.sort(Comparator.comparing(Account::getBalance));
}
```

---

## 🎨 TEMA 3: Patrones de Diseño

### 🔸 Builder Pattern

**Archivo**: `User.java`

**Problema**: Constructores con muchos parámetros son difíciles de leer.

**Solución**: Builder Pattern permite construir objetos paso a paso.

```java
// ❌ SIN Builder: confuso
User user = new User(null, "Juan", "PREMIUM", "123", "juan@mail.com", true);

// ✅ CON Builder: claro y legible
User user = User.builder()
    .name("Juan")
    .email("juan@mail.com")
    .type("PREMIUM")
    .number("123")
    .active(true)
    .build(); // Valida antes de crear
```

### 🔸 Strategy Pattern

**Archivos**: `ValidationStrategy.java`, `MinimumBalanceValidation.java`, etc.

**Problema**: Muchas validaciones en un IF gigante es difícil de mantener.

**Solución**: Cada validación es una "estrategia" intercambiable.

```java
// Interfaz común
public interface ValidationStrategy {
    Mono<Boolean> validate(Account account);
}

// Estrategias concretas
public class MinimumBalanceValidation implements ValidationStrategy { ... }
public class ActiveAccountValidation implements ValidationStrategy { ... }

// Uso: combina las que quieras
List<ValidationStrategy> validations = Arrays.asList(
    new MinimumBalanceValidation(),
    new ActiveAccountValidation()
);
```

**Ventajas**:
- ✅ Fácil agregar nuevas validaciones
- ✅ Cada validación en su propia clase
- ✅ Puedes combinarlas como quieras

### 🔸 Observer Pattern

**Archivos**: `AccountEventListener.java`, `NotificationListener.java`, `AuditListener.java`

**Problema**: Cuando ocurre un evento, ¿cómo notificar a múltiples sistemas?

**Solución**: Lista de "observadores" que se suscriben a eventos.

```java
// Interfaz de observer
public interface AccountEventListener {
    void onAccountCreated(Account account);
    void onBalanceChanged(Account account, Double oldBalance, Double newBalance);
}

// Observers concretos
public class NotificationListener implements AccountEventListener {
    // Envía emails/SMS
}

public class AuditListener implements AccountEventListener {
    // Guarda logs de auditoría
}

// Uso: registrar observers
eventUseCase.addListener(new NotificationListener());
eventUseCase.addListener(new AuditListener());

// Cuando ocurre un evento, TODOS son notificados automáticamente
```

**Analogía**: Es como suscribirse a un canal de YouTube. Cuando el canal publica, todos los suscriptores reciben notificación.

---

## 🏛️ TEMA 4: Clean Architecture

### Principio de Inversión de Dependencias

**Las capas internas NO conocen las externas.**

```java
// ❌ MAL: Use Case depende de implementación concreta
public class TransferUseCase {
    private MongoAccountRepository repository; // Dependencia concreta
}

// ✅ BIEN: Use Case depende de abstracción
public class TransferUseCase {
    private AccountRepository repository; // Interfaz (abstracción)
}
```

### Capas del Proyecto

#### 📦 Domain (Capa 1 - Centro)
- Modelos puros sin dependencias
- `Account.java`, `User.java`, `Transaction.java`
- **NO** conoce Spring, MongoDB, APIs

#### 💼 Use Cases (Capa 2)
- Lógica de negocio pura
- `TransferUseCase.java`, `AccountManagementUseCase.java`
- Usa interfaces (no implementaciones)

#### 🔧 Infrastructure (Capa 3)
- Implementaciones concretas
- `MongoAccountRepositoryAdapter.java`
- Conoce MongoDB, Spring Data

#### 🌐 Entry Points (Capa 4 - Externa)
- APIs REST, CLI, GraphQL
- `AccountController.java`
- Recibe requests HTTP

---

## 🗄️ TEMA 5: Base de Datos (MongoDB)

### Índices para Optimización

**Archivo**: `MongoIndexConfig.java`

**¿Qué son los índices?** Como el índice de un libro: te lleva directo a la página.

```java
// Crear índice en campo ownerId
mongoTemplate.indexOps(AccountData.class)
    .ensureIndex(new Index().on("ownerId", Sort.Direction.ASC))
```

**Impacto**:
- SIN índice: 1,000,000 comparaciones
- CON índice: ~20 comparaciones

### CRUD Reactivo

**Archivo**: `MongoAccountRepositoryAdapter.java`

```java
// CREATE
mongoTemplate.save(account)

// READ
mongoTemplate.findById(id, AccountData.class)

// UPDATE
mongoTemplate.save(accountActualizado)

// DELETE
mongoTemplate.remove(query, AccountData.class)
```

### Consultas con Filtros

```java
// WHERE ownerId = ?
Query query = Query.query(Criteria.where("ownerId").is(ownerId));
mongoTemplate.find(query, AccountData.class)
```

---

## 🚀 Cómo Ejecutar el Proyecto

### 1. Requisitos Previos
- Java 17+
- MongoDB (o Docker)
- Gradle

### 2. Iniciar MongoDB con Docker
```bash
docker run -d -p 27017:27017 --name mongodb mongo:latest
```

### 3. Compilar el Proyecto
```bash
./gradlew build
```

### 4. Ejecutar la Aplicación
```bash
./gradlew bootRun
```

La aplicación iniciará en `http://localhost:8080`

---

## 📡 Endpoints de la API

### Usuarios

#### Crear Usuario
```http
POST /api/users
Content-Type: application/json

{
  "name": "Juan Perez",
  "email": "juan@example.com",
  "type": "PREMIUM",
  "number": "123456789"
}
```

#### Obtener Usuario
```http
GET /api/users/{id}
```

### Cuentas

#### Crear Cuenta
```http
POST /api/accounts
Content-Type: application/json

{
  "ownerId": 1,
  "balance": 5000.0
}
```

#### Obtener Cuenta
```http
GET /api/accounts/{id}
```

#### Obtener Cuenta (con Cache - O(1))
```http
GET /api/accounts/{id}/cached
```

#### Listar Cuentas de un Usuario
```http
GET /api/accounts/owner/{ownerId}
```

#### Actualizar Saldo
```http
PUT /api/accounts/{id}/balance?newBalance=1500.0
```

### Transferencias

#### Realizar Transferencia
```http
POST /api/accounts/transfer
Content-Type: application/json

{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 500.0
}
```

---

## 💡 Flujo de una Transferencia (Ejemplo Completo)

```
1. Usuario hace: POST /api/accounts/transfer
   ↓
2. AccountController recibe el request
   ↓
3. TransferUseCase ejecuta validaciones:
   ✓ ¿Las cuentas son diferentes?
   ✓ ¿El monto es positivo?
   ✓ ¿No excede $10,000?
   ✓ ¿Hay saldo suficiente?
   ↓
4. MongoAccountRepositoryAdapter actualiza MongoDB:
   - Cuenta A: $2,000 → $1,500 (resta $500)
   - Cuenta B: $1,000 → $1,500 (suma $500)
   ↓
5. AccountEventUseCase notifica a observers:
   - NotificationListener: "Transferencia exitosa"
   - AuditListener: Guarda en log de auditoría
   ↓
6. Retorna respuesta 200 OK
```

---

## 🎯 Resumen de Conceptos por Archivo

| Archivo | Concepto Principal | Qué Enseña |
|---------|-------------------|------------|
| `TransactionHistoryUseCase.java` | Deque | Estructura de datos eficiente O(1) |
| `AccountSearchUseCase.java` | Complejidad | O(1), O(log n), O(n), O(n log n) |
| `AccountCategory.java` | Árbol | Búsqueda recursiva O(log n) |
| `User.java` | Builder Pattern | Construcción de objetos limpia |
| `ValidationStrategy.java` | Strategy Pattern | Algoritmos intercambiables |
| `AccountEventListener.java` | Observer Pattern | Notificaciones desacopladas |
| `TransferUseCase.java` | Clean Architecture | Use Cases independientes |
| `MongoAccountRepositoryAdapter.java` | Adaptador | Inversión de dependencias |
| `MongoIndexConfig.java` | Índices BD | Optimización de consultas |

---

## 📖 Glosario

- **Mono**: Representa 0 o 1 elemento (programación reactiva)
- **Flux**: Representa 0 a N elementos (programación reactiva)
- **Bean**: Objeto creado y administrado por Spring
- **Repository**: Capa que accede a la base de datos
- **Use Case**: Clase con lógica de negocio específica
- **Gateway**: Interfaz entre capas (puerto en arquitectura hexagonal)
- **Adapter**: Implementación concreta de un gateway
- **DTO**: Data Transfer Object (objeto para transferir datos entre capas)

---

## 🤝 Contribuir

Este es un proyecto educativo. Siéntete libre de:
- Agregar más ejemplos de patrones
- Mejorar la documentación
- Crear más casos de uso
- Agregar tests unitarios

---

## 📚 Recursos Adicionales

- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Design Patterns](https://refactoring.guru/design-patterns)
- [Big O Notation](https://www.bigocheatsheet.com/)
- [Spring Boot Reactive](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [MongoDB Manual](https://docs.mongodb.com/manual/)

---

**¡Feliz aprendizaje! 🚀**

