/*
 * This file is part of the Money package.
 *
 * Copyright (c) Eriksen Costa <eriksencosta@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.eriksencosta.money

import com.eriksencosta.math.common.Rounding
import com.eriksencosta.money.Money.Factory.defaultRoundingMode
import com.eriksencosta.money.currency.CurrencyResolution
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Currency as JavaCurrency

private const val DEFAULT_CURRENCY_FOR_UNPARSABLE_EXCEPTION = "USD"

/**
 * Creates a [Money] based on this [String]. Example:
 *
 * ```
 * val oneDollar = "USD 1.00".money()
 * oneDollar + oneDollar // USD 2.00
 * ```
 *
 * Alternatively:
 *
 * ```
 * val oneDollar = "1.00 USD".money()
 * ```
 *
 * The currency code may be an ISO 4217 code (three-letter uppercase, e.g., `USD` for United States Dollar, `EUR` for
 * Euro, and so on) for circulating currencies, an ISO 24165 (Digital Token Identifier, e.g., `4H95J0R2X` for Bitcoin
 * and `X9J9K872S` for Ethereum) or a non-standardized code (like `BTC` for Bitcoin and `ETH` for Ethereum) for
 * cryptocurrencies.
 *
 * The monetary amount parsing is locale-aware and uses the locale of the running environment by default. The parsing
 * will behave like in the following example for a locale that uses "." as a decimal separator and "," as a grouping
 * separator:
 *
 * ```
 * "USD 1.23".money()     // USD 1.23
 * "USD 1,234.56".money() // USD 1234.56
 * "USD 1,23".money()     // USD 123
 * ```
 *
 * If the locale uses "," as a decimal separator and "." as a grouping separator, the parsing will behave like in the
 * following example:
 *
 * ```
 * "USD 1.23".money()     // USD 123
 * "USD 1,234.56".money() // USD 1.234
 * "USD 1,23".money()     // USD 1.23
 * ```
 *
 * @receiver[String]
 * @param[mode] The rounding mode to configure the [Rounding] strategy.
 * @param[locale] The [Locale] used to parse the amount string. Defaults to the [Locale] of the running environment.
 * @param[minorUnits] The minor units (i.e., the decimal places) of the currency. If defined, the returned [Money]
 *   will use a [CustomCurrency] based on a [StandardizedCurrency].
 * @throws[IllegalArgumentException] When no currency code is identified in the string.
 * @throws[IllegalArgumentException] When no currency is found for a currency code.
 * @throws[IllegalArgumentException] When [minorUnits] is lower than a zero.
 * @throws[IllegalArgumentException] When the monetary amount is not parsable.
 * @return A [Money].
 */
public fun String.money(
    mode: RoundingMode = defaultRoundingMode,
    locale: Locale = Locale.getDefault(),
    minorUnits: Int? = null
): Money = trim().let { value ->
    CurrencyResolution.thorough.identify(value).let { currencyCode ->
        when (currencyCode.isNotEmpty()) {
            true -> ParseAmount(this, currencyCode, minorUnits, mode, locale).money()
            false -> unparsableCurrency(this, locale)
        }
    }
}

/**
 * Shorthand for [String.money].
 */
public infix fun String.money(locale: Locale): Money = money(defaultRoundingMode, locale)

/**
 * Shorthand for [String.money].
 */
public infix fun String.money(minorUnits: Int): Money = money(defaultRoundingMode, minorUnits = minorUnits)

internal data class ParseAmount(
    val amount: String,
    val currencyCode: String,
    val minorUnits: Int?,
    val mode: RoundingMode,
    val locale: Locale
) {
    private val currency = Currency.of(currencyCode).toCustomCurrency(minorUnits)

    fun money(): Money = runCatching {
        DecimalFormatSymbols.getInstance(locale).let { symbols ->
            val parsed = DecimalFormat().apply {
                isParseBigDecimal = true
                decimalFormatSymbols = symbols
            }.parse(amount.sanitizeAmount(symbols)) as BigDecimal

            Money.of(parsed, currency, mode)
        }
    }.fold({ it }) {
        unparsableAmount(amount, currencyCode, currency, locale)
    }

    private fun String.sanitizeAmount(symbols: DecimalFormatSymbols): String = run {
        val allowedSeparators = "%s%s".format(symbols.groupingSeparator, symbols.decimalSeparator).replace("-", "\\-")

        replace(currencyCode, "")
            .trim()
            .replace(Regex("[^0-9\\-$allowedSeparators]"), "")
    }

    private fun unparsableAmount(input: String, currencyCode: String, currency: Currency, locale: Locale): Nothing =
        throw IllegalArgumentException(
            "No parsable amount was found in \"$input\". Examples of valid values for the current currency and " +
                "locale ($locale): ${validExamplesFor(currencyCode, currency, locale)}"
        )
}

private fun unparsableCurrency(input: String, locale: Locale): Nothing {
    val currency = runCatching { JavaCurrency.getInstance(locale) }.fold({ it.currencyCode }) {
        DEFAULT_CURRENCY_FOR_UNPARSABLE_EXCEPTION
    }.let { Currency.of(it) }

    throw IllegalArgumentException(
        "No parsable currency was found in \"$input\". It must be a standardized code like ISO 4217 for circulating " +
            "currencies (e.g., USD, EUR, JPY, BRL) or ISO 24165 for cryptocurrencies (e.g., 4H95J0R2X and X9J9K872S" +
            "). Cryptocurrencies may also be represented by their secondary codes (e.g., BTC and ETH). Examples of " +
            "valid values for the current locale ($locale): ${validExamplesFor(currency.code, currency, locale)}"
    )
}

private fun validExamplesFor(currencyCode: String, currency: Currency, locale: Locale): String = run {
    val formatter = formatterFor(currency, locale)

    @Suppress("MagicNumber")
    listOf(1234.5678912345, 123.4567891234, 12.3456789123, 1.2345678912).joinToString(", ") {
        val formatted = formatter.format(it)
        "$currencyCode $formatted"
    }
}

private fun formatterFor(currency: Currency, locale: Locale) = DecimalFormatSymbols.getInstance(locale).let { symbols ->
    val repeating = when (currency.minorUnits) {
        0 -> ""
        else -> "." + "0".repeat(currency.minorUnits)
    }

    DecimalFormat("#,###$repeating").apply {
        isParseBigDecimal = true
        decimalFormatSymbols = symbols
        roundingMode = RoundingMode.DOWN
    }
}
