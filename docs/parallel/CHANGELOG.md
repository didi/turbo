# Change Log

All notable changes to this project will be documented in this file.

## [1.1.1] - 2025-11-14

### Added
- Support for nested parallel gateways
- Virtual threads support (JDK 21+) for better performance in high-concurrency scenarios
- Configurable thread pool with flexible options (file-based or custom Bean)
- Thread pool configuration documentation with virtual threads guide
- **Concurrency safety mechanism for branch merging** - Prevents data overwrite issues when multiple branches arrive concurrently
  - Default local lock implementation using `ReentrantLock` (single-machine deployment)
  - Extensible lock interface supporting custom implementations (e.g., Redis distributed lock)
  - Automatic retry mechanism when lock acquisition fails (configurable retry interval and max retry times)

### Changed
- Thread pool default core size increased from 1 to 10
- Engine dependency changed to version range `[1.3.0,)` allowing newer versions
- Enhanced POM configuration for standalone Maven Central publishing

### Fixed
- Fixed incorrect flow completion status in nested parallel gateway scenarios
- Fixed MyBatis interceptor unable to extract entity from `updateById` wrapped Map parameters
- Fixed `ParallelNodeInstanceHandler` unable to handle `updateById` and `deleteById` operations

### Documentation
- Added `ThreadPoolConfiguration.md` for detailed thread pool configuration guide
- Added `ConcurrencySafety.md` for branch merging lock mechanism documentation
- Updated `Parallel&InclusiveGateway.md` with quick start guide, nested gateway documentation, and concurrency safety guide
- Cross-referenced documentation for better navigation

## [1.1.0] - 2025-10-29

### Changed
- JDK upgrade: Upgraded from JDK 8 to JDK 21

### Support
- turbo 1.3.0+