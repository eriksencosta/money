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
import com.eriksencosta.money.Money
import com.eriksencosta.money.allocation.AllocationExpectations.assertions
import com.eriksencosta.money.allocation.EvenAllocatorFixtures.cases
import com.eriksencosta.money.money
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

class EvenAllocatorTest {
    private val allocator = EvenAllocator.default()

    @TestFactory
    fun `Allocate Money evenly`() = assertions(cases, allocator).also {
        // Overloaded method
        val result = allocator.allocate(100.00 money "USD", 2)
        val expected = listOf(50.00 money "USD", 50.00 money "USD")

        assertEquals(expected, result.allocations())
    }

    @Test
    fun `Throw exception when Money has no rounding support`() = assertThrows<IllegalArgumentException> {
        val money = Money.of(1.00, "USD", Rounding.no())
        allocator.allocate(money, 1)
    }.run {
        assertEquals("Money must support rounding for precise allocation calculation", message)
    }
}
