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

import com.eriksencosta.money.allocation.AllocationFixtures.cases
import com.eriksencosta.money.allocation.Result.Details
import com.eriksencosta.money.allocation.difference.Discard
import com.eriksencosta.money.allocation.difference.DistributeBySign
import com.eriksencosta.money.locale
import com.eriksencosta.money.money
import com.eriksencosta.money.sum
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class ResultTest {
    private val allocator = DistributeBySign.positiveOnFirstSmallest()

    @TestFactory
    fun `Return the allocations`() = cases.map { case ->
        dynamicTest("given a result for ${case.expected.result} query for the allocations") {
            val expected = case.expected.result
            val allocations = Result(case.money, case.reverse, case.allocations, allocator).allocations()

            assertEquals(expected, allocations)
            assertEquals(case.money, allocations.sum(), "Allocations sum check")
        }
    }

    @TestFactory
    fun `Return the allocation result details`() = cases.map { case ->
        dynamicTest("given a result for ${case.expected.result} query for its details") {
            val expected = Details(
                case.expected.calculated,
                case.expected.adjustments,
                case.expected.result
            )

            val details = Result(case.money, case.reverse, case.allocations, allocator).details()

            assertEquals(expected, details)
            assertEquals(case.reverse, details.calculationsTotals, "Calculations check")
            assertEquals(case.adjustment, details.adjustmentsTotals, "Adjustments check")
            assertEquals(case.money, details.allocationsTotals, "Allocations check")
        }
    }

    @Test
    fun `Check for equality`() {
        val result1 = Result(
            1.00 money "USD",
            1.00 money "USD",
            listOf(1.00 money "USD"),
            allocator
        )

        val result2 = Result(
            1.00 money "USD",
            1.00 money "USD",
            listOf(1.00 money "USD"),
            allocator
        )

        val result3 = Result(
            0.50 money "USD",
            0.50 money "USD",
            listOf(0.50 money "USD"),
            allocator
        )

        val result4 = Result(
            1.00 money "USD",
            0.50 money "USD",
            listOf(0.50 money "USD", 0.00 money "USD"), // This is an impossible case, only for testing.
            allocator
        )

        val result5 = Result(
            1.00 money "USD",
            1.00 money "USD",
            listOf(0.50 money "USD", 0.50 money "USD"),
            allocator
        )

        val result6 = Result(
            1.00 money "USD",
            1.00 money "USD",
            listOf(0.50 money "USD", 0.50 money "USD"),
            DistributeBySign.positiveOnFirstGreatest()
        )

        assertEquals(result1, result1, "Same instance")
        assertEquals(result1, result2, "Same values")
        assertEquals(result2, result1, "Same values")
        assertNotEquals(result1, result3, "Different monetary value")
        assertNotEquals(result1, result4, "Different reverse value")
        assertNotEquals(result1, result5, "Different allocations")
        assertEquals(result5, result6, "Same values")

        @Suppress("EqualsNullCall")
        assertFalse(result1.equals(null), "Different instances types: null")
        assertFalse(result1.equals("resultString"), "Different instances types: String")
    }

    @TestFactory
    fun `Calculate the hash code`() = listOf(
        Result(
            1.00 money "USD",
            1.00 money "USD",
            listOf(1.00 money "USD"),
            allocator
        ) to 896_091_638,

        Result(
            1.00 money "USD",
            1.00 money "USD",
            listOf(1.00 money "USD"),
            DistributeBySign.positiveOnFirstGreatest()
        ) to 896_091_638,

        Result(
            1.00 money "USD",
            1.00 money "USD",
            listOf(0.50 money "USD", 0.50 money "USD"),
            allocator
        ) to -202_983_553
    )
        .map { (result, expected) ->
            dynamicTest("given $result then the hash code should be $expected") {
                assertEquals(expected, result.hashCode())
            }
        }

    @Test
    fun `Convert Result to string`() {
        val result = Result(
            1.00 money "USD",
            1.00 money "USD",
            listOf(0.50 money "USD", 0.50 money "USD"),
            Discard
        )

        assertEquals("[USD 0.50, USD 0.50]", locale { result.toString() })
    }
}
