## Rounding

By default, operations with `Money` are rounded automatically:

```kotlin
val price = 6.6512 money "USD" // USD 6.6512
val items = 2
val total = price * items      // USD 13.30 (rounded from 13.3024)
```

The amount will always be rounded to the number of minor units (i.e., the decimal places) of the `Currency`:

```kotlin
val price = 6.6512 money "JPY" // JPY 6.6512
val items = 2
val total = price * items      // JPY 13 (rounded from 13.3024)
```

As you can see from the previous examples, the amount is rounded only after an operation. By default, the rounding mode
used is `RoundingMode.HALF_EVEN`. You can choose the mode best suited to your use case when creating a `Money` object:

```kotlin
val price = 6.6512.money("USD", RoundingMode.CEILING)
val items = 2
val total = price * items      // USD 13.31 (rounded from 13.3024)
```

Notice that the resulting `Money` will keep the rounding mode used to round its monetary amount. Also, operations with
two `Money` objects will preserve the rounding mode of the receiving object, i.e., the object where the operation was
invoked:

```kotlin
val subtotal = 123.4321.money("USD", RoundingMode.CEILING) // USD 123.4321
val transactionFee = 0.99.money("USD", RoundingMode.FLOOR) // USD 0.99
val total = subtotal + transactionFee                      // USD 124.43 (rounded from USD 124.4221)
```

Last but not least, you can change the rounding strategy a `Money` object uses. For example, when calculating the
capital gains tax for an investment asset, you may need to collect it by rounding up its amount. To do this, call the
`with()` method:

```kotlin
val grossIncome = 8.43 money "USD"                             // USD 8.43
val taxRate = 12.5.percent()                                   // 12.5%
val capitalGains = grossIncome.with(RoundingMode.UP) * taxRate // USD 1.06 (rounded from USD 1.05375)
```

### Disabling rounding

Rounding can be disabled anytime by calling the `noRounding()` method:

```kotlin
val price = 6.6512 money "USD" // USD 6.6512
val items = 2
price.noRounding() * items     // USD 13.3024
```

Disabling rounding can be important for calculations like investment yield, tax collections, or even simple functions
involving multiple consecutive mathematical operations. For example, a foreign exchange may round the converted amount
only after discounting its fee:

```kotlin
val amount = 70 money "USD"                                         // USD 70.00
val exchangeRate = (5.4905 money "BRL").noRounding()                // BRL 5.4905
val converted = amount exchange exchangeRate                        // BRL 384.335
val exchangeFeeRate = 2.15.percent()                                // 2.15%
val exchangeFee = converted.with(RoundingMode.UP) * exchangeFeeRate // BRL 8.27
val result = converted - exchangeFee                                // BRL 376.065
val actualResult = result.round(RoundingMode.DOWN)                  // BRL 376.06
```

If the same operations were run with rounding enabled, the `actualResult` would be `BRL 376.07`. The difference may seem
small, but it may be
[costly over time](https://slate.com/technology/2019/10/round-floor-software-errors-stock-market-battlefield.html).

## Code examples

The code examples can be found in the
[UsageExamples](../../money/src/test/kotlin/com/eriksencosta/money/UsageExamples.kt) test file.
