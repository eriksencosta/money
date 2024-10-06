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
import com.eriksencosta.math.percentage.sum
import com.eriksencosta.money.locale
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PercentageTest {
    @TestFactory
    fun `Adjust list of percentages that sums up one hundred percent`() = listOf(
        listOf(
            33.33.percent(),
            33.33.percent(),
            33.34.percent(),
        ),
        listOf(
            50.percent(),
            50.percent(),
        ),
        listOf(
            100.percent(),
        ),
    ).map { ratios ->
        val sumOfRatios = ratios.sum()

        dynamicTest("given $ratios ($sumOfRatios) when normalized then I should get a list that sums up to 100%") {
            val normalized = ratios.adjustForAllocate()

            assertTrue(normalized.sum().isOneHundred)
            assertTrue(sumOfRatios.isOneHundred, "Ratios list check")
        }
    }

    @TestFactory
    fun `Adjust list of percentages to sum up one hundred percent`() = listOf(
        listOf(
            99.99999999999997.percent()
        ),
        listOf(
            99.99999999999996.percent()
        ),
        listOf(
            99.99999999999994.percent()
        ),
        listOf(
            99.99999999999993.percent()
        ),
        listOf(
            99.99999999999991.percent()
        ),
        listOf(
            { 0.09650955110328836 * 100 }.percent(),
            { 0.06458416518196212 * 100 }.percent(),
            { 0.10702615778578536 * 100 }.percent(),
            { 0.1223412290473479 * 100 }.percent(),
            { 0.023484089702908474 * 100 }.percent(),
            { 0.07667660652228124 * 100 }.percent(),
            { 0.1612188595080266 * 100 }.percent(),
            { 0.10487335899022134 * 100 }.percent(),
            { 0.07470606391734863 * 100 }.percent(),
            { 0.16857991824082977 * 100 }.percent(),
        ),
    ).map { ratios ->
        val sumOfRatios = ratios.sum()

        dynamicTest("given $ratios ($sumOfRatios) when normalized then I should get a list that sums up to 100%") {
            val normalized = ratios.adjustForAllocate()

            assertTrue(normalized.sum().isOneHundred)
            assertTrue(sumOfRatios.isNotOneHundred, "Ratios list check")
        }
    }

    @TestFactory
    fun `Throw exception when the difference to one hundred percent is not within the tolerance`() = listOf(
        listOf(99.99999999999990.percent()) to "99.9999999999999%",
        listOf(99.99999999999980.percent(), 0.00000000000010.percent()) to "99.9999999999999%",
        listOf(0.00000000000010.percent(), 99.99999999999980.percent()) to "99.9999999999999%",
    ).map { (ratios, formattedPercentage) ->
        val expected = "Can not adjust the ratios. The difference between $formattedPercentage and 100% is not " +
            "within the tolerance (9.0E-14)"

        dynamicTest("given $ratios when normalized then I should get an IllegalArgumentException") {
            assertThrows<IllegalArgumentException> { locale { ratios.adjustForAllocate() } }.run {
                assertEquals(expected, message)
            }
        }
    }
}
