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
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.RoundingMode.DOWN
import java.math.RoundingMode.HALF_EVEN
import java.math.RoundingMode.UP
import kotlin.test.Test
import kotlin.test.assertEquals

class StringTest : Localizable {
    @Suppress("LongMethod")
    @TestFactory
    fun `Create a Money`() = listOf(
        // Circulating currencies
        "USD 1.23" to Money.of(1.23, "USD"),
        "1.23 USD" to Money.of(1.23, "USD"),
        "USD 1,23" to Money.of(123, "USD"),
        "1,23 USD" to Money.of(123, "USD"),
        "USD 1.234" to Money.of(1.234, "USD"),
        "1.234 USD" to Money.of(1.234, "USD"),
        "USD 1.234,56" to Money.of(1.234, "USD"),
        "1.234,56 USD" to Money.of(1.234, "USD"),
        "USD 1,234" to Money.of(1234, "USD"),
        "1,234 USD" to Money.of(1234, "USD"),
        "USD 1,234.56" to Money.of(1234.56, "USD"),
        "1,234.56 USD" to Money.of(1234.56, "USD"),

        // Large sums
        "USD 12345.56" to Money.of(12_345.56, "USD"),
        "12345.56 USD" to Money.of(12_345.56, "USD"),
        "USD 123456.56" to Money.of(123_456.56, "USD"),
        "123456.56 USD" to Money.of(123_456.56, "USD"),
        "USD 1234567.56" to Money.of(1_234_567.56, "USD"),
        "1234567.56 USD" to Money.of(1_234_567.56, "USD"),
        "USD 12345678.56" to Money.of(12_345_678.56, "USD"),
        "12345678.56 USD" to Money.of(12_345_678.56, "USD"),
        "USD 123456789.56" to Money.of(123_456_789.56, "USD"),
        "123456789.56 USD" to Money.of(123_456_789.56, "USD"),

        // Grouping
        "USD 123,456,789.56" to Money.of(123_456_789.56, "USD"),
        "123,456,789.56 USD" to Money.of(123_456_789.56, "USD"),

        // Negative amount
        "USD -1.23" to Money.of(-1.23, "USD"),
        "USD -1,23" to Money.of(-123, "USD"),

        // Cryptocurrencies
        "4H95J0R2X 0.12345678" to Money.of(0.12345678, "4H95J0R2X"),
        "0.12345678 4H95J0R2X" to Money.of(0.12345678, "4H95J0R2X"),
        "BTC 0.12345678" to Money.of(0.12345678, "4H95J0R2X"),
        "0.12345678 BTC" to Money.of(0.12345678, "4H95J0R2X"),
        "WGJSRNZGM 0.123456789" to Money.of(0.123456789, "WGJSRNZGM"),
        "0.123456789 WGJSRNZGM" to Money.of(0.123456789, "WGJSRNZGM"),
        "GRT.e 0.123456789" to Money.of(0.123456789, "WGJSRNZGM"),
        "0.123456789 GRT.e" to Money.of(0.123456789, "WGJSRNZGM"),

        // Large sums
        "ETH 1.123456789123456789" to Money.of(BigDecimal("1.123456789123456789"), "X9J9K872S"),
        "1.123456789123456789 ETH" to Money.of(BigDecimal("1.123456789123456789"), "X9J9K872S"),
        "ETH 12.123456789123456789" to Money.of(BigDecimal("12.123456789123456789"), "X9J9K872S"),
        "12.123456789123456789 ETH" to Money.of(BigDecimal("12.123456789123456789"), "X9J9K872S"),
        "ETH 123.123456789123456789" to Money.of(BigDecimal("123.123456789123456789"), "X9J9K872S"),
        "123.123456789123456789 ETH" to Money.of(BigDecimal("123.123456789123456789"), "X9J9K872S"),
        "ETH 1234.123456789123456789" to Money.of(BigDecimal("1234.123456789123456789"), "X9J9K872S"),
        "1234.123456789123456789 ETH" to Money.of(BigDecimal("1234.123456789123456789"), "X9J9K872S"),
        "ETH 12345.123456789123456789" to Money.of(BigDecimal("12345.123456789123456789"), "X9J9K872S"),
        "12345.123456789123456789 ETH" to Money.of(BigDecimal("12345.123456789123456789"), "X9J9K872S"),
        "ETH 123456.123456789123456789" to Money.of(BigDecimal("123456.123456789123456789"), "X9J9K872S"),
        "123456.123456789123456789 ETH" to Money.of(BigDecimal("123456.123456789123456789"), "X9J9K872S"),
        "ETH 1234567.123456789123456789" to Money.of(BigDecimal("1234567.123456789123456789"), "X9J9K872S"),
        "1234567.123456789123456789 ETH" to Money.of(BigDecimal("1234567.123456789123456789"), "X9J9K872S"),
        "ETH 12345678.123456789123456789" to Money.of(BigDecimal("12345678.123456789123456789"), "X9J9K872S"),
        "12345678.123456789123456789 ETH" to Money.of(BigDecimal("12345678.123456789123456789"), "X9J9K872S"),
        "ETH 123456789.123456789123456789" to Money.of(BigDecimal("123456789.123456789123456789"), "X9J9K872S"),
        "123456789.123456789123456789 ETH" to Money.of(BigDecimal("123456789.123456789123456789"), "X9J9K872S"),

        // Grouping
        "ETH 123,456,789.123456789123456789" to Money.of(BigDecimal("123456789.123456789123456789"), "X9J9K872S"),
        "123,456,789.123456789123456789 ETH" to Money.of(BigDecimal("123456789.123456789123456789"), "X9J9K872S"),

        // Negative amount
        "ETH -0.123456789123456789" to Money.of(BigDecimal("-0.123456789123456789"), "X9J9K872S"),
        "X9J9K872S -0.123456789123456789" to Money.of(BigDecimal("-0.123456789123456789"), "X9J9K872S"),
    )
        .map { (string, expected) ->
            dynamicTest("given \"$string\" when converted then I should get $expected") {
                assertEquals(expected, string.money())
            }
        }

    @TestFactory
    fun `Create a Money with RoundingMode`() = listOf(
        Triple("USD 1.23", UP, Money.of(1.23, "USD", UP)),
        Triple("USD 1,23", DOWN, Money.of(123, "USD", DOWN)),
    )
        .map { (string, mode, expected) ->
            dynamicTest("given \"$string\" and RoundingMode $mode when converted then I should get $expected") {
                assertEquals(expected, string.money(mode))
            }
        }

    @TestFactory
    fun `Create a Money with minor units`() = listOf(
        Triple("USD 1.2345", 2, Money.of(1.2345, "USD")),
        Triple(
            "USD 1.2345",
            4,
            Money.of(1.2345, Currency.of("USD").toCustomCurrency(4), Rounding.to(4, HALF_EVEN))
        ),
        Triple(
            "JPY 1.23",
            2,
            Money.of(1.23, Currency.of("JPY").toCustomCurrency(2), Rounding.to(2, HALF_EVEN))
        ),
    )
        .map { (string, minorUnits, expected) ->
            dynamicTest("given \"$string\" and $minorUnits minor units when converted then I should get $expected") {
                assertEquals(expected, string money minorUnits)
            }
        }

    @TestFactory
    fun `Create a Money from localized string`() = listOf(
        Triple("USD 1.234,56", brazil, Money.of(1234.56, "USD")),
        Triple("USD 1,234.56", brazil, Money.of(1.234, "USD")),
        Triple("USD 1 234,56", brazil, Money.of(1234.56, "USD")),
        Triple("USD 1 234.56", brazil, Money.of(123_456, "USD")),
        Triple("USD 1'234,56", brazil, Money.of(1234.56, "USD")),
        Triple("USD 1'234.56", brazil, Money.of(123_456, "USD")),
        Triple("BTC 123,456,789.12345678", brazil, Money.of(123.456, "4H95J0R2X")),
        Triple("123,456,789.12345678 BTC", brazil, Money.of(123.456, "4H95J0R2X")),

        Triple("USD 1.234,56", uk, Money.of(1.234, "USD")),
        Triple("USD 1,234.56", uk, Money.of(1234.56, "USD")),
        Triple("USD 1 234,56", uk, Money.of(123_456, "USD")),
        Triple("USD 1 234.56", uk, Money.of(1234.56, "USD")),
        Triple("USD 1'234,56", uk, Money.of(123_456, "USD")),
        Triple("USD 1'234.56", uk, Money.of(1234.56, "USD")),
        Triple("BTC 123,456,789.12345678", uk, Money.of(123_456_789.12345678, "4H95J0R2X")),
        Triple("123,456,789.12345678 BTC", uk, Money.of(123_456_789.12345678, "4H95J0R2X")),

        Triple("USD 1.234,56", france, Money.of(1234.56, "USD")),
        Triple("USD 1,234.56", france, Money.of(1.23456, "USD")),
        Triple("USD 1 234,56", france, Money.of(1234.56, "USD")),
        Triple("USD 1 234.56", france, Money.of(123_456, "USD")),
        Triple("USD 1'234,56", france, Money.of(1234.56, "USD")),
        Triple("USD 1'234.56", france, Money.of(123_456, "USD")),
        Triple("BTC 123,456,789.12345678", france, Money.of(123.456, "4H95J0R2X")),
        Triple("123,456,789.12345678 BTC", france, Money.of(123.456, "4H95J0R2X")),

        Triple("USD 1.234,56", switzerland, Money.of(1.23456, "USD")),
        Triple("USD 1,234.56", switzerland, Money.of(1234.56, "USD")),
        Triple("USD 1 234,56", switzerland, Money.of(123_456, "USD")),
        Triple("USD 1 234.56", switzerland, Money.of(1234.56, "USD")),
        Triple("USD 1'234,56", switzerland, Money.of(123_456, "USD")),
        Triple("USD 1'234.56", switzerland, Money.of(1234.56, "USD")),
        Triple("BTC 123,456,789.12345678", switzerland, Money.of(123_456_789.12345678, "4H95J0R2X")),
        Triple("123,456,789.12345678 BTC", switzerland, Money.of(123_456_789.12345678, "4H95J0R2X")),
    )
        .map { (string, locale, expected) ->
            dynamicTest("given \"$string\" when converted in the locale $locale then I should get $expected") {
                assertEquals(expected, string money locale)
            }
        }

    @Test
    fun `Throw exception when currency is unparsable`() = assertThrows<Exception> {
        "USD$123".money()
    }.run {
        val expected = "No parsable currency was found in \"USD\$123\". It must be a standardized code like ISO " +
            "4217 for circulating currencies (e.g., USD, EUR, JPY, BRL) or ISO 24165 for cryptocurrencies (e.g., " +
            "4H95J0R2X and X9J9K872S). Cryptocurrencies may also be represented by their secondary codes (e.g., " +
            "BTC and ETH). Examples of valid values for the current locale (en): USD 1,234.56, USD 123.45, " +
            "USD 12.34, USD 1.23"

        assertEquals(expected, message)
    }

    @Test
    fun `Throw exception when string is empty`() = assertThrows<Exception> { "".money() }.run {
        val expected = "No parsable currency was found in \"\". It must be a standardized code like ISO " +
            "4217 for circulating currencies (e.g., USD, EUR, JPY, BRL) or ISO 24165 for cryptocurrencies (e.g., " +
            "4H95J0R2X and X9J9K872S). Cryptocurrencies may also be represented by their secondary codes (e.g., " +
            "BTC and ETH). Examples of valid values for the current locale (en): USD 1,234.56, USD 123.45, " +
            "USD 12.34, USD 1.23"

        assertEquals(expected, message)
    }

    @Test
    fun `Throw localized exception when currency is unparsable and locale is provided`() = assertThrows<Exception> {
        "USD$123".money(brazil)
    }.run {
        val expected = "No parsable currency was found in \"USD\$123\". It must be a standardized code like ISO " +
            "4217 for circulating currencies (e.g., USD, EUR, JPY, BRL) or ISO 24165 for cryptocurrencies (e.g., " +
            "4H95J0R2X and X9J9K872S). Cryptocurrencies may also be represented by their secondary codes (e.g., " +
            "BTC and ETH). Examples of valid values for the current locale (pt_BR): BRL 1.234,56, BRL 123,45, " +
            "BRL 12,34, BRL 1,23"

        assertEquals(expected, message)
    }

    @TestFactory
    fun `Throw exception when amount is unparsable`() = listOf(
        "JPY \$" to "No parsable amount was found in \"JPY \$\". Examples of valid values for the " +
            "current currency and locale (en): JPY 1,234, JPY 123, JPY 12, JPY 1",
        "USD \$" to "No parsable amount was found in \"USD \$\". Examples of valid values for the " +
            "current currency and locale (en): USD 1,234.56, USD 123.45, USD 12.34, USD 1.23",
        "UYW \$" to "No parsable amount was found in \"UYW \$\". Examples of valid values for the " +
            "current currency and locale (en): UYW 1,234.5678, UYW 123.4567, UYW 12.3456, UYW 1.2345",
        "BTC \$" to "No parsable amount was found in \"BTC \$\". Examples of valid values for the " +
            "current currency and locale (en): BTC 1,234.56789123, BTC 123.45678912, BTC 12.34567891, BTC 1.23456789",
        "BTC \$" to "No parsable amount was found in \"BTC \$\". Examples of valid values for the " +
            "current currency and locale (en): BTC 1,234.56789123, BTC 123.45678912, BTC 12.34567891, BTC 1.23456789",
    ).map { (string, expected) ->
        dynamicTest("given \"$string\" when converted then I should get an IllegalArgumentException") {
            assertThrows<IllegalArgumentException> { string.money() }.run { assertEquals(expected, message) }
        }
    }

    @Test
    fun `Throw localized exception when amount is unparsable and locale is provided`() =
        assertThrows<IllegalArgumentException> { "USD \$".money(brazil) }.run {
            val expected = "No parsable amount was found in \"USD \$\". Examples of valid values for the " +
                "current currency and locale (pt_BR): USD 1.234,56, USD 123,45, USD 12,34, USD 1,23"

            assertEquals(expected, message)
        }
}
