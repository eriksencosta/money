# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to
[Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Breaking changes are highlighted with the [BC] prefix.

## Notes on the 0.x version

The 0.x version may have breaking changes (as foreseen in the Semantic Versioning Specification). Nevertheless, efforts
will be taken to minimize this kind of change.

## 0.2.0 (2025-04-24)

### Added

* Support for Android development
* Zimbabwean Gold (`ZWG`) as the new currency of Zimbabwe

### Removed

* [BC] Caching layer
    * The library used [Caffeine](https://github.com/ben-manes/caffeine) to cache some of its objects. However, Caffeine
      [breaks on Android](https://github.com/ben-manes/caffeine/issues/959). This change only breaks if you are
      configuring or disabling the cache through the `configureCache()` or `disableCache()` calls

### Changed

* Percentage dependency to v0.3.0

## 0.1.0 (2024-10-06)

Initial release.
