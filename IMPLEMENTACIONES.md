# Implementaciones de Estructuras de Datos, Patrones y Arquitectura Limpia

Este documento describe todas las implementaciones realizadas en el proyecto organizadas por tema.

## 📚 1. PROGRAMACIÓN Y ESTRUCTURAS DE DATOS

### Transaction & TransactionHistory (Deque/Queue)
- **Archivos**: `Transaction.java`, `TransactionHistoryUseCase.java`
- **Estructura**: `Deque<Transaction>` (Double-ended queue)
- **Complejidad**: 
  - Agregar transacción: O(1)
  - Obtener últimas N: O(n)
- **Características**:
  - Historial limitado con FIFO (First In First Out)
  - Elimina automáticamente la transacción más antigua al exceder límite
  - Operaciones en ambos extremos de la cola

### AccountCategory (Árbol)
- **Archivo**: `AccountCategory.java`
- **Estructura**: Árbol de categorías con subcategorías
- **Complejidad**: O(log n) en búsqueda de categoría óptima
- **Características**:
  - Jerarquía de categorías por rangos de saldo
  - Búsqueda recursiva en árbol

## ⚡ 2. COMPLEJIDAD ALGORÍTMICA

### AccountSearchUseCase
- **Archivo**: `AccountSearchUseCase.java`
- **Implementaciones**:

#### Cache con HashMap - O(1)
```java
findByIdWithCache(Long id) // Primera vez O(n), siguiente O(1)
```

#### Búsqueda Binaria - O(log n)
```java
binarySearchById(List<Account> sortedAccounts, Long id)
```

#### Ordenamiento - O(n log n)
```java
sortAccountsByBalance() // Usa Dual-Pivot Quicksort
```

#### Top N con MinHeap - O(n log k)
```java
findTopNAccountsByBalance(int n) // k = tamaño del heap
```

#### Búsqueda Lineal - O(n)
```java
linearSearchByOwner(Long ownerId)
```

## 🎨 3. PATRONES DE DISEÑO

### Builder Pattern
- **Archivo**: `User.java`
- **Características**:
  - Builder personalizado con validaciones
  - Validación de email y nombre obligatorios
  - Valores por defecto (active = true)

```java
User user = User.builder()
    .name("Juan")
    .email("juan@example.com")
    .build();
```

### Strategy Pattern
- **Archivos**: 
  - `ValidationStrategy.java` (interfaz)
  - `MinimumBalanceValidation.java`
  - `ActiveAccountValidation.java`
  - `OwnerExistsValidation.java`
  - `AccountValidationUseCase.java`
- **Características**:
  - Estrategias de validación intercambiables
  - Agregar/remover validaciones dinámicamente
  - Validación con múltiples estrategias

### Observer Pattern
- **Archivos**:
  - `AccountEventListener.java` (interfaz)
  - `NotificationListener.java` (notificaciones)
  - `AuditListener.java` (auditoría)
  - `AccountEventUseCase.java` (subject)
- **Eventos**:
  - onAccountCreated
  - onBalanceChanged
  - onAccountDeleted
- **Listeners registrados**:
  - NotificationListener: logs de notificaciones
  - AuditListener: registro de auditoría con timestamps

## 🏛️ 4. ARQUITECTURA LIMPIA

### Capas Implementadas

#### Domain Layer (Capa de Dominio)
**Model**:
- `Account.java` - Entidad de cuenta
- `User.java` - Entidad de usuario con Builder
- `Transaction.java` - Modelo de transacción
- `TransferResult.java` - Resultado de transferencia
- `AccountCategory.java` - Categorías de cuenta

**Gateways**:
- `AccountRepository.java` - Interfaz con operaciones CRUD completas
- `UserRepository.java` - Interfaz para usuarios

**Use Cases**:
- `AccountManagementUseCase` - CRUD de cuentas con reglas de negocio
- `TransferUseCase` - Transferencias con validaciones complejas
- `AccountSearchUseCase` - Búsquedas optimizadas
- `TransactionHistoryUseCase` - Gestión de historial
- `AccountValidationUseCase` - Validaciones con Strategy
- `AccountEventUseCase` - Gestión de eventos con Observer

#### Infrastructure Layer (Capa de Infraestructura)

**Driven Adapters (Salida)**:
- `MongoAccountRepositoryAdapter` - Implementación MongoDB del gateway
- `AccountAggregationRepository` - Consultas avanzadas
- `TransactionalAccountRepository` - Transacciones ACID

**Entry Points (Entrada)**:
- `AccountController` - REST API para cuentas
- `UserController` - REST API para usuarios

### Principios SOLID Aplicados

✅ **S** - Single Responsibility: Cada Use Case tiene una responsabilidad específica
✅ **O** - Open/Closed: Strategy Pattern permite extensión sin modificación
✅ **L** - Liskov Substitution: Implementaciones intercambiables de repositories
✅ **I** - Interface Segregation: Interfaces específicas (ValidationStrategy, EventListener)
✅ **D** - Dependency Inversion: Use Cases dependen de abstracciones (gateways)

## 🗄️ 5. BASE DE DATOS (MongoDB Reactivo)

### Implementaciones MongoDB

#### CRUD Completo
- **Archivo**: `MongoAccountRepositoryAdapter.java`
- **Operaciones**:
  - save() - Crear cuenta
  - update() - Actualizar cuenta
  - delete() - Eliminar cuenta
  - findAll() - Listar todas
  - findByOwnerId() - Buscar por propietario
  - exists() - Verificar existencia

#### Índices Optimizados
- **Archivo**: `MongoIndexConfig.java`
- **Índices creados**:
  - `ownerId` (ASC) - Para búsquedas por propietario
  - `ownerId + balance` (compuesto) - Para consultas complejas
  - `email` (único) - Para usuarios
  - `type` (ASC) - Para filtrar por tipo
- **Beneficio**: Mejora búsquedas de O(n) a O(log n)

#### Agregaciones Complejas
- **Archivo**: `AccountAggregationRepository.java`
- **Operaciones**:
  - `getAccountSummaryByUser()` - Estadísticas por usuario (sum, avg, min, max)
  - `getTotalBalance()` - Balance total del sistema
  - `getTopAccountsByBalance()` - Cuentas con mayor saldo
  - `countAccountsByBalanceRange()` - Distribución por rangos

#### Transacciones ACID
- **Archivo**: `TransactionalAccountRepository.java`
- **Operaciones transaccionales**:
  - `createUserWithAccount()` - Crea usuario y cuenta atómicamente
  - `transferBetweenAccountsTransactional()` - Transferencia con rollback
- **Garantías**: Atomicidad, Consistencia, Aislamiento, Durabilidad

## 🌐 6. API REST (Entry Points)

### Endpoints Implementados

#### Cuentas (`/api/accounts`)
- `POST /api/accounts` - Crear cuenta
- `GET /api/accounts/{id}` - Obtener por ID
- `GET /api/accounts/{id}/cached` - Obtener con cache O(1)
- `GET /api/accounts/owner/{ownerId}` - Listar por propietario
- `GET /api/accounts` - Listar todas
- `PUT /api/accounts/{id}/balance` - Actualizar saldo
- `POST /api/accounts/transfer` - Realizar transferencia
- `DELETE /api/accounts/{id}` - Eliminar cuenta
- `POST /api/accounts/cache/clear` - Limpiar cache

#### Usuarios (`/api/users`)
- `POST /api/users` - Registrar usuario (usa Builder Pattern)
- `GET /api/users/{id}` - Obtener por ID
- `GET /api/users/{id}/exists` - Verificar existencia

## 🔧 Configuración

### Beans de Use Cases
- **Archivo**: `UseCaseBeansConfig.java`
- Configura todos los Use Cases con sus dependencias
- Inyección de dependencias siguiendo Clean Architecture

### Event Listeners
- **Archivo**: `EventListenersConfig.java`
- Registra automáticamente NotificationListener y AuditListener
- Se ejecuta al iniciar la aplicación

## 🚀 Cómo Probar

### 1. Crear Usuario
```bash
POST http://localhost:8080/api/users
{
  "name": "Juan Perez",
  "email": "juan@example.com",
  "type": "PREMIUM",
  "number": "1234567890"
}
```

### 2. Crear Cuenta
```bash
POST http://localhost:8080/api/accounts
{
  "ownerId": 1,
  "balance": 5000.0
}
```

### 3. Realizar Transferencia
```bash
POST http://localhost:8080/api/accounts/transfer
{
  "fromAccountId": 1,
  "toAccountId": 2,
  "amount": 1000.0
}
```

## 📊 Resumen de Implementaciones

| Tema | Archivos Creados | Conceptos Aplicados |
|------|------------------|---------------------|
| **Estructuras de Datos** | 3 | Deque, Queue, Tree |
| **Complejidad** | 1 | O(1), O(log n), O(n), O(n log n) |
| **Patrones** | 9 | Builder, Strategy, Observer |
| **Clean Architecture** | 8 | Use Cases, Gateways, SOLID |
| **Base de Datos** | 8 | CRUD, Índices, Agregaciones, Transacciones |
| **APIs REST** | 5 | Controllers, DTOs |
| **Total** | **34 archivos** | - |

## 🎯 Objetivos de Aprendizaje Cubiertos

✅ Estructuras de datos eficientes (Deque, Tree, HashMap, PriorityQueue)
✅ Análisis de complejidad algorítmica
✅ Patrones de diseño (Builder, Strategy, Observer)
✅ Arquitectura limpia y SOLID
✅ MongoDB reactivo con Spring WebFlux
✅ Índices y optimización de consultas
✅ Agregaciones complejas
✅ Transacciones ACID
✅ APIs REST reactivas
✅ Inyección de dependencias

## 📝 Notas

- Todas las operaciones son **reactivas** usando Project Reactor
- Se usa **Lombok** para reducir boilerplate
- Los **Use Cases** son independientes de frameworks (Clean Architecture)
- Los **Gateways** son interfaces que permiten cambiar implementaciones
- Los **eventos** se propagan a múltiples listeners (Observer Pattern)
- Las **validaciones** son extensibles (Strategy Pattern)

