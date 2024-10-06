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

import com.eriksencosta.money.Money
import com.eriksencosta.money.allocation.difference.DifferenceAllocation
import com.eriksencosta.money.sum
import com.eriksencosta.money.zero
import java.util.Objects.hash

/**
 * Represents the result of the allocation operation.
 *
 * @see Allocation.allocate
 */
public class Result internal constructor(
    /**
     * The original [Money] amount that was allocated in [calculations].
     */
    private val money: Money,

    /**
     * The allocation sanity check calculated by an [Allocation] implementation.
     */
    private val reverse: Money,

    /**
     * The allocations calculated by an [Allocation] implementation.
     */
    private val calculations: List<Money>,

    /**
     * The [DifferenceAllocation] strategy to allocate any difference found between [money] and [reverse].
     */
    private val differenceAllocator: DifferenceAllocation
) {
    private val allocations: List<Money> by lazy {
        when {
            isAllocated() -> noDifference()
            isIndivisible() -> indivisible()
            else -> allocate()
        }
    }

    private val details: Details by lazy {
        val adjustments = (calculations zip allocations).map { it.second - it.first }

        Details(calculations, adjustments, allocations)
    }

    /**
     * Returns the allocation result.
     *
     * @return The allocation result.
     */
    public fun allocations(): List<Money> = allocations

    /**
     * Returns the detailed allocation results.
     *
     * @return A [Details] with the detailed allocation results.
     */
    public fun details(): Details = details

    internal operator fun unaryMinus(): Result = Result(
        -money,
        -reverse,
        calculations.map { -it },
        differenceAllocator
    )

    override fun equals(other: Any?): Boolean = this === other || (
        other is Result &&
            money == other.money &&
            reverse == other.reverse &&
            details == other.details
        )

    override fun hashCode(): Int = hash(money, reverse, details)

    override fun toString(): String = allocations().toString()

    private fun isAllocated(): Boolean = reverse == money

    private fun isIndivisible(): Boolean = calculations[0] == money

    private fun noDifference(): List<Money> = calculations

    private fun indivisible(): List<Money> = listOf(money) + money.zero().let { zero ->
        List(calculations.size - 1) { zero }
    }

    private fun allocate(): List<Money> = differenceAllocator.allocate(money - reverse, calculations)

    /**
     * Provides more details on the allocation result.
     */
    public class Details internal constructor(
        /**
         * The allocations calculated by an [Allocation] implementation.
         */
        public val calculations: List<Money>,

        /**
         * The adjustments applied to correct a possible rounding issue generated in the allocation calculation, i.e.,
         * the allocation of the difference between the allocation result and the original [Money] amount.
         */
        public val adjustments: List<Money>,

        /**
         * The allocation result.
         */
        public val allocations: List<Money>
    ) {
        /**
         * The sum of the [calculations] list items.
         */
        public val calculationsTotals: Money by lazy { calculations.sum() }

        /**
         * The sum of the [adjustments] list items.
         */
        public val adjustmentsTotals: Money by lazy { adjustments.sum() }

        /**
         * The sum of the [allocations] list items.
         */
        public val allocationsTotals: Money by lazy { allocations.sum() }

        override fun equals(other: Any?): Boolean = this === other || (
            other is Details &&
                calculations == other.calculations &&
                adjustments == other.adjustments &&
                allocations == other.allocations
            )

        override fun hashCode(): Int = hash(calculations, adjustments, allocations)

        override fun toString(): String = "Details[calculations=$calculations, adjustments=$adjustments, " +
            "allocations=$allocations]"
    }
}
