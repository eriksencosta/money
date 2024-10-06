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

import com.eriksencosta.money.CirculatingCurrency.Type.HISTORICAL
import com.eriksencosta.money.CirculatingCurrency.Type.OTHER
import com.eriksencosta.money.CirculatingCurrency.Type.TENDER
import com.eriksencosta.money.CryptoCurrency.Type.AUXILIARY
import com.eriksencosta.money.CryptoCurrency.Type.NATIVE
import com.eriksencosta.money.CustomCurrency.Type.CUSTOM
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.RoundingMode.CEILING
import java.math.RoundingMode.DOWN
import java.math.RoundingMode.UP
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class CurrencyTest {
    private val circulatingCurrencies = listOf(
        "BRL" to CirculatingCurrency("BRL", "986", "Brazilian Real", "R$", TENDER, 2),
        "BRE" to CirculatingCurrency("BRE", "076", "Brazilian Cruzeiro (1990–1993)", "BRE", HISTORICAL, 2),
        "KRW" to CirculatingCurrency("KRW", "410", "South Korean Won", "₩", TENDER, 0),
        "USD" to CirculatingCurrency("USD", "840", "US Dollar", "$", TENDER, 2),
        "XXX" to CirculatingCurrency("XXX", "999", "Unknown Currency", "XXX", OTHER, 2),
    )

    private val cryptocurrencies = listOf(
        "4H95J0R2X" to CryptoCurrency("4H95J0R2X", "BTC", "Bitcoin", "₿", NATIVE, 8),
        "X9J9K872S" to CryptoCurrency("X9J9K872S", "ETH", "Ethereum Ether", "Ξ", NATIVE, 18),
        "JGNDNLNNX" to CryptoCurrency("JGNDNLNNX", "CEL", "Celsius", "CEL", AUXILIARY, 4),
        "ZFP9K2W9F" to CryptoCurrency("ZFP9K2W9F", "USDT", "Tether USD Token", "₮", AUXILIARY, 6),
    )

    private val currencies = circulatingCurrencies + cryptocurrencies

    @TestFactory
    fun `Create a StandardizedCurrency`() = currencies
        .map { (code, expected) ->
            dynamicTest("given $code then I should get a StandardizedCurrency instance") {
                assertEquals(expected, Currency.of(code))
            }
        }

    @TestFactory
    fun `Create a CirculatingCurrency`() = circulatingCurrencies
        .map { (code, expected) ->
            dynamicTest("given $code then I should get a CirculatingCurrency instance") {
                assertEquals(expected, Currency.circulating(code))
            }
        }

    @TestFactory
    fun `Create a CryptoCurrency`() = cryptocurrencies
        .map { (code, expected) ->
            dynamicTest("given $code then I should get a CryptoCurrency instance") {
                assertEquals(expected, Currency.crypto(code))
            }
        }

    @Test
    fun `Throw exception when no Currency is found for a given code`() {
        assertThrows<IllegalArgumentException> { Currency.of("UNDEFINED_CURRENCY_CODE") }.run {
            assertEquals(expectedExceptionMessage("UNDEFINED_CURRENCY_CODE"), message)
        }
    }

    @Test
    fun `Throw exception when code for CirculatingCurrency is not defined`() {
        assertThrows<IllegalArgumentException> { Currency.circulating("WWW") }.run {
            assertEquals(expectedExceptionMessage("WWW"), message)
        }
    }

    @Test
    fun `Throw exception when code for CryptoCurrency is not defined`() {
        assertThrows<IllegalArgumentException> { Currency.crypto("CRYPTO") }.run {
            assertEquals(expectedExceptionMessage("CRYPTO"), message)
        }
    }

    @Test
    fun `Create a CustomCurrency`() {
        val expected = CustomCurrency("WWW", "", "", "WWW", CUSTOM, 6)

        assertEquals(expected, Currency.custom("WWW", 6))
    }

    @Test
    fun `Create a fully configured CustomCurrency`() {
        val expected = CustomCurrency("WWW", "WEB", "World Wide Web Token", "WWW$", NATIVE, 8)

        val currency = Currency.custom("WWW", 8) {
            secondaryCode = "WEB"
            name = "World Wide Web Token"
            symbol = "WWW$"
            type = CryptoCurrency.Type.NATIVE
        }

        assertEquals(expected, currency)
    }

    @Test
    fun `Create a CustomCurrency based on a CirculatingCurrency`() {
        val expected = CustomCurrency("BRE", "076", "Brazilian Cruzeiro (1990–1993)", "BRE", HISTORICAL, 0)

        assertEquals(expected, Currency.of("BRE").toCustomCurrency(0))
    }

    @Test
    fun `Create a CustomCurrency based on a CryptoCurrency`() {
        val expected = CustomCurrency("4H95J0R2X", "BTC", "Bitcoin", "₿", NATIVE, 12)

        assertEquals(expected, Currency.of("4H95J0R2X").toCustomCurrency(12))
    }

    @Test
    fun `Throw exception when the minor units for a custom Currency is lower than zero`() {
        assertThrows<IllegalArgumentException> { Currency.custom("BRE", -1) }.run {
            assertEquals("The currency minor units must be greater than or equal 0", message)
        }
    }

    @Test
    fun `Order a collection of Currency`() {
        val currencies = listOf(
            Currency.custom("JPY", 2),
            Currency.of("USD"),
            Currency.of("EUR"),
            Currency.custom("BRL", 4),
            Currency.of("JPY"),
            Currency.of("BRL"),
            Currency.custom("USD", 4),
            Currency.custom("USD", 3),
            Currency.custom("USD", 0),
        )

        val expected = listOf(
            Currency.of("BRL"),
            Currency.custom("BRL", 4),
            Currency.of("EUR"),
            Currency.of("JPY"),
            Currency.custom("JPY", 2),
            Currency.custom("USD", 0),
            Currency.of("USD"),
            Currency.custom("USD", 3),
            Currency.custom("USD", 4),
        )

        assertEquals(expected, currencies.sorted())
    }

    @Test
    fun `Check for equality`() {
        // Currency's factory methods cache the returned objects. Reset the cache to prevent flaky execution, with
        // failing assertSame() due to eviction.
        Currency.resetCache()

        // It's important to cover all branches as Money depends on Currency for its equality check. A correct
        // structural equality implementation is important as both are value objects.
        val currency1 = Currency.of("USD")
        val currency2 = Currency.of("USD")
        val currency3 = CirculatingCurrency("USD", "840", "US Dollar", "$", TENDER, 2)
        val currency4 = Currency.of("EUR")

        val currency5 = Currency.of("4H95J0R2X") // Bitcoin
        val currency6 = Currency.of("4H95J0R2X")
        val currency7 = CryptoCurrency("4H95J0R2X", "BTC", "Bitcoin", "₿", NATIVE, 8)
        val currency8 = Currency.of("X9J9K872S") // Ethereum

        val currency9 = Currency.of("USD").toCustomCurrency(2)
        val currency10 = Currency.of("USD").toCustomCurrency(2)
        val currency11 = CustomCurrency("USD", "840", "US Dollar", "$", TENDER, 2)
        val currency12 = Currency.of("EUR").toCustomCurrency(2)

        equalityAssertions(currency1, currency2, currency3, currency4, "USD")
        equalityAssertions(currency5, currency6, currency7, currency8, "4H95J0R2X")
        equalityAssertions(currency9, currency10, currency11, currency12, "BRL")

        // CustomCurrency branches.
        val currency13 = CustomCurrency("BRL", "986", "Brazilian Real", "R$", TENDER, 2)
        val currency14 = CustomCurrency("BRL", "013", "Brazilian Real", "R$", TENDER, 2)
        val currency15 = CustomCurrency("BRL", "986", "Real", "R$", TENDER, 2)
        val currency16 = CustomCurrency("BRL", "986", "Brazilian Real", "BR$", TENDER, 2)
        val currency17 = CustomCurrency("BRL", "986", "Brazilian Real", "R$", OTHER, 2)
        val currency18 = CustomCurrency("BRL", "986", "Brazilian Real", "R$", TENDER, 4)

        assertNotEquals(currency13, currency14, "Different value: secondaryCode")
        assertNotEquals(currency13, currency15, "Different value: name")
        assertNotEquals(currency13, currency16, "Different value: symbol")
        assertNotEquals(currency13, currency17, "Different value: type")
        assertNotEquals(currency13, currency18, "Different value: minorUnits")
    }

    @TestFactory
    fun `Calculate the hash code`() = listOf(
        Currency.of("USD") to -371_013_181,
        Currency.of("EUR") to 1_602_666_982,
        Currency.of("BTC") to 774_632_354,

        // The different hash codes lies in the first object being fully configured while the second object lacks data
        // for secondary code, name, and symbol.
        Currency.of("BRL").toCustomCurrency(4) to 1_746_739_242,
        Currency.custom("BRL", 4) to -500_041_612,
    )
        .map { (currency, expected) ->
            dynamicTest("given currency for $currency then the hash code should be $expected") {
                assertEquals(expected, currency.hashCode())
            }
        }

    @TestFactory
    fun `Convert Currency to string`() = currencies.map { it.second to it.first }
        .map { (currency, expected) ->
            dynamicTest("given $currency when I convert it a string then I should get $expected") {
                assertEquals(expected, currency.toString())
            }
        }

    @TestFactory
    fun `Convert Currency to a detailed string`() = listOf(
        currencies.get("USD") to
            "CirculatingCurrency[code=USD secondaryCode=840 name=US Dollar symbol=\$ type=TENDER minorUnits=2]",
        currencies.get("4H95J0R2X") to
            "CryptoCurrency[code=4H95J0R2X secondaryCode=BTC name=Bitcoin symbol=₿ type=NATIVE minorUnits=8]",
    )
        .map { (currency, expected: String) ->
            dynamicTest("given $currency when I convert it a string then I should get $expected") {
                assertEquals(expected, currency.toDetailedString())
            }
        }

    @Test
    fun `Create a Money from a Currency`() {
        val eth = Currency.of("ETH")
        val jpy = Currency.of("JPY")
        val usd = Currency.of("USD")

        assertEquals(Money.of(-1.00, "USD"), usd money -1.00)
        assertEquals(Money.of(0.00, "USD", UP), usd.money(0.00, UP))
        assertEquals(Money.of(1000, "JPY"), jpy money 1000)
        assertEquals(Money.of(2000, "JPY", UP), jpy.money(2000, UP))
        assertEquals(Money.of(0.3333333333333333, "ETH"), eth money 1.00 / 3)
        assertEquals(
            Money.of(BigDecimal("0.123456789123456789"), eth.toCustomCurrency(9), UP),
            eth.toCustomCurrency(9).money(BigDecimal("0.123456789123456789"), UP)
        )
    }

    @Test
    fun `Transform a collection of Number to Money from a Currency`() {
        val usd = Currency.of("USD")

        val expected = listOf(
            Money.of(1.00, "USD"),
            Money.of(12.50, "USD"),
            Money.of(100.00, "USD"),
        )

        assertEquals(expected, usd money listOf(1, 12.5, 100))
    }

    @Test
    fun `Transform a collection of Number to Money from a Currency with RoundingMode`() {
        val usd = Currency.of("USD")

        val expected = listOf(
            Money.of(1.00, "USD", DOWN),
            Money.of(12.50, "USD", DOWN),
            Money.of(100.00, "USD", DOWN),
        )

        assertEquals(expected, usd.money(listOf(1, 12.5, 100), DOWN))
    }

    @Test
    fun `Create a Money for a zero amount`() {
        val eth = Currency.of("ETH")
        val jpy = Currency.of("JPY")
        val usd = Currency.of("USD")

        assertEquals(Money.of(0.00, "USD"), usd.zero())
        assertEquals(Money.of(0.00, "USD", CEILING), usd.zero(CEILING))
        assertEquals(Money.of(0, "JPY"), jpy.zero())
        assertEquals(Money.of(0, "JPY", UP), jpy.zero(UP))
        assertEquals(Money.of(0.000000000000000000, "ETH"), eth.zero())
        assertEquals(Money.of(0.000000000000000000, "ETH", DOWN), eth.zero(DOWN))
    }

    @Test
    fun `Create a Money representing the smallest unit of the Currency`() {
        val eth = Currency.of("ETH")
        val jpy = Currency.of("JPY")
        val usd = Currency.of("USD")

        assertEquals(Money.of(0.01, "USD"), usd.smallestUnit())
        assertEquals(Money.of(0.01, "USD", CEILING), usd.smallestUnit(CEILING))
        assertEquals(Money.of(1, "JPY"), jpy.smallestUnit())
        assertEquals(Money.of(1, "JPY", UP), jpy.smallestUnit(UP))
        assertEquals(Money.of(0.000000000000000001, "ETH"), eth.smallestUnit())
        assertEquals(Money.of(0.000000000000000001, "ETH", DOWN), eth.smallestUnit(DOWN))
    }

    private fun equalityAssertions(
        base: Currency,
        sameByReference: Currency,
        sameByValue: Currency,
        different: Currency,
        differentCurrencyCode: String
    ) {
        assertEquals(base, base, "Same instance")
        assertSame(base, base, "Same instance")

        // This won't always hold true when missing the cache.
        assertEquals(base, sameByReference, "Same instance (cache)")
        assertSame(base, sameByReference, "Same instance (cache)")

        assertEquals(base, sameByValue, "Same values")
        assertNotSame(base, sameByValue, "Same values")

        assertNotEquals(base, different, "Different currencies")
        assertNotSame(base, different, "Different currencies")

        // Coverage cases.
        @Suppress("EqualsNullCall")
        assertFalse(base.equals(null), "Different instances types: null")
        assertFalse(base.equals(differentCurrencyCode), "Different instances types: String")
    }

    private fun expectedExceptionMessage(code: String) = "The currency code $code does not represent a standardized " +
        "currency. If you're trying to create a custom currency, create a CustomCurrency object through " +
        "Currency.custom() factory method"

    private fun List<Pair<String, Currency>>.get(code: String): Currency = find { code == it.first }?.second!!
}
