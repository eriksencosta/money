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

import java.math.BigDecimal
import java.math.RoundingMode.DOWN
import java.math.RoundingMode.UP
import kotlin.test.Test
import kotlin.test.assertEquals

class NumberTest {
    private val eth = Currency.of("ETH")
    private val jpy = Currency.of("JPY")
    private val usd = Currency.of("USD")

    @Test
    fun `Create a Money`() {
        assertEquals(Money.of(-1.00, "USD"), -1 money "USD")
        assertEquals(Money.of(0.00, "USD", UP), 0.money("USD", UP))
        assertEquals(Money.of(1.00, Currency.of("USD").toCustomCurrency(4)), 1.money("USD", 4))
        assertEquals(Money.of(2.00, Currency.of("USD").toCustomCurrency(4), UP), 2.money("USD", UP, 4))
        assertEquals(Money.of(1000, "JPY"), 1000 money jpy)
        assertEquals(Money.of(2000, "JPY", UP), 2000.money(jpy, UP))
        assertEquals(
            Money.of(0.3333333333333333, "ETH"),
            (1.0 / 3) money "ETH"
        )
        assertEquals(
            Money.of(BigDecimal("0.123456789123456789"), eth, UP),
            BigDecimal("0.123456789123456789").money(eth, UP)
        )
    }

    @Test
    fun `Transform a collection of Number to Money`() {
        val numbers = 1..3
        val expected = listOf(Money.of(1.00, "USD"), Money.of(2.00, "USD"), Money.of(3.00, "USD"))

        assertEquals(expected, numbers money "USD")
        assertEquals(expected, numbers money usd)
    }

    @Test
    fun `Transform a collection of Number to Money with RoundingMode`() {
        val numbers = 1..3
        val expected = listOf(
            Money.of(1.00, "USD", DOWN),
            Money.of(2.00, "USD", DOWN),
            Money.of(3.00, "USD", DOWN),
        )

        assertEquals(expected, numbers.money("USD", DOWN))
        assertEquals(expected, numbers.money(usd, DOWN))
    }
}
