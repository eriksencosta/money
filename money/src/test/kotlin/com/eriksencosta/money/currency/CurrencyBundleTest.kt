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

import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

internal abstract class CurrencyBundleTest {
    abstract val invalidCurrenciesCodes: Set<String>
    abstract val invalidCurrenciesSecondaryCodes: Set<String>
    abstract val invalidSuffix: String

    abstract fun factory(): CurrencyBundle

    @TestFactory
    fun `Create a CurrencyData`() = factory().codes().map { code ->
        dynamicTest("given $code then I should get a CurrencyData") {
            val currency = factory().ofCode(code)

            assertIs<CurrencyData>(currency)
            assertEquals(code, currency.code)
            assertNotNull(currency.secondaryCode)
            assertNotNull(currency.name)
            assertNotNull(currency.symbol)
            assertNotNull(currency.type)
            assertNotNull(currency.minorUnits)
        }
    }

    @TestFactory
    fun `Create an UndefinedCurrencyData when code is invalid`() = invalidCurrenciesCodes().map {
        val code = when (invalidCurrenciesCodes.contains(it)) {
            true -> it
            false -> "$it$invalidSuffix"
        }

        dynamicTest("given $code then I should get an UndefinedCirculatingCurrencyData") {
            val currency = factory().ofCode(code)

            assertIs<UndefinedCurrencyData>(currency)
            assertEquals(code, currency.code)
            assertEquals("", currency.secondaryCode)
            assertEquals("", currency.name)
            assertEquals("", currency.symbol)
            assertEquals("", currency.type)
            assertEquals(0, currency.minorUnits)
        }
    }

    @TestFactory
    fun `Create a CurrencyData using a secondary codes`() = factory().secondaryCodes().map { code ->
        dynamicTest("given $code then I should get a CurrencyData") {
            val currency = factory().ofSecondaryCode(code)

            assertIs<CurrencyData>(currency)
            assertNotNull(currency.code)
            assertEquals(code, currency.secondaryCode)
            assertNotNull(currency.name)
            assertNotNull(currency.symbol)
            assertNotNull(currency.type)
            assertNotNull(currency.minorUnits)
        }
    }

    @TestFactory
    fun `Create a UndefinedCurrencyData when secondary code is invalid`() = invalidCurrenciesSecondaryCodes().map {
        val code = when (invalidCurrenciesSecondaryCodes.contains(it)) {
            true -> it
            false -> "$it$invalidSuffix"
        }

        dynamicTest("given $code then I should get an UndefinedCurrencyData") {
            val currency = factory().ofSecondaryCode(code)

            assertIs<UndefinedCurrencyData>(currency)
            assertEquals("", currency.code)
            assertEquals("", currency.secondaryCode)
            assertEquals("", currency.name)
            assertEquals("", currency.symbol)
            assertEquals("", currency.type)
            assertEquals(0, currency.minorUnits)
        }
    }

    private fun invalidCurrenciesCodes() = invalidCurrenciesCodes + factory().codes()
    private fun invalidCurrenciesSecondaryCodes() = invalidCurrenciesSecondaryCodes + factory().secondaryCodes()
}
