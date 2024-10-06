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

package com.eriksencosta.money.currency

import com.eriksencosta.money.CirculatingCurrency
import com.eriksencosta.money.CirculatingCurrency.Type.TENDER
import com.eriksencosta.money.CryptoCurrency
import com.eriksencosta.money.CryptoCurrency.Type.AUXILIARY
import com.eriksencosta.money.CryptoCurrency.Type.NATIVE
import com.eriksencosta.money.Currency
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class CurrencyResolutionTest {
    private val circulatingCurrencies = listOf(
        CirculatingCurrency("BRL", "986", "Brazilian Real", "R$", TENDER, 2),
        CirculatingCurrency("USD", "840", "US Dollar", "$", TENDER, 2),
    )

    private val cryptocurrencies = listOf(
        CryptoCurrency("9X1Z1NQG5", "bb-a-USDT", "Balancer Aave Boosted Pool (USDT)", "bb-a-USDT", AUXILIARY, 18),
        CryptoCurrency("4H95J0R2X", "BTC", "Bitcoin", "₿", NATIVE, 8),
        CryptoCurrency("3WCGMWKQM", "CH1319968462", "Citta di Lugano/1.415 Bd 20340307", "CH1319968462", AUXILIARY, 0),
        CryptoCurrency("WGJSRNZGM", "GRT.e", "Graph Token", "GRT.e", AUXILIARY, 18),
        CryptoCurrency("J7KQCG93W", "1INCH.e", "1INCH Token", "1INCH.e", AUXILIARY, 18),
        CryptoCurrency("44SDQ6NX0", "\$HIB", "SHIBA INU", "\$HIB", AUXILIARY, 9),
    )

    private val undefinedCodes = setOf("", "WWW", "999999999", "UNDEFINED_CURRENCY", "UNDEFINED_CURRENCY_CODE")

    private val circulating = CurrencyResolution.circulating
    private val crypto = CurrencyResolution.crypto
    private val thorough = CurrencyResolution.thorough

    @TestFactory
    fun `Resolve circulating currency data by primary code`() = circulatingCurrencies.indexedByCode()
        .map { (code, expected) ->
            dynamicTest("given $code when I resolve it then I should get a CirculatingCurrency") {
                assertEquals(expected, circulating.resolve(code))
                assertEquals(expected, thorough.resolve(code))
            }
        }

    @TestFactory
    fun `Resolve circulating currency data by secondary code`() = circulatingCurrencies.indexedBySecondaryCode()
        .map { (secondaryCode, expected) ->
            dynamicTest("given $secondaryCode when I resolve it then I should get a CirculatingCurrency") {
                assertEquals(expected, circulating.resolve(secondaryCode))
                assertEquals(expected, thorough.resolve(secondaryCode))
            }
        }

    @TestFactory
    fun `Resolve cryptocurrency data by primary code`() = cryptocurrencies.indexedByCode()
        .map { (code, expected) ->
            dynamicTest("given $code when I resolve it then I should get a CryptoCurrency") {
                assertEquals(expected, crypto.resolve(code))
                assertEquals(expected, thorough.resolve(code))
            }
        }

    @TestFactory
    fun `Resolve cryptocurrency data by secondary code`() = cryptocurrencies.indexedBySecondaryCode()
        .map { (secondaryCode, expected) ->
            dynamicTest("given $secondaryCode when I resolve it then I should get a CryptoCurrency") {
                assertEquals(expected, crypto.resolve(secondaryCode))
                assertEquals(expected, thorough.resolve(secondaryCode))
            }
        }

    @TestFactory
    fun `Throw exception when no currency is found when resolving thoroughly`() = undefinedCodes
        .map { code ->
            dynamicTest("given $code when I resolve the code then I should get an exception") {
                assertThrows<IllegalArgumentException> { thorough.resolve(code) }.run {
                    assertEquals(expectedExceptionMessage(code), message)
                }
            }
        }

    @TestFactory
    fun `Throw exception when no currency is found when resolving using circulating chain`() = undefinedCodes
        .map { code ->
            dynamicTest("given $code when I resolve the code then I should get an exception") {
                assertThrows<IllegalArgumentException> { circulating.resolve(code) }.run {
                    assertEquals(expectedExceptionMessage(code), message)
                }
            }
        }

    @TestFactory
    fun `Throw exception when no currency is found when resolving using crypto chain`() = undefinedCodes
        .map { code ->
            dynamicTest("given $code when I resolve the code then I should get an exception") {
                assertThrows<IllegalArgumentException> { crypto.resolve(code) }.run {
                    assertEquals(expectedExceptionMessage(code), message)
                }
            }
        }

    @TestFactory
    fun `Identify circulating currency code in a string`() = listOf(
        "USD 1.23" to "USD",
        "USD 1,23" to "USD",
        "USD 1.234" to "USD",
        "USD 1.234,56" to "USD",
        "USD -1.23" to "USD",

        "1.23 BRL" to "BRL",
        "1,23 BRL" to "BRL",
        "1.234 BRL" to "BRL",
        "1.234,56 BRL" to "BRL",
        "-1.23 BRL" to "BRL",
    )
        .map { (string, expected) ->
            dynamicTest("given \"$string\" when identified then I should get $expected") {
                assertEquals(expected, circulating.identify(string))
                assertEquals(expected, thorough.identify(string))
            }
        }

    @TestFactory
    fun `Identify cryptocurrency code in a string`() = listOf(
        "4H95J0R2X 1.2345" to "4H95J0R2X",
        "4H95J0R2X 1,2345" to "4H95J0R2X",
        "4H95J0R2X 1.23456789" to "4H95J0R2X",
        "4H95J0R2X 1,23456789" to "4H95J0R2X",
        "4H95J0R2X -1.2345" to "4H95J0R2X",

        "0.123456789 X9J9K872S" to "X9J9K872S",
        "0,123456789 X9J9K872S" to "X9J9K872S",
        "1.123456789123456789 X9J9K872S" to "X9J9K872S",
        "1,123456789123456789 X9J9K872S" to "X9J9K872S",
        "-0.123456789 X9J9K872S" to "X9J9K872S",
    )
        .map { (string, expected) ->
            dynamicTest("given \"$string\" when identified then I should get $expected") {
                assertEquals(expected, crypto.identify(string))
                assertEquals(expected, thorough.identify(string))
            }
        }

    @TestFactory
    fun `Identify cryptocurrency secondary code in a string`() = listOf(
        "BTC 1.2345" to "BTC",
        "ETH 1,2345" to "ETH",
        "1INCH.e 1.23456789" to "1INCH.e",
        "\$HIB 1,23456789" to "\$HIB",
        "bb-a-USDT -1.2345" to "bb-a-USDT",
        "token-wstx -12.345" to "token-wstx",
        "t -12.345" to "t",

        "1.2345 BTC" to "BTC",
        "1,2345 ETH" to "ETH",
        "1.23456789 1INCH.e" to "1INCH.e",
        "1,23456789 \$HIB" to "\$HIB",
        "-1.2345 bb-a-USDT" to "bb-a-USDT",
        "-12.345 token-wstx" to "token-wstx",
        "-12.345 t" to "t",

        // The best candidates for currency code have at least one uppercase letter. Then, at least one letter
        "USD zero" to "USD",
        "USD none" to "USD",
        "zero USD" to "USD",
        "none USD" to "USD",
        "4H95J0R2X zero" to "4H95J0R2X",
        "4H95J0R2X none" to "4H95J0R2X",
        "zero 4H95J0R2X" to "4H95J0R2X",
        "none 4H95J0R2X" to "4H95J0R2X",
        "BTC zero" to "BTC",
        "BTC none" to "BTC",
        "zero BTC" to "BTC",
        "none BTC" to "BTC",
        "1INCH.e zero" to "1INCH.e",
        "1INCH.e none" to "1INCH.e",
        "zero 1INCH.e" to "1INCH.e",
        "none 1INCH.e" to "1INCH.e",
        "bb-a-USDT zero" to "bb-a-USDT",
        "bb-a-USDT none" to "bb-a-USDT",
        "zero bb-a-USDT" to "bb-a-USDT",
        "none bb-a-USDT" to "bb-a-USDT",

        // More complex strings
        "USD 1.00 per unit" to "USD",
        "USD 1.00 per gallon" to "USD",
        "USD 1.00/l" to "USD",
        "bb-a-USDT 1.00 per unit" to "bb-a-USDT",
        "bb-a-USDT 1.00 per gallon" to "bb-a-USDT",
        "bb-a-USDT 1.00/l" to "bb-a-USDT",
        "bb-a-USDT token-token" to "bb-a-USDT",
    )
        .map { (string, expected) ->
            dynamicTest("given \"$string\" when identified then I should get $expected") {
                assertEquals(expected, crypto.identify(string))
                assertEquals(expected, thorough.identify(string))
            }
        }

    @TestFactory
    fun `Return empty string when unable to identify a currency code`() = listOf(
        // Singular values are discarded
        "USD",
        "4H95J0R2X",
        "BTC",
        "USD1.00",
        "1.00USD",
        "USD1,000",
        "1,000USD",
        "$ 1",
        "1 $",
    ).map { string ->
        dynamicTest("given \"$string\" when identified then I should get an empty string") {
            assertEquals("", circulating.identify(string))
            assertEquals("", crypto.identify(string))
            assertEquals("", thorough.identify(string))
        }
    }

    private fun List<Currency>.indexedByCode() = map { it.code to it }
    private fun List<Currency>.indexedBySecondaryCode() = map { it.secondaryCode to it }

    private fun expectedExceptionMessage(code: String) = "The currency code $code does not represent a standardized " +
        "currency. If you're trying to create a custom currency, create a CustomCurrency object through " +
        "Currency.custom() factory method"
}
