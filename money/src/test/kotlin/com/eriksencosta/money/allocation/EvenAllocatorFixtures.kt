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

import com.eriksencosta.money.Currency
import com.eriksencosta.money.allocation.AllocationExpectations.Case
import com.eriksencosta.money.allocation.AllocationExpectations.SameAllocations
import com.eriksencosta.money.allocation.AllocationExpectations.VariableAllocations
import com.eriksencosta.money.allocation.AllocationExpectations.VariableAllocations.RoundingExpectations
import com.eriksencosta.money.allocation.Fixtures.exchangeJpy
import com.eriksencosta.money.allocation.Fixtures.exchangeUsd
import com.eriksencosta.money.allocation.Fixtures.jpy
import com.eriksencosta.money.allocation.Fixtures.usd

internal object EvenAllocatorFixtures {
    internal abstract class EvenAllocatorCase(
        override val amount: Number,
        override val currency: Currency,
        open val parts: Int
    ) : Case(amount, currency) {
        override val allocateBy: AllocationBy get() = EvenParts(parts)
    }

    internal data class SameAllocationsCase(
        override val amount: Number,
        override val currency: Currency,
        override val parts: Int,
        override val expected: List<Number>,
    ) : EvenAllocatorCase(amount, currency, parts), SameAllocations

    internal data class VariableAllocationsCase(
        override val amount: Number,
        override val currency: Currency,
        override val parts: Int,
        override val expectations: RoundingExpectations
    ) : EvenAllocatorCase(amount, currency, parts), VariableAllocations

    private val sameAllocationsCases = listOf(
        /* Standardized currencies cases */

        // USD 0.00
        SameAllocationsCase(0.00, usd, 1, List(1) { 0.00 }),
        SameAllocationsCase(0.00, usd, 2, List(2) { 0.00 }),
        SameAllocationsCase(0.00, usd, 4, List(4) { 0.00 }),

        // USD 1.00
        SameAllocationsCase(1.00, usd, 1, List(1) { 1.00 }),
        SameAllocationsCase(1.00, usd, 2, List(2) { 0.50 }),
        SameAllocationsCase(1.00, usd, 4, List(4) { 0.25 }),

        // USD 0.10
        SameAllocationsCase(0.10, usd, 1, List(1) { 0.10 }),
        SameAllocationsCase(0.10, usd, 2, List(2) { 0.05 }),

        // USD 0.01
        SameAllocationsCase(0.01, usd, 1, List(1) { 0.01 }),

        // JPY 1000
        SameAllocationsCase(1000, jpy, 1, List(1) { 1000 }),
        SameAllocationsCase(1000, jpy, 2, List(2) { 500 }),
        SameAllocationsCase(1000, jpy, 4, List(4) { 250 }),

        // JPY 100
        SameAllocationsCase(100, jpy, 1, List(1) { 100 }),
        SameAllocationsCase(100, jpy, 2, List(2) { 50 }),
        SameAllocationsCase(100, jpy, 4, List(4) { 25 }),

        // JPY 10
        SameAllocationsCase(10, jpy, 1, List(1) { 10 }),
        SameAllocationsCase(10, jpy, 2, List(2) { 5 }),

        /* Exchange currencies cases */

        // USD[4] 0.0010
        SameAllocationsCase(0.0010, exchangeUsd, 2, List(2) { 0.0005 }),

        // JPY[4] 0.0010
        SameAllocationsCase(0.0010, exchangeJpy, 2, List(2) { 0.0005 }),

        /* Negative cases */

        // USD 1.00
        SameAllocationsCase(-1.00, usd, 1, List(1) { -1.00 }),
        SameAllocationsCase(-1.00, usd, 2, List(2) { -0.50 }),
        SameAllocationsCase(-1.00, usd, 4, List(4) { -0.25 }),

        // USD 0.10
        SameAllocationsCase(-0.10, usd, 1, List(1) { -0.10 }),
        SameAllocationsCase(-0.10, usd, 2, List(2) { -0.05 }),

        // USD 0.01
        SameAllocationsCase(-0.01, usd, 1, List(1) { -0.01 }),
    )

    private val variableAllocationsCases = listOf(
        /* Standardized currencies cases */

        // USD 0.0001
        VariableAllocationsCase(
            0.0001,
            usd,
            2,
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
            3,
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
            2,
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
            3,
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
            2,
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
            3,
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
            3,
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
            4,
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
            5,
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

        // USD 62.00
        VariableAllocationsCase(
            62.00,
            usd,
            7,
            RoundingExpectations(
                List(6) { 8.86 } + 8.84,
                List(6) { 8.86 } + 8.84,
                List(6) { 8.86 } + 8.84,
                List(6) { 8.86 } + 8.84,
                List(6) { 8.86 } + 8.84,
                listOf(8.90) + List(6) { 8.85 },
                listOf(8.90) + List(6) { 8.85 },
            )
        ),

        // USD 100.00
        VariableAllocationsCase(
            100.00,
            usd,
            3,
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

        // USD 9847.00
        VariableAllocationsCase(
            9847.00,
            usd,
            12,
            RoundingExpectations(
                listOf(820.62) + List(11) { 820.58 },
                List(11) { 820.59 } + 820.51,
                List(11) { 820.59 } + 820.51,
                listOf(820.62) + List(11) { 820.58 },
                listOf(820.62) + List(11) { 820.58 },
                listOf(820.62) + List(11) { 820.58 },
                listOf(820.62) + List(11) { 820.58 },
            )
        ),

        /* Exchange currencies cases */

        // USD[4] 0.0001
        VariableAllocationsCase(
            0.0001,
            exchangeUsd,
            2,
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
            3,
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
            2,
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
            3,
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
            2,
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
            3,
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
            3,
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
}
