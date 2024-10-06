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
import com.eriksencosta.money.Money
import com.eriksencosta.money.allocation.Fixtures.fourCents
import com.eriksencosta.money.allocation.Fixtures.oneCent
import com.eriksencosta.money.allocation.Fixtures.threeCents
import com.eriksencosta.money.allocation.Fixtures.twoCents
import com.eriksencosta.money.allocation.Fixtures.zeroCents
import com.eriksencosta.money.money
import com.eriksencosta.money.zero

internal object AllocationFixtures {
    internal data class Case(
        val money: Money,
        val reverse: Money,
        val allocations: List<Money>,
        val expected: Expected
    ) {
        val adjustment: Money = money - reverse

        companion object {
            fun noDifference(money: Money, allocation: Money, size: Int): Case =
                Case(money, money, List(size) { allocation }, Expected.noDifference(allocation, size))

            fun indivisible(money: Money, size: Int): Case =
                Case(money, money, listOf(money) + List(size - 1) { zeroCents }, Expected.indivisible(money, size))

            fun allocate(money: Money, reverse: Money, calculated: List<Money>, allocations: List<Money>): Case =
                Case(money, reverse, calculated, Expected.allocate(calculated, allocations))
        }
    }

    internal data class Expected(
        val calculated: List<Money>,
        val adjustments: List<Money>,
        val result: List<Money>
    ) {
        companion object {
            fun noDifference(money: Money, size: Int): Expected = Expected(
                List(size) { money },
                List(size) { money.zero() },
                List(size) { money }
            )

            fun indivisible(money: Money, size: Int): Expected = Expected(
                listOf(money) + List(size - 1) { money.zero() },
                List(size) { money.zero() },
                listOf(money) + List(size - 1) { money.zero() },
            )

            fun allocate(calculated: List<Money>, allocations: List<Money>): Expected = Expected(
                calculated,
                (calculated zip allocations).map { it.second - it.first },
                allocations
            )
        }
    }

    val cases = listOf(
        /* No difference cases */
        // USD 1.00
        Case.noDifference(1.00 money "USD", 1.00 money "USD", 1),
        Case.noDifference(1.00 money "USD", 0.50 money "USD", 2),
        Case.noDifference(1.00 money "USD", 0.25 money "USD", 4),

        // USD 0.10
        Case.noDifference(0.10 money "USD", 0.10 money "USD", 1),
        Case.noDifference(0.10 money "USD", 0.05 money "USD", 2),

        // USD 0.01
        Case.noDifference(oneCent, oneCent, 1),

        // USD 0.00
        Case.noDifference(zeroCents, zeroCents, 1),
        Case.noDifference(zeroCents, zeroCents, 2),
        Case.noDifference(zeroCents, zeroCents, 4),

        // JPY 1000
        Case.noDifference(1000 money "JPY", 1000 money "JPY", 1),
        Case.noDifference(1000 money "JPY", 500 money "JPY", 2),
        Case.noDifference(1000 money "JPY", 250 money "JPY", 4),

        // JPY 100
        Case.noDifference(100 money "JPY", 100 money "JPY", 1),
        Case.noDifference(100 money "JPY", 50 money "JPY", 2),
        Case.noDifference(100 money "JPY", 25 money "JPY", 4),

        // USD[4] 0.0010
        Case.noDifference(0.0010.money("USD", 4), Money.of(0.0005, Currency.of("USD").toCustomCurrency(4)), 2),

        // JPY[4] 0.0010
        Case.noDifference(0.0010.money("JPY", 4), Money.of(0.0005, Currency.of("JPY").toCustomCurrency(4)), 2),

        /* Indivisible cases */
        Case.indivisible(oneCent, 1),
        Case.indivisible(oneCent, 2),
        Case.indivisible(oneCent, 3),
        Case.indivisible(oneCent, 4),

        /* Allocation cases */
        Case.allocate(
            twoCents,
            threeCents,
            listOf(oneCent, oneCent, oneCent, zeroCents),
            listOf(oneCent, oneCent, zeroCents, zeroCents)
        ),
        Case.allocate(
            twoCents,
            fourCents,
            List(4) { oneCent },
            listOf(oneCent, oneCent, zeroCents, zeroCents)
        ),
        Case.allocate(
            62.00 money "USD",
            62.02 money "USD",
            List(7) { 8.86 money "USD" },
            List(5) { 8.86 money "USD" } + List(2) { 8.85 money "USD" },
        ),
        Case.allocate(
            100.00 money "USD",
            99.99 money "USD",
            listOf(33.33 money "USD", 33.33 money "USD", 33.33 money "USD"),
            listOf(33.34 money "USD", 33.33 money "USD", 33.33 money "USD"),
        )
    )
}
