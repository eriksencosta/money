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

import com.eriksencosta.math.percentage.Percentage
import com.eriksencosta.math.percentage.percent
import com.eriksencosta.money.Currency
import com.eriksencosta.money.allocation.AllocationExpectations.Case
import com.eriksencosta.money.allocation.AllocationExpectations.SameAllocations
import com.eriksencosta.money.allocation.AllocationExpectations.VariableAllocations
import com.eriksencosta.money.allocation.AllocationExpectations.VariableAllocations.RoundingExpectations
import com.eriksencosta.money.allocation.Fixtures.exchangeJpy
import com.eriksencosta.money.allocation.Fixtures.exchangeUsd
import com.eriksencosta.money.allocation.Fixtures.jpy
import com.eriksencosta.money.allocation.Fixtures.usd

internal object ProportionalAllocatorFixtures {
    private const val ONE_HUNDRED = 100

    internal abstract class ProportionalAllocatorCase(
        override val amount: Number,
        override val currency: Currency,
        open val ratios: List<Percentage>
    ) : Case(amount, currency) {
        override val allocateBy: AllocationBy get() = Ratios(ratios)
    }

    internal data class SameAllocationsCase(
        override val amount: Number,
        override val currency: Currency,
        override val ratios: List<Percentage>,
        override val expected: List<Number>
    ) : ProportionalAllocatorCase(amount, currency, ratios), SameAllocations

    internal data class VariableAllocationsCase(
        override val amount: Number,
        override val currency: Currency,
        override val ratios: List<Percentage>,
        override val expectations: RoundingExpectations
    ) : ProportionalAllocatorCase(amount, currency, ratios), VariableAllocations

    private val sameAllocationsCases = listOf(
        /* Standardized currencies cases */

        // USD 0.00
        SameAllocationsCase(0.00, usd, ratiosFor(1), List(1) { 0.00 }),
        SameAllocationsCase(0.00, usd, ratiosFor(2), List(2) { 0.00 }),
        SameAllocationsCase(0.00, usd, ratiosFor(4), List(4) { 0.00 }),

        // USD 1.00
        SameAllocationsCase(1.00, usd, ratiosFor(1), List(1) { 1.00 }),
        SameAllocationsCase(1.00, usd, ratiosFor(2), List(2) { 0.50 }),
        SameAllocationsCase(1.00, usd, ratiosFor(4), List(4) { 0.25 }),

        // USD 0.10
        SameAllocationsCase(0.10, usd, ratiosFor(1), List(1) { 0.10 }),
        SameAllocationsCase(0.10, usd, ratiosFor(2), List(2) { 0.05 }),

        // USD 0.01
        SameAllocationsCase(0.01, usd, ratiosFor(1), List(1) { 0.01 }),

        // JPY 1000
        SameAllocationsCase(1000, jpy, ratiosFor(1), List(1) { 1000 }),
        SameAllocationsCase(1000, jpy, ratiosFor(2), List(2) { 500 }),
        SameAllocationsCase(1000, jpy, ratiosFor(4), List(4) { 250 }),

        // JPY 100
        SameAllocationsCase(100, jpy, ratiosFor(1), List(1) { 100 }),
        SameAllocationsCase(100, jpy, ratiosFor(2), List(2) { 50 }),
        SameAllocationsCase(100, jpy, ratiosFor(4), List(4) { 25 }),

        // JPY 10
        SameAllocationsCase(10, jpy, ratiosFor(1), List(1) { 10 }),
        SameAllocationsCase(10, jpy, ratiosFor(2), List(2) { 5 }),

        /* Exchange currencies cases */

        // USD[4] 0.0010
        SameAllocationsCase(0.0010, exchangeUsd, ratiosFor(2), List(2) { 0.0005 }),

        // JPY[4] 0.0010
        SameAllocationsCase(0.0010, exchangeJpy, ratiosFor(2), List(2) { 0.0005 }),

        /* Negative cases */

        // USD 1.00
        SameAllocationsCase(-1.00, usd, ratiosFor(1), List(1) { -1.00 }),
        SameAllocationsCase(-1.00, usd, ratiosFor(2), List(2) { -0.50 }),
        SameAllocationsCase(-1.00, usd, ratiosFor(4), List(4) { -0.25 }),

        // USD 0.10
        SameAllocationsCase(-0.10, usd, ratiosFor(1), List(1) { -0.10 }),
        SameAllocationsCase(-0.10, usd, ratiosFor(2), List(2) { -0.05 }),

        // USD 0.01
        SameAllocationsCase(-0.01, usd, ratiosFor(1), List(1) { -0.01 }),
    )

    private val variableAllocationsCases = listOf(
        /* Standardized currencies cases */

        // USD 0.0001
        VariableAllocationsCase(
            0.0001,
            usd,
            ratiosFor(2),
            RoundingExpectations(
                listOf(0.00, 0.00),
                listOf(0.01, 0.00),
                listOf(0.01, 0.00),
                listOf(0.00, 0.00),
                listOf(0.00, 0.00),
                listOf(0.00, 0.00),
                listOf(0.00, 0.00),
            )
        ),
        VariableAllocationsCase(
            0.0001,
            usd,
            ratiosFor(3).adjustForAllocate(),
            RoundingExpectations(
                listOf(0.00, 0.00, 0.00),
                listOf(0.01, 0.00, 0.00),
                listOf(0.01, 0.00, 0.00),
                listOf(0.00, 0.00, 0.00),
                listOf(0.00, 0.00, 0.00),
                listOf(0.00, 0.00, 0.00),
                listOf(0.00, 0.00, 0.00),
            )
        ),

        // USD 0.001
        VariableAllocationsCase(
            0.001,
            usd,
            ratiosFor(2),
            RoundingExpectations(
                listOf(0.00, 0.00),
                listOf(0.01, 0.00),
                listOf(0.01, 0.00),
                listOf(0.00, 0.00),
                listOf(0.00, 0.00),
                listOf(0.00, 0.00),
                listOf(0.00, 0.00),
            )
        ),
        VariableAllocationsCase(
            0.001,
            usd,
            ratiosFor(3).adjustForAllocate(),
            RoundingExpectations(
                listOf(0.00, 0.00, 0.00),
                listOf(0.01, 0.00, 0.00),
                listOf(0.01, 0.00, 0.00),
                listOf(0.00, 0.00, 0.00),
                listOf(0.00, 0.00, 0.00),
                listOf(0.00, 0.00, 0.00),
                listOf(0.00, 0.00, 0.00),
            )
        ),

        // USD 0.01
        VariableAllocationsCase(
            0.01,
            usd,
            ratiosFor(2),
            RoundingExpectations(
                listOf(0.01, 0.00),
                listOf(0.01, 0.00),
                listOf(0.01, 0.00),
                listOf(0.01, 0.00),
                listOf(0.01, 0.00),
                listOf(0.01, 0.00),
                listOf(0.01, 0.00),
            )
        ),
        VariableAllocationsCase(
            0.01,
            usd,
            ratiosFor(3).adjustForAllocate(),
            RoundingExpectations(
                listOf(0.01, 0.00, 0.00),
                listOf(0.01, 0.00, 0.00),
                listOf(0.01, 0.00, 0.00),
                listOf(0.01, 0.00, 0.00),
                listOf(0.01, 0.00, 0.00),
                listOf(0.01, 0.00, 0.00),
                listOf(0.01, 0.00, 0.00),
            )
        ),

        // USD 0.02
        VariableAllocationsCase(
            0.02,
            usd,
            ratiosFor(3).adjustForAllocate(),
            RoundingExpectations(
                listOf(0.01, 0.01, 0.00),
                listOf(0.01, 0.01, 0.00),
                listOf(0.01, 0.01, 0.00),
                listOf(0.01, 0.01, 0.00),
                listOf(0.01, 0.01, 0.00),
                listOf(0.02, 0.00, 0.00),
                listOf(0.02, 0.00, 0.00),
            )
        ),
        VariableAllocationsCase(
            0.02,
            usd,
            ratiosFor(4),
            RoundingExpectations(
                listOf(0.02, 0.00, 0.00, 0.00),
                listOf(0.01, 0.01, 0.00, 0.00),
                listOf(0.01, 0.01, 0.00, 0.00),
                listOf(0.01, 0.01, 0.00, 0.00),
                listOf(0.02, 0.00, 0.00, 0.00),
                listOf(0.02, 0.00, 0.00, 0.00),
                listOf(0.02, 0.00, 0.00, 0.00),
            )
        ),

        // USD 0.04
        VariableAllocationsCase(
            0.04,
            usd,
            ratiosFor(5),
            RoundingExpectations(
                List(4) { 0.01 } + 0.00,
                List(4) { 0.01 } + 0.00,
                List(4) { 0.01 } + 0.00,
                List(4) { 0.01 } + 0.00,
                List(4) { 0.01 } + 0.00,
                listOf(0.04) + List(4) { 0.00 },
                listOf(0.04) + List(4) { 0.00 },
            )
        ),

        // USD 0.12
        VariableAllocationsCase(
            0.12,
            usd,
            listOf(62.5.percent(), 31.25.percent(), 6.25.percent()),
            RoundingExpectations(
                listOf(0.08, 0.04, 0.00),
                listOf(0.08, 0.04, 0.00),
                listOf(0.08, 0.04, 0.00),
                listOf(0.08, 0.04, 0.00),
                listOf(0.07, 0.04, 0.01),
                listOf(0.09, 0.03, 0.00),
                listOf(0.09, 0.03, 0.00),
            )
        ),

        // USD 2.50
        VariableAllocationsCase(
            2.50,
            usd,
            listOf(65.percent(), 35.percent()),
            RoundingExpectations(
                listOf(1.62, 0.88),
                listOf(1.63, 0.87),
                listOf(1.63, 0.87),
                listOf(1.63, 0.87),
                listOf(1.63, 0.87),
                listOf(1.63, 0.87),
                listOf(1.63, 0.87),
            )
        ),

        // USD 6.13
        VariableAllocationsCase(
            6.13,
            usd,
            listOf(
                58.52.percent(),
                41.20.percent(),
                0.25.percent(),
                0.03.percent(),
            ),
            RoundingExpectations(
                listOf(3.59, 2.53, 0.01, 0.00),
                listOf(3.59, 2.53, 0.01, 0.00),
                listOf(3.59, 2.53, 0.01, 0.00),
                listOf(3.59, 2.53, 0.01, 0.00),
                listOf(3.59, 2.53, 0.01, 0.00),
                listOf(3.60, 2.52, 0.01, 0.00),
                listOf(3.60, 2.52, 0.01, 0.00),
            )
        ),

        // USD 100.00
        VariableAllocationsCase(
            100.00,
            usd,
            ratiosFor(3).adjustForAllocate(),
            RoundingExpectations(
                listOf(33.34, 33.33, 33.33),
                listOf(33.34, 33.34, 33.32),
                listOf(33.34, 33.34, 33.32),
                listOf(33.34, 33.33, 33.33),
                listOf(33.34, 33.33, 33.33),
                listOf(33.34, 33.33, 33.33),
                listOf(33.34, 33.33, 33.33),
            )
        ),

        // USD 100.00
        VariableAllocationsCase(
            100.00,
            usd,
            listOf(
                { 0.15745723008934218 * 100 }.percent(),
                { 0.16822475044576482 * 100 }.percent(),
                { 0.25450548305790277 * 100 }.percent(),
                { 0.32555527127804923 * 100 }.percent(),
                { 0.09425726512894098 * 100 }.percent(),
            ),
            RoundingExpectations(
                listOf(15.75, 16.82, 25.45, 32.56, 9.42),
                listOf(15.75, 16.83, 25.46, 32.56, 9.40),
                listOf(15.75, 16.83, 25.46, 32.56, 9.40),
                listOf(15.75, 16.82, 25.45, 32.56, 9.42),
                listOf(15.75, 16.82, 25.45, 32.56, 9.42),
                listOf(15.74, 16.82, 25.45, 32.57, 9.42),
                listOf(15.74, 16.82, 25.45, 32.57, 9.42),
            )
        ),

        // USD 100.00
        VariableAllocationsCase(
            100.00,
            usd,
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
            ).adjustForAllocate(),
            RoundingExpectations(
                listOf(9.65, 6.46, 10.70, 12.23, 2.35, 7.67, 16.12, 10.49, 7.47, 16.86),
                listOf(9.66, 6.46, 10.71, 12.24, 2.30, 7.67, 16.13, 10.49, 7.48, 16.86),
                listOf(9.66, 6.46, 10.71, 12.24, 2.30, 7.67, 16.13, 10.49, 7.48, 16.86),
                listOf(9.65, 6.46, 10.70, 12.23, 2.35, 7.67, 16.12, 10.49, 7.47, 16.86),
                listOf(9.65, 6.46, 10.70, 12.23, 2.35, 7.67, 16.12, 10.49, 7.47, 16.86),
                listOf(9.65, 6.45, 10.70, 12.23, 2.34, 7.66, 16.12, 10.48, 7.47, 16.90),
                listOf(9.65, 6.45, 10.70, 12.23, 2.34, 7.66, 16.12, 10.48, 7.47, 16.90),
            )
        ),

        /* Exchange currencies cases */

        // USD[4] 0.0001
        VariableAllocationsCase(
            0.0001,
            exchangeUsd,
            ratiosFor(2),
            RoundingExpectations(
                listOf(0.0001, 0.0000),
                listOf(0.0001, 0.0000),
                listOf(0.0001, 0.0000),
                listOf(0.0001, 0.0000),
                listOf(0.0001, 0.0000),
                listOf(0.0001, 0.0000),
                listOf(0.0001, 0.0000),
            )
        ),
        VariableAllocationsCase(
            0.0001,
            exchangeUsd,
            ratiosFor(3).adjustForAllocate(),
            RoundingExpectations(
                listOf(0.0001, 0.0000, 0.0000),
                listOf(0.0001, 0.0000, 0.0000),
                listOf(0.0001, 0.0000, 0.0000),
                listOf(0.0001, 0.0000, 0.0000),
                listOf(0.0001, 0.0000, 0.0000),
                listOf(0.0001, 0.0000, 0.0000),
                listOf(0.0001, 0.0000, 0.0000),
            )
        ),

        // USD[4] 0.001
        VariableAllocationsCase(
            0.001,
            exchangeUsd,
            ratiosFor(2),
            RoundingExpectations(
                listOf(0.0005, 0.0005),
                listOf(0.0005, 0.0005),
                listOf(0.0005, 0.0005),
                listOf(0.0005, 0.0005),
                listOf(0.0005, 0.0005),
                listOf(0.0005, 0.0005),
                listOf(0.0005, 0.0005),
            )
        ),
        VariableAllocationsCase(
            0.001,
            exchangeUsd,
            ratiosFor(3).adjustForAllocate(),
            RoundingExpectations(
                listOf(0.0004, 0.0003, 0.0003),
                listOf(0.0004, 0.0004, 0.0002),
                listOf(0.0004, 0.0004, 0.0002),
                listOf(0.0004, 0.0003, 0.0003),
                listOf(0.0004, 0.0003, 0.0003),
                listOf(0.0004, 0.0003, 0.0003),
                listOf(0.0004, 0.0003, 0.0003),
            )
        ),

        // USD[4] 0.01
        VariableAllocationsCase(
            0.01,
            exchangeUsd,
            ratiosFor(2),
            RoundingExpectations(
                listOf(0.005, 0.005),
                listOf(0.005, 0.005),
                listOf(0.005, 0.005),
                listOf(0.005, 0.005),
                listOf(0.005, 0.005),
                listOf(0.005, 0.005),
                listOf(0.005, 0.005),
            )
        ),
        VariableAllocationsCase(
            0.01,
            exchangeUsd,
            ratiosFor(3).adjustForAllocate(),
            RoundingExpectations(
                listOf(0.0034, 0.0033, 0.0033),
                listOf(0.0034, 0.0034, 0.0032),
                listOf(0.0034, 0.0034, 0.0032),
                listOf(0.0034, 0.0033, 0.0033),
                listOf(0.0034, 0.0033, 0.0033),
                listOf(0.0034, 0.0033, 0.0033),
                listOf(0.0034, 0.0033, 0.0033),
            )
        ),

        /* Negative cases */

        // USD 100.00
        VariableAllocationsCase(
            -100.00,
            usd,
            ratiosFor(3).adjustForAllocate(),
            RoundingExpectations(
                listOf(-33.33, -33.33, -33.34),
                listOf(-33.32, -33.34, -33.34),
                listOf(-33.32, -33.34, -33.34),
                listOf(-33.33, -33.33, -33.34),
                listOf(-33.33, -33.33, -33.34),
                listOf(-33.33, -33.33, -33.34),
                listOf(-33.33, -33.33, -33.34),
            )
        ),
    )

    val cases = sameAllocationsCases + variableAllocationsCases

    private fun ratiosFor(number: Int) = List(number) { { 1.0 / number * ONE_HUNDRED }.percent() }
}
