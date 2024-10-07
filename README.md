# Money

[![Codacy grade](https://img.shields.io/codacy/grade/a6fc46b250fd447d881922b200214d03)](https://app.codacy.com/gh/eriksencosta/money/dashboard)
[![Codacy coverage](https://img.shields.io/codacy/coverage/a6fc46b250fd447d881922b200214d03)](https://app.codacy.com/gh/eriksencosta/money/coverage)

Monetary calculations and allocations made easy.

## Installation

Add Money to your Gradle build script:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.eriksencosta:money:0.1.0")
}
```

If you're using Maven, add to your POM xml file:

```xml
<dependency>
    <groupId>com.eriksencosta</groupId>
    <artifactId>money</artifactId>
    <version>0.1.0</version>
</dependency>
```

Money is not compatible with the Android SDK at the moment.

## Usage

The library provides a powerful yet simple API that makes monetary calculations easy:

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

The library supports arithmetic operations with monetary amounts, calculations with percentages, and allocation, making
it simple to model use cases like installment payments (e.g., buy now, pay later), foreign exchange, investment yields,
and tax collection. Cryptocurrencies are also fully supported out of the box:

```kotlin
val price = 0.01607580 money "BTC"           // BTC 0.01607580
val transactionFee = 1.25.percent()          // 1.5%
val total = price increaseBy transactionFee  // BTC 0.01627675
val installments = total allocate 3          // [BTC 0.00542559, BTC 0.00542558, BTC 0.00542558]

val rate = 62_555.60 money "USD"             // USD 62,555.60
val totalInUsd = total exchange rate         // USD 1005.63
```

[Refer to the documentation](docs/README.md) for more about how to work with monetary amounts, supported currencies,
available operations, rounding, and allocation.

## License

[The Apache Software License, Version 2.0](https://choosealicense.com/licenses/apache/)
