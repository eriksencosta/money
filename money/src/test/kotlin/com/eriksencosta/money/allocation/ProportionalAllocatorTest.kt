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

import com.eriksencosta.math.common.Rounding
import com.eriksencosta.math.percentage.percent
import com.eriksencosta.money.Money
import com.eriksencosta.money.allocation.AllocationExpectations.assertions
import com.eriksencosta.money.allocation.ProportionalAllocatorFixtures.cases
import com.eriksencosta.money.money
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class ProportionalAllocatorTest {
    private val allocator = ProportionalAllocator.default()

    @TestFactory
    fun `Allocate Money according to ratios`() = assertions(cases, allocator).also {
        // Overloaded methods
        val money = 100 money "USD"
        val ratios = listOf(75.percent(), 25.percent())
        val expected = listOf(75 money "USD", 25 money "USD")

        assertEquals(expected, allocator.allocate(money, ratios).allocations())
        assertEquals(expected, allocator.allocate(money, *ratios.toTypedArray()).allocations())
    }

    @Test
    fun `Throw exception when Money has no rounding support`() = assertThrows<IllegalArgumentException> {
        val money = Money.of(1, "USD", Rounding.no())
        allocator.allocate(money, listOf(100.percent()))
    }.run {
        assertEquals("Money must support rounding for precise allocation calculation", message)
    }
}
