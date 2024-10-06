## Performance

The library was designed to minimize the cycles needed for monetary calculations. Also, the required currency data is
packaged into the library, keeping the number of dependencies at bay and reducing its footprint.

The library has some optimizations for the currency data loading as well, striking a balance between memory usage and
performance by using caching. You can configure this cache by calling `configureCache()`:

```kotlin
configureCache {
    maximumItems = 100
    expirationTime = 2
    expirationTimeUnit = TimeUnit.HOURS
}

Currency of "USD" === Currency of "USD" // true
```

The previous example will configure the cache to store up to 100 `Currency` objects, and a cached object will live for
up to two hours. If you want to disable the cache, call `disableCache()`:

```kotlin
disableCache()

Currency of "USD" === Currency of "USD" // false
```

## Code examples

The code examples can be found in the
[UsageExamples](../../money/src/test/kotlin/com/eriksencosta/money/UsageExamples.kt) test file.
