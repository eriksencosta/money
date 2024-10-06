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

package com.eriksencosta.money.allocation.difference

import com.eriksencosta.money.Currency
import com.eriksencosta.money.Money
import com.eriksencosta.money.smallestUnit

/**
 * Allocates the [Money] difference by distributing every unit of it according to [allocator]. For example, a difference
 * of `USD 0.03` will be allocated by distributing `USD 0.01` three times.
 *
 * This interface implements the Template Method pattern. Subclasses need to override the method [distribute], which
 * will receive a list of [Money] representing the difference units and use the [allocator] to distribute them. A
 * difference unit is always equal to the smallest possible value for a [Money] in a given [Currency], examples:
 *
 * * A difference of `USD 0.03` yields a list with three items with the `USD 0.01` value
 * * A difference of `JPY 2` yields a list with two items with the `JPY 1` value
 */
public interface DistributableAllocation : DifferenceAllocation {
    /**
     * The [DifferenceAllocation] instance to allocate the difference units.
     */
    public val allocator: DifferenceAllocation

    override fun allocate(difference: Money, allocations: List<Money>): List<Money> = run {
        val smallestUnit = when (difference.isPositive) {
            true -> difference.smallestUnit()
            else -> -difference.smallestUnit()
        }

        val ratio = (difference ratio smallestUnit).toInt()
        val units = List(ratio) { smallestUnit }

        distribute(units, allocations)
    }

    /**
     * Distributes the difference units in the [allocations] list.
     *
     * @param[units] The difference units to be allocated.
     * @param[allocations] The allocations list.
     * @return The allocation result.
     */
    public fun distribute(units: List<Money>, allocations: List<Money>): List<Money>
}
