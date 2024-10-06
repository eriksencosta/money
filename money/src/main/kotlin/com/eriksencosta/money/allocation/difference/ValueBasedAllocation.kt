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
 * Allocates the [Money] difference in a specific value in the allocations list.
 */
public interface ValueBasedAllocation : DifferenceAllocation {
    /**
     * Apply the [difference] in a specific [value] in the [allocations] list.
     *
     * @param[difference] A [Money] amount representing the difference to allocate in the allocations list.
     * @param[value] The value in [allocations] to apply the [difference].
     * @param[allocations] The allocations list.
     * @return The allocation result.
     */
    public fun applyDifferenceOnValue(difference: Money, value: Money, allocations: List<Money>): List<Money>
}
