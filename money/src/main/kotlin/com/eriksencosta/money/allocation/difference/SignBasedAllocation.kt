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
 * Allocates the [Money] difference according to its sign. Positive values are allocated according to
 * [positiveAllocator], whereas negative values are allocated by [negativeAllocator].
 */
public interface SignBasedAllocation : DifferenceAllocation {
    /**
     * The [DifferenceAllocation] instance to allocate a positive difference value.
     */
    public val positiveAllocator: DifferenceAllocation

    /**
     * The [DifferenceAllocation] instance to allocate a negative difference value.
     */
    public val negativeAllocator: DifferenceAllocation

    override fun allocate(difference: Money, allocations: List<Money>): List<Money> = when (difference.isPositive) {
        true -> positiveAllocator.allocate(difference, allocations)
        false -> negativeAllocator.allocate(difference, allocations)
    }
}
