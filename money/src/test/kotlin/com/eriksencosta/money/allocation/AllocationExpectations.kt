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
import com.eriksencosta.money.Currency
import com.eriksencosta.money.Money
import com.eriksencosta.money.sum
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import java.math.RoundingMode
import java.math.RoundingMode.CEILING
import java.math.RoundingMode.DOWN
import java.math.RoundingMode.FLOOR
import java.math.RoundingMode.HALF_DOWN
import java.math.RoundingMode.HALF_EVEN
import java.math.RoundingMode.HALF_UP
import java.math.RoundingMode.UP
import kotlin.test.assertEquals

typealias ExpectedAllocations = List<Pair<RoundingMode, List<Money>>>

internal object AllocationExpectations {
    internal interface Expected {
        fun allocations(): ExpectedAllocations
        fun allocations(transform: (Number, RoundingMode) -> Money): ExpectedAllocations
    }

    internal abstract class Case(open val amount: Number, open val currency: Currency) : Expected {
        abstract val allocateBy: AllocationBy

        private val transform: (Number, RoundingMode) -> Money = { amount, mode ->
            Money.of(amount, currency, mode)
        }

        override fun allocations(): ExpectedAllocations = allocations(transform)

        fun money(mode: RoundingMode): Money = Money.of(amount, currency, mode)

        fun rounding(currency: Currency, mode: RoundingMode): Rounding = Rounding.to(currency.minorUnits, mode)
    }

    internal interface SameAllocations : Expected {
        val expected: List<Number>

        override fun allocations(transform: (Number, RoundingMode) -> Money): ExpectedAllocations = listOf(
            HALF_EVEN to expected.map { transform(it, HALF_EVEN) },
            CEILING to expected.map { transform(it, CEILING) },
            UP to expected.map { transform(it, UP) },
            HALF_UP to expected.map { transform(it, HALF_UP) },
            HALF_DOWN to expected.map { transform(it, HALF_DOWN) },
            DOWN to expected.map { transform(it, DOWN) },
            FLOOR to expected.map { transform(it, FLOOR) }
        )
    }

    internal interface VariableAllocations : Expected {
        val expectations: RoundingExpectations

        override fun allocations(transform: (Number, RoundingMode) -> Money): ExpectedAllocations = listOf(
            HALF_EVEN to expectations.halfEven.map { transform(it, HALF_EVEN) },
            CEILING to expectations.ceiling.map { transform(it, CEILING) },
            UP to expectations.up.map { transform(it, UP) },
            HALF_UP to expectations.halfUp.map { transform(it, HALF_UP) },
            HALF_DOWN to expectations.halfDown.map { transform(it, HALF_DOWN) },
            DOWN to expectations.down.map { transform(it, DOWN) },
            FLOOR to expectations.floor.map { transform(it, FLOOR) },
        )

        data class RoundingExpectations(
            val halfEven: List<Number>,
            val ceiling: List<Number>,
            val up: List<Number>,
            val halfUp: List<Number>,
            val halfDown: List<Number>,
            val down: List<Number>,
            val floor: List<Number>,
        )
    }

    fun <T : AllocationBy> assertions(cases: List<Case>, allocator: Allocation<T>): List<DynamicTest> =
        cases.map { case ->
            case.allocations().map { (mode, expected) ->
                val money = case.money(mode)
                val rounding = case.rounding(case.currency, mode)
                val by = case.allocateBy

                dynamicTest("given $money with $rounding when allocated to $by then I should get $expected") {
                    @Suppress("UNCHECKED_CAST")
                    val result = allocator.allocate(money, by as T)

                    val roundedMoney = money.round()
                    val expectedTotals = expected.sum()
                    val allocationsTotals = result.details().allocationsTotals

                    assertEquals(expected, result.allocations())
                    assertEquals(roundedMoney, expectedTotals, "Expected check")
                    assertEquals(roundedMoney, allocationsTotals, "Allocations check")
                }
            }
        }.flatten()
}
