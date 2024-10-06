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

import com.eriksencosta.money.Money

/**
 * Allocates the [Money] difference on the item with the smallest value in the allocations list.
 *
 * This allocation strategy tries to prevent the creation of negative values in the allocations list when the difference
 * is negative. If the difference is positive, it tries to eliminate any negative value available in the list.
 */
public interface OnSmallestValueAllocation : ValueBasedAllocation {
    override fun allocate(difference: Money, allocations: List<Money>): List<Money> =
        applyDifferenceOnValue(difference, findSmallest(allocations, predicates(difference)), allocations)

    private fun predicates(difference: Money): List<(Money) -> Boolean> = when (difference.isNegative) {
        true -> listOf(
            { it.isPositive },
            { it.isZero },
            { it.isNegative },
        )
        false -> listOf(
            { it.isNegative },
            { it.isZero },
            { it.isPositive },
        )
    }

    private tailrec fun findSmallest(allocations: List<Money>, predicates: List<(Money) -> Boolean>): Money {
        val items = allocations.filter { predicates.first()(it) }

        return when {
            1 == items.size -> items[0]
            items.isNotEmpty() -> items.min()
            else -> findSmallest(allocations, predicates.drop(1))
        }
    }
}
