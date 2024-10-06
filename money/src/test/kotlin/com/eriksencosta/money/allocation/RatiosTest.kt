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
import com.eriksencosta.money.locale
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class RatiosTest {
    @Test
    fun `Throw exception when the ratios do not sum 100 percent`() = assertThrows<IllegalArgumentException> {
        Ratios(70.percent(), 29.995.percent())
    }.run {
        val expected = "The sum of the ratios must be 100%. The provided ratios sum up to 99.995%. Consider " +
            "normalizing the list using the function com.eriksencosta.money.allocation.adjustForAllocate()"

        assertEquals(expected, message)
    }

    @Test
    fun `Throw exception when a negative Percentage is found`() = assertThrows<IllegalArgumentException> {
        Ratios(-(5.5).percent(), 70.percent(), 50.percent(), -(10).percent(), -(4.5).percent())
    }.run {
        assertEquals(
            "The ratios list must contain only positive percentages. Invalid values found: -5.5%, -10%, -4.5%",
            message
        )
    }

    @Test
    fun `Check for equality`() {
        val ratios1 = Ratios(50.percent(), 50.percent())
        val ratios2 = Ratios(50.percent(), 50.percent())
        val ratios3 = Ratios(25.percent(), 25.percent(), 25.percent(), 25.percent())

        assertEquals(ratios1, ratios1, "Same instance")
        assertEquals(ratios1, ratios2, "Same values")
        assertNotEquals(ratios1, ratios3, "Different values")

        @Suppress("EqualsNullCall")
        assertFalse(ratios1.equals(null), "Different instances types: null")
        assertFalse(ratios1.equals(1), "Different instances types: Number")
    }

    @TestFactory
    fun `Calculate the hash code`() = listOf(
        Ratios(100.percent()) to 1_041_237_896,
        Ratios(50.percent(), 50.percent()) to -2_080_313_119,
        Ratios(25.percent(), 25.percent(), 25.percent(), 25.percent()) to 260_647_361,
    ).map { (ratios, expected) ->
        dynamicTest("given $ratios then the hash code should be $expected") {
            assertEquals(expected, ratios.hashCode())
        }
    }

    @TestFactory
    fun `Convert Ratios to string`() = listOf(
        Ratios(100.percent()) to "[100%]",
        Ratios(50.percent(), 50.percent()) to "[50%, 50%]",
        Ratios(25.percent(), 25.percent(), 25.percent(), 25.percent()) to "[25%, 25%, 25%, 25%]",
    ).map { (parts, expected) ->
        dynamicTest("given $parts when I convert it to a string then I should get $expected") {
            assertEquals(expected, locale { parts.toString() })
        }
    }
}
