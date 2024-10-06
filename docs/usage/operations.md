## Operations

The library provides methods for mathematical operations with monetary amounts, currency exchange calculations, and
other utilities.

### Mathematical

#### Addition

Two `Money` objects with the same `Currency` may be summed:

```kotlin
val value = 1 money "USD" // USD 1.00
val other = 2 money "USD" // USD 2.00
value + other             // USD 3.00
```

#### Subtraction

Two `Money` objects with the same `Currency` may be subtracted:

```kotlin
val value = 1 money "USD" // USD 1.00
val other = 2 money "USD" // USD 2.00
value - other             // USD -1.00
```

#### Multiplication

A `Money` object may be multiplied by an arbitrary `Number`:

```kotlin
val value = 1.23 money "USD" // USD 1.23
value * 3.14                 // USD 3.86
```

Or by a `Percentage`:

```kotlin
val value = 150 money "USD"   // USD 150.00
val percentage = 10.percent() // 10%
value * percentage            // USD 15.00
```

#### Division

A `Money` object may be divided by an arbitrary `Number`:

```kotlin
val value = 1.23 money "USD" // USD 1.23
value / 3.14                 // USD 0.39
```

#### Negation

A `Money` object may have its amount negated:

```kotlin
val value = 1 money "USD" // USD 1.00
val other = -original     // USD -1.00
```

#### Comparison

Two `Money` objects with the same `Currency` may be compared:

```kotlin
val value = 1 money "USD"
val other = 2 money "USD"
value > other // false
value < other // true
```

#### Ratio

The ratio between two `Money` objects with the same `Currency` may be calculated:

```kotlin
val value = 1.23 money "USD" // USD 1.23
val other = 2.46 money "USD" // USD 2.46
value ratio other            // 0.5
```

#### Increase by a percentage

A `Money` object may be increased by a `Percentage`:

```kotlin
val dueAmount = 1250 money "USD" // USD 1250.00
val lateFine = 5.percent()       // 5%
dueAmount increaseBy lateFine    // USD 1312.50
```

#### Decrease by a percentage

A `Money` object may be decreased by a `Percentage`:

```kotlin
val subtotal = 2355.98 money "USD" // USD 2355.98
val discount = 10.percent()        // 10%
subtotal decreaseBy discount       // USD 2120.38
```

### Currency exchange

A `Money` object may be exchanged to another `Currency`:

```kotlin
val amount = 1 money "USD"    // USD 1.00
val rate = 5.4905 money "BRL" // BRL 5.4905
amount exchange rate          // BRL 5.49
```

### Utilities

#### Zero amount creation

A `Money` object with a zero amount may be created through a `Currency` object:

```kotlin
val usd = Currency of "USD"
usd.zero() // USD 0.00
```

#### Smallest unit creation

A `Money` object with its amount equal to the smallest unit of its `Currency` may be created:

```kotlin
val usd = Currency of "USD"
usd.smallestUnit() // USD 0.01
```

#### Transforming a list of numbers

A list of numerical values may be transformed into a list of `Money`:

```kotlin
val usd = Currency of "USD"
usd money listOf(1, 2, 3) // [USD 1.00, USD 2.00, USD 3.00]
```

## Code examples

The code examples can be found in the
[UsageExamples](../../money/src/test/kotlin/com/eriksencosta/money/UsageExamples.kt) test file.
