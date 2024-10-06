# Money and currencies

Manipulating monetary amounts is a common computing chore. However, no mainstream language has a first-class data type
for representing money, it's up to programmers to code abstractions for it. This isn't an issue until dealing with
rounding issues from operations like installment calculation, currency exchange, or even simple things like fee
processing and tax collection.

The Money library provides classes representing monetary amounts and currencies designed to be used like any Kotlin
built-in type. Learn how to use them and let the days of hand-coding monetary calculations in the past.

## Creating instances of Money

To create a `Money` instance, you need to provide a currency code and the monetary amount:

```kotlin
Money.of(1, "USD") // USD 1.00
Money.of(1, "EUR") // EUR 1.00
```

To make it handy to work with monetary amounts, the library provides convenience extension functions to create `Money`
instances from `Number` and `String` objects:

```kotlin
1.99 money "USD"  // USD 1.99
11.01 money "EUR" // EUR 11.01
```

The string version is locale-aware, parsing the string value according to the `Locale` set in the JVM. For example, in a
JVM with the `Locale` set to `Locale.US`, a comma will be parsed as the grouping separator whereas in a JVM set to
`Locale.ITALY` will parse a comma as the decimal separator:

```kotlin
val us = Locale.US
val italy = Locale.ITALY

"USD 1,234.56" money us    // USD 1234.56
"USD 1.234,56" money us    // USD 1.234
"USD 1,234.56" money italy // USD 1.234
"USD 1.234,56" money italy // USD 1234.56
```

Finally, you can also create `Money` instances through a `Currency`:

```kotlin
val usd = Currency of "USD"
val eur = Currency of "EUR"
val jpy = Currency of "JPY"

usd money 1.99 // USD 1.99
eur money 2.99 // EUR 2.99
jpy money 3999 // JPY 3999
```

## Currencies

The library supports standardized, de-facto, and historical currencies alongside cryptocurrencies:

```kotlin
val usd = Currency of "USD" // US Dollar
val eur = Currency of "EUR" // Euro
val bre = Currency of "BRE" // Brazilian Cruzeiro (1990–1993)
val xau = Currency of "XAU" // Gold
val btc = Currency of "BTC" // Bitcoin
val eth = Currency of "ETH" // Ethereum
```

To create a circulating currency (i.e., a national, supranational, de-facto, or historical currency), pass a
three-letter code or its secondary numerical code (both codes are
[standardized by ISO 4217](../appendixes/circulating-currencies.md#supported-currencies)):

```kotlin
val usd = Currency of "USD" // US Dollar
val eur = Currency of "978" // Euro
```

For cryptocurrencies, pass a 9-alphanumerical code (also known as Digital Token Identifier as
[standardized by ISO 24165-1](../appendixes/cryptocurrencies.md#supported-currencies)) or the publicly-known short name:

```kotlin
val polkadot = Currency of "P5B46MFPP" // Polkadot
val dogecoin = Currency of "DOGE"      // Dogecoin
```

### Standardized and custom currencies

Internally, the library models `Currency` as a `StandardizedCurrency` or `CustomCurrency` immutable object:

* `StandardizedCurrency` represents a standardized circulating currency or cryptocurrency. The concrete implementations
  are `CirculatingCurrency` for currencies standardized by ISO 4217 and `CryptoCurrency` for cryptocurrencies
  standardized by ISO 24165-1
* `CustomCurrency` represents a currency fully defined by the library user. It can be used to model monetary
  calculations for specific use cases, such as fuel prices and exchange rates

### Supported currencies

The supported currencies table lists the currency codes and other relevant data for
[circulating currencies](../appendixes/circulating-currencies.md#supported-currencies) and
[cryptocurrencies](../appendixes/cryptocurrencies.md#supported-currencies).

### Querying currency data

You may query additional currency data through the `Currency` public properties:

```kotlin
val usd = Currency of "USD"

usd.code          // USD       -- primary code for identification purposes
usd.secondaryCode // 840       -- secondary code for identification purposes
usd.name          // US Dollar -- the currency name
usd.symbol        // $         -- the graphical symbol of the currency
usd.type          // TENDER    -- the type of the currency
usd.minorUnits    // 2         -- the number of decimal places of the currency
```

The semantics for `code`, `secondaryCode`, and `symbol` have differences depending on the subclass of `Currency` as
summarized in the following table:

| Attribute       | CirculatingCurrency                                              | CryptoCurrency                                                       | CustomCurrency                             |
|-----------------|------------------------------------------------------------------|----------------------------------------------------------------------|--------------------------------------------|
| `code`          | A unique three-letter code (ISO 4217) set by the library         | A unique 9-alphanumerical code (ISO 24165-1) set by the library      | An arbitrary value set by the library user |
| `secondaryCode` | A three-digit numeric code (ISO 4217) set by the library         | A publicly known short name of the cryptocurrency set by the library | An arbitrary value set by the library user |
| `symbol`        | The official graphical symbol of the currency set by the library | The official graphical symbol of the currency set by the library     | An arbitrary value set by the library user |

### Minor units and rounding

Currencies may have minor units (i.e., the number of decimal places). For example, USD (US Dollar) has two minor units
while JPY (Japanese Yen) has no minor unit. The minor units affect the result of mathematical operations done in a
`Money` object due to rounding. The library automatically rounds the result of a mathematical operation respecting the
minor units of the `Currency`:

```kotlin
val price = 6.6512 money "USD" // USD 6.6512
val total = price * 2
val total = price * items      // USD 13.30 (rounded from 13.3024)
```

The relation between minor units and rounding is more evident when dealing with a `Currency` that has no minor unit:

```kotlin
val price = 6.6512 money "JPY" // JPY 6.6512
val items = 2
val total = price * items      // JPY 13 (rounded from 13.3024)
```

You can take this behavior to your advantage when dealing with prices, for example. Gas prices have three decimal places
in countries like the USA and Brazil. In the USA, it's up to retailers to round the final price. Usually the
[rounding strategy](https://www.convenience.org/Topics/Fuels/Why-Gas-Is-Priced-Using-Fractions-of-a-Penny) used by the
dispensers in the USA is `RoundingMode.HALF_UP`:

```kotlin
val price = 3.557.money("USD", RoundingMode.HALF_UP)
val gallons = 5
val total = price * gallons // USD 17.79 (rounded up from USD 17.785)
```

In Brazil, the law mandates that the [third decimal place be discarded](https://tinyurl.com/gas-price-brazil-rounding)
in the final price to the customer, effectively truncating the due amount:

```kotlin
val price = 5.968.money("BRL", RoundingMode.DOWN)
val liters = 19
val total = price * liters // BRL 113.39 (rounded down from BRL 113.392)
```

In common, both results end up in two decimal places (both USD and BRL have two minor units). If you need to model
monetary amounts with more decimal places, you can use `CustomCurrency`, as further explained in the next section. Read
more about rounding in the [Rounding](rounding.md) guide.

### Custom currencies

If you need to represent a currency that is not standardized, like a new cryptocurrency or a circulating currency from
an unrecognized or partially recognized country, create a `CustomCurrency` using the `custom()` method:

```kotlin
val custom = Currency.custom("CUSTOM", 4)
val price = 1.2345 money custom
val items = 4
val total = price * items // CUSTOM 4.9380
```

You can fully customize the attributes by using a code block:

```kotlin
val custom = Currency.custom("CUSTOM", 4) {
    secondaryCode = "CSTC"
    name = "CustomCoin"
    symbol = "C$"
    type = CryptoCurrency.Type.AUXILIARY
}
```

Another use case for custom currencies is to model exchange rates. If you want the result of `Money` calculations
preserving the extra decimal places, create a `CustomCurrency` based on an existing `StandardizedCurrency`:

```kotlin
val usd = Currency.of("USD")
val standardBrl = Currency.of("BRL")
val exchangeBrl = Currency.of("BRL").toCustomCurrency(4)

val amount = 1 money usd
val standardRate = 5.49 money standardBrl
val exchangeRate = 5.4905 money exchangeBrl

amount exchange standardRate // BRL 5.49
amount exchange exchangeRate // BRL 5.4905
```

## Code examples

The code examples can be found in the
[UsageExamples](../../money/src/test/kotlin/com/eriksencosta/money/UsageExamples.kt) test file.
