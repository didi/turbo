
# Change Log

All notable changes to this project will be documented in this file.

## [1.3.0] - 2025-10-29
### Changed
 - JDK Upgrade: Upgraded from JDK 8 to JDK 21
 - Spring Boot Upgrade: Upgraded to Spring Boot 3.2.x+ (current version 3.5.0), completing Spring Boot 3.x adaptation
### Bugfix
  - Fix v1.2.0 release issues, fix the bug in the data merge part of Parallel & Inclusive Gateway

### Notice
 - Starting from version 1.3.x, JDK 1.8 is no longer supported. Previous versions will not be updated unless major bugs occur. It is recommended to upgrade to JDK 21+.

## [1.2.0] - 2024-11-21

Support plugin extension function
   - Add `node_type` field to the `ei_node_instance` table in the database to save the node type.
   - Add `NodeExecuteResult` inner class to the `RuntimeResult` class, and move the `activeTaskInstance` and `variables` fields to the inner class.
   - Add `properties` variable to the `CommonPO` entity class to store extended data.
   - Add `ExtendRuntimeContext` class to store extended branch context information.

Support parallel gateway and inclusive gateway through plugins

### Bugfix
- Fix v1.1.1 release issues

## [1.1.1] - 2023-06-26
### Bugfix
- Fix v1.1.0 release issues


## [1.1.0] - 2023-03-15

Support CallActivity configuration and execution.

### Added

Add CallActivity Node

## [1.0.2] - 2023-02-20

### Changed

1. Optimize Maven dependency management

    -Remove httpclient dependency
    
    -Remove individual jsr250 API dependencies

    -Remove individual junit dependencies

    -Commons collections 3.2.2-->commons collections 4
    
    -Commons lang3 3.4-->3.12.0

2. Optimize hook function, change HTTP mode to internal call mode, reduce dependence on HTTP packages, and improve performance

3. Simplify the configuration steps for Turbo introduction and add @ enableTurboEngine annotation

4. Optimize partial code structure

5. Optimize Demo Test Execution

## [1.0.1] -  2022-07-08

### Changed

1. Upgrade external dependencies
2. Uniformly manage spring boot starter versions

### Fixed

Fix vulnerabilities

## [1.0.0] - 2020-12-28

### Added

Turbo join open source.
