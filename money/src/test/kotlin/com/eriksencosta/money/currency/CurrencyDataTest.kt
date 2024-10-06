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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

internal class CurrencyDataTest {
    private val definedSuccessExpectation: (CurrencyData) -> Unit = {
        assertEquals("DEFINED", it.code)
    }

    private val definedFailureExpectation: (CurrencyData) -> Unit = {
        fail("The defined(CurrencyData) block should not be called")
    }

    private val undefinedSuccessExpectation: (String) -> Unit = {
        assertEquals("UNDEFINED", it)
    }

    private val undefinedFailureExpectation: (String) -> Unit = {
        fail("The undefined(String) block should not be called")
    }

    @Test
    fun `Transform CurrencyData for defined currency`() {
        val currencyData = CurrencyData("DEFINED", "", "", "", "", 0)

        currencyData.transform(definedSuccessExpectation, undefinedFailureExpectation)
    }

    @Test
    fun `Transform CurrencyData for undefined currency`() {
        val currencyData = UndefinedCurrencyData("UNDEFINED")

        currencyData.transform(definedFailureExpectation, undefinedSuccessExpectation)
    }
}
