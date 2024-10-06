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

package com.eriksencosta.money.allocation

import com.eriksencosta.math.percentage.percent
import com.eriksencosta.money.money
import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyTest {
    private val money = 100.00 money "USD"

    @Test
    fun `Should allocate using EvenAllocator`() {
        val parts = EvenParts(2)
        val expected = List(2) { 50 money "USD" }

        assertEquals(expected, money.allocate(parts).allocations())
        assertEquals(expected, money.allocate(parts.parts).allocations())
    }

    @Test
    fun `Should allocate using ProportionalAllocator`() {
        val ratios = Ratios(List(4) { 25.percent() })
        val expected = List(4) { 25.00 money "USD" }

        assertEquals(expected, money.allocate(ratios).allocations())
        assertEquals(expected, money.allocate(ratios.ratios).allocations())
        assertEquals(expected, money.allocate(*ratios.ratios.toTypedArray()).allocations())
    }
}
