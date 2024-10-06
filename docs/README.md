# Documentation

Money is a Kotlin library that makes monetary calculations and allocations easy:

```kotlin
val price = 100 money "USD"                     // USD 100.00
val shipping = 5 money "USD"                    // USD 5.00
val subtotal = price + shipping                 // USD 105.00
val discount = 10.percent()                     // 10%
val total = subtotal decreaseBy discount        // USD 94.50

val ratios = listOf(60.percent(), 40.percent()) // [60%, 40%]

total allocate 2                                // [USD 47.25, USD 47.25]
total allocate ratios                           // [USD 56.70, USD 37.80]
```

The previous example shows at a glance how the library works. Discover more in the usage guide.

## Usage guide

1. [Money and currencies](usage/money-currencies.md): understand how to define monetary amounts and currencies
2. [Operations](usage/operations.md): get a complete picture of the supported operations
3. [Rounding](usage/rounding.md): learn how flexible are the rounding options for your use cases
4. [Allocation](usage/allocation.md): calculate installments correctly by using the allocation features
5. [Performance](usage/performance.md): fine tune the library cache for your performance requirements

## Appendixes

1. [Circulating currencies](appendixes/circulating-currencies.md): details the library support for circulating
   currencies alongside a table with all supported currencies and their data
2. [Cryptocurrencies](appendixes/cryptocurrencies.md): details the library support for cryptocurrencies alongside a
   table with all supported currencies and their data

## API documentation

Browse the [API documentation](https://blog.eriksen.com.br/opensource/money) for further technical details.
