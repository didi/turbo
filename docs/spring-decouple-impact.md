# Spring Decoupling – Impact Report

## Overview

This document describes the Spring Framework decoupling refactoring for the Turbo workflow engine.
It was produced as part of the module restructure that introduced `turbo-core`, `turbo-spring`, and
`turbo-spring-boot-starter`.

---

## Affected Classes / Files

### 1. Config & Wiring Classes (Spring-specific – moved to `turbo-spring`)

| File | Spring Feature Used | Status |
|------|-------------------|--------|
| `engine/config/TurboEngineConfig.java` | `@Configuration`, `@Bean`, `@ComponentScan`, `@MapperScan`, `@Value`, `@EnableAutoConfiguration` | **Moved to `turbo-spring`**; updated to reference `*DAOImpl` beans |
| `engine/config/PluginConfig.java` | `@Configuration`, `@Bean`, `@Value`, `ApplicationContext` | **Moved to `turbo-spring`** |
| `engine/config/TurboMybatisConfig.java` | `@Configuration`, `@PostConstruct`, Spring-managed `SqlSessionFactory` | **Moved to `turbo-spring`** |
| `engine/annotation/EnableTurboEngine.java` | `@Import` | **Moved to `turbo-spring`** |

### 2. DAO Layer (Spring/MyBatis-Plus – implementation stays in `turbo-spring`)

| File | Spring Feature Used | Status |
|------|-------------------|--------|
| `engine/dao/BaseDAO.java` | Extends `ServiceImpl` (MyBatis-Plus Spring integration), `@DS` (dynamic datasource) | **Kept in `engine`/`turbo-spring`** |
| `engine/dao/FlowDefinitionDAO.java` | MyBatis-Plus `ServiceImpl`, `@DS` | **Renamed to `FlowDefinitionDAOImpl`** in `engine/dao/impl/` |
| `engine/dao/FlowDeploymentDAO.java` | Same | **Renamed to `FlowDeploymentDAOImpl`** |
| `engine/dao/ProcessInstanceDAO.java` | Same | **Renamed to `ProcessInstanceDAOImpl`** |
| `engine/dao/NodeInstanceDAO.java` | Same | **Renamed to `NodeInstanceDAOImpl`** |
| `engine/dao/InstanceDataDAO.java` | Same | **Renamed to `InstanceDataDAOImpl`** |
| `engine/dao/FlowInstanceMappingDAO.java` | Same | **Renamed to `FlowInstanceMappingDAOImpl`** |
| `engine/dao/NodeInstanceLogDAO.java` | Same | **Renamed to `NodeInstanceLogDAOImpl`** |
| `engine/dao/mapper/*.java` | `@Mapper`, `@DS`, MyBatis-Spring mapper interfaces | **Kept in `engine`/`turbo-spring`** |

### 3. Business Logic Classes (No Spring imports – moved to `turbo-core`)

All classes in the following packages use only `javax.annotation.Resource` and
`javax.annotation.PostConstruct` (standard Java EE annotations, not Spring-specific). They were
moved to `turbo-core`:

| Package | Description |
|---------|-------------|
| `engine/executor/**` | Flow executors (`FlowExecutor`, `RuntimeExecutor`, `ElementExecutor`, etc.) |
| `engine/processor/**` | `DefinitionProcessor`, `RuntimeProcessor` |
| `engine/service/**` | `FlowInstanceService`, `InstanceDataService`, `NodeInstanceService` |
| `engine/validator/**` | All validators and `ElementValidatorFactory` |
| `engine/plugin/**` | Plugin interfaces, `DefaultPluginManager`, `AbstractPluginManager`, etc. |
| `engine/entity/**` | Entity POs (`FlowDefinitionPO`, `NodeInstancePO`, etc.) |
| `engine/bo/**` | Business Objects |
| `engine/common/**` | Constants, enums, `RuntimeContext` |
| `engine/exception/**` | Custom exceptions |
| `engine/model/**` | Flow model classes |
| `engine/param/**` | Request params |
| `engine/result/**` | Response results |
| `engine/spi/**` | SPI interfaces (`HookService`) |
| `engine/util/**` | Utility classes |
| `engine/engine/**` | `ProcessEngine` interface, `ProcessEngineImpl` |
| `engine/config/BusinessConfig.java` | Pure POJO config (no Spring) |

### 4. Plugin Manager – Cleaned Up Import

| File | Change |
|------|--------|
| `engine/plugin/manager/PluginManager.java` | Removed spurious `import com.didiglobal.turbo.engine.dao.BaseDAO` |

---

## New Module Structure

```
turbo/
├── turbo-core/                  # NEW – pure Java, NO org.springframework.* deps
│   ├── src/main/java/...
│   │   ├── dao/                 # DAO interfaces (FlowDefinitionDAO, etc.)
│   │   ├── engine/              # ProcessEngine interface, ProcessEngineImpl
│   │   │   ├── TurboEngineBuilder.java  # NEW: explicit wiring without IoC
│   │   │   └── impl/ProcessEngineImpl.java
│   │   ├── executor/            # All executor classes
│   │   ├── processor/           # DefinitionProcessor, RuntimeProcessor
│   │   ├── service/             # FlowInstanceService, etc.
│   │   ├── validator/           # All validators
│   │   ├── plugin/              # Plugin interfaces & managers
│   │   ├── entity/              # Entity POs
│   │   ├── bo/common/model/param/result/exception/util/
│   │   └── config/BusinessConfig.java
│   └── src/test/java/...
│       └── TurboEnginePureJavaTest.java  # NEW: pure Java test, no Spring
│
├── engine/                      # UPDATED – Spring-integrated; now depends on turbo-core
│   └── src/main/java/...
│       ├── annotation/          # @EnableTurboEngine
│       ├── config/              # TurboEngineConfig, PluginConfig, TurboMybatisConfig
│       ├── dao/BaseDAO.java
│       ├── dao/impl/*DAOImpl.java  # NEW: MyBatis-Plus DAO implementations
│       ├── dao/mapper/          # MyBatis mappers
│       └── interceptor/
│
├── turbo-spring/                # NEW – Spring adapter module
│   └── (mirrors engine/ structure; provided for standalone Spring use)
│
├── turbo-spring-boot-starter/   # NEW – Spring Boot auto-configuration
│   ├── TurboAutoConfiguration.java
│   ├── TurboProperties.java
│   └── TurboAutoConfigurationTest.java
│
├── parallel-plugin/             # UNCHANGED (depends on engine)
└── demo/                        # UNCHANGED
```

---

## SPI / Interface Changes

### DAO Interfaces Introduced in `turbo-core`

The following pure-Java DAO interfaces are now defined in `turbo-core`:

| Interface | Methods |
|-----------|---------|
| `FlowDefinitionDAO` | `insert`, `updateByModuleId`, `selectByModuleId` |
| `FlowDeploymentDAO` | `insert`, `selectByDeployId`, `selectRecentByFlowModuleId` |
| `ProcessInstanceDAO` | `selectByFlowInstanceId`, `insert`, `updateStatus` (×2) |
| `NodeInstanceDAO` | 11 methods (insert, insertOrUpdateList, select*, updateStatus) |
| `InstanceDataDAO` | `select`, `selectRecentOne`, `insert`, `updateData`, `insertOrUpdate` |
| `FlowInstanceMappingDAO` | `selectFlowInstanceMappingPOList`, `selectFlowInstanceMappingPO`, `insert`, `updateType` |
| `NodeInstanceLogDAO` | `insert`, `insertList` |

Spring/MyBatis-Plus implementations live in `engine/dao/impl/` (`*DAOImpl` classes).

### New `TurboEngineBuilder`

`com.didiglobal.turbo.engine.engine.TurboEngineBuilder` allows constructing a fully-wired
`ProcessEngine` **without any IoC container**:

```java
ProcessEngine engine = TurboEngineBuilder.create()
    .flowDefinitionDAO(myFlowDefinitionDAO)
    .flowDeploymentDAO(myFlowDeploymentDAO)
    .processInstanceDAO(myProcessInstanceDAO)
    .nodeInstanceDAO(myNodeInstanceDAO)
    .instanceDataDAO(myInstanceDataDAO)
    .flowInstanceMappingDAO(myFlowInstanceMappingDAO)
    .nodeInstanceLogDAO(myNodeInstanceLogDAO)
    .build();
```

---

## Spring Boot Configuration

Configuration keys supported by `turbo-spring-boot-starter`:

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `turbo.enabled` | `boolean` | `true` | Enable/disable Turbo auto-configuration |
| `turbo.plugin-manager-custom-class` | `String` | _null_ | FQCN of a custom `PluginManager` |
| `turbo.call-activity-nested-level` | `String` | _null_ | JSON map controlling CallActivity nesting limits per caller |

Existing property `callActivity.nested.level` (used in the old `TurboEngineConfig`) is preserved via
`@Value` in `TurboEngineConfig.businessConfig()`; the new property `turbo.call-activity-nested-level`
provides the same capability through the Boot starter.

---

## Behavioral Risks & Notes

### Transaction Boundaries
- The existing code does **not** use `@Transactional`. There are no transaction boundary changes.
- Individual DAO operations remain atomic at the SQL level.

### Initialization Order
- Business logic classes (`processor/`, `service/`, `executor/`) use `@PostConstruct` for lifecycle
  callbacks. In Spring context this works as before. In standalone (`TurboEngineBuilder`) mode,
  the builder calls `init()` on `DefinitionProcessor` and `ExecutorFactory` explicitly.
- `PluginManager` initialization (loading SPI plugins) now runs in the `DefaultPluginManager`
  constructor, which is consistent with previous behaviour.

### `SyncSingleCallActivityExecutor.save()` → `insert()`
- One call site used the MyBatis-Plus `ServiceImpl.save()` shorthand. This was replaced with the
  equivalent `insert()` method defined in the `FlowInstanceMappingDAO` interface, preserving
  semantics (no upsert – the record is always new at this point).

### `PluginManager.java` – removed unused `BaseDAO` import
- The interface had an unused import for `BaseDAO`. Removed to keep `turbo-core` free of
  Spring/MyBatis-Plus dependencies.

### `NodeInstanceLogDAOTest` – test updated
- The test previously called `nodeInstanceLogDAO.list(queryWrapper)` (a MyBatis-Plus
  `ServiceImpl` method). Since `NodeInstanceLogDAO` is now a clean interface, the test was
  updated to verify the boolean return value of `insertList()` instead.

---

## Migration Notes for Existing Users

### Users relying on `@EnableTurboEngine`
No change – the annotation still exists in `engine` (and `turbo-spring`).

### Users of `engine` artifact directly
No change – the `engine` artifact still exists and contains all Spring-integrated beans.
Add `turbo-core` to your project if you need standalone (non-Spring) engine access.

### Spring Boot users
Add the new starter instead of (or in addition to) the `engine` artifact:

```xml
<dependency>
    <groupId>com.didiglobal.turbo</groupId>
    <artifactId>turbo-spring-boot-starter</artifactId>
    <version>1.3.1</version>
</dependency>
```
