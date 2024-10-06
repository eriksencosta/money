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

import com.eriksencosta.money.allocation.Result.Details
import com.eriksencosta.money.locale
import com.eriksencosta.money.money
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class DetailsTest {
    private val details = Details(
        listOf(33.33 money "USD", 33.33 money "USD", 33.33 money "USD"),
        listOf(0.01 money "USD", 0.00 money "USD", 0.00 money "USD"),
        listOf(33.34 money "USD", 33.33 money "USD", 33.33 money "USD"),
    )

    @Test
    fun `Return the calculations totals`() {
        assertEquals(99.99 money "USD", details.calculationsTotals)
    }

    @Test
    fun `Return the adjustments totals`() {
        assertEquals(0.01 money "USD", details.adjustmentsTotals)
    }

    @Test
    fun `Return the allocations totals`() {
        assertEquals(100.00 money "USD", details.allocationsTotals)
    }

    @Test
    fun `Check for equality`() {
        val details1 = Details(
            listOf(33.33 money "USD", 33.33 money "USD", 33.33 money "USD"),
            listOf(0.01 money "USD", 0.00 money "USD", 0.00 money "USD"),
            listOf(33.34 money "USD", 33.33 money "USD", 33.33 money "USD"),
        )

        val details2 = Details(
            listOf(33.33 money "USD", 33.33 money "USD", 33.33 money "USD"),
            listOf(0.01 money "USD", 0.00 money "USD", 0.00 money "USD"),
            listOf(33.34 money "USD", 33.33 money "USD", 33.33 money "USD"),
        )

        val details3 = Details(
            listOf(33.33 money "USD", 33.33 money "USD", 33.33 money "USD"),
            listOf(0.00 money "USD", 0.00 money "USD", 0.00 money "USD"),
            listOf(33.33 money "USD", 33.33 money "USD", 33.33 money "USD"),
        )

        // This will never happen with the build-in allocators. But I need this value to cover the code branches.
        val details4 = Details(
            listOf(33.33 money "USD", 33.33 money "USD", 33.33 money "USD"),
            listOf(0.01 money "USD", 0.00 money "USD", 0.00 money "USD"),
            listOf(33.33 money "USD", 33.33 money "USD", 33.33 money "USD"),
        )

        assertEquals(details1, details1, "Same instance")
        assertEquals(details1, details2, "Same values")
        assertNotEquals(details1, details3, "Different values: adjustments and allocations")
        assertNotEquals(details1, details4, "Different values: allocations")

        @Suppress("EqualsNullCall")
        assertFalse(details1.equals(null), "Different types: null")
        assertFalse(details1.equals(""), "Different types: String")
    }

    @TestFactory
    fun `Calculate the hash code`() = listOf(
        Details(
            listOf(33.33 money "USD", 33.33 money "USD", 33.33 money "USD"),
            listOf(0.01 money "USD", 0.00 money "USD", 0.00 money "USD"),
            listOf(33.34 money "USD", 33.33 money "USD", 33.33 money "USD"),
        ) to 1_553_059_473,
        Details(
            listOf(33.33 money "USD", 33.33 money "USD", 33.33 money "USD"),
            listOf(0.00 money "USD", 0.00 money "USD", 0.01 money "USD"),
            listOf(33.33 money "USD", 33.33 money "USD", 33.34 money "USD"),
        ) to 1_552_047_633,
    ).map { (details, expected) ->
        dynamicTest("given $details then the hash code should be $expected") {
            assertEquals(expected, details.hashCode())
        }
    }

    @Suppress("TrimMultilineRawString")
    @TestFactory
    fun `Convert Details to string`() = listOf(
        Details(
            listOf(50.00 money "USD", 50.00 money "USD"),
            listOf(0.00 money "USD", 0.00 money "USD"),
            listOf(50.00 money "USD", 50.00 money "USD"),
        ) to """
            |Details[
            |calculations=[USD 50.00, USD 50.00],
            | adjustments=[USD 0.00, USD 0.00],
            | allocations=[USD 50.00, USD 50.00]
            |]
        """.stripNewLine(),

        Details(
            listOf(33.33 money "USD", 33.33 money "USD", 33.33 money "USD"),
            listOf(0.01 money "USD", 0.00 money "USD", 0.00 money "USD"),
            listOf(33.34 money "USD", 33.33 money "USD", 33.33 money "USD"),
        ) to """
            |Details[
            |calculations=[USD 33.33, USD 33.33, USD 33.33],
            | adjustments=[USD 0.01, USD 0.00, USD 0.00],
            | allocations=[USD 33.34, USD 33.33, USD 33.33]
            |]
        """.stripNewLine()
    ).map { (details, expected) ->
        dynamicTest("given $details when I convert it to a string then I should get $expected") {
            assertEquals(expected, locale { details.toString() })
        }
    }

    private fun String.stripNewLine(): String = this.trimMargin().replace("\n", "")
}
