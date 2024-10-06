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
 * Allocates the [Money] difference by distributing every unit of it according to [allocator].
 *
 * @param[allocator] The [DifferenceAllocation] instance to allocate the difference units.
 */
public class Distribute(override val allocator: DifferenceAllocation) : DistributableAllocation {
    public override tailrec fun distribute(units: List<Money>, allocations: List<Money>): List<Money> =
        when (units.isEmpty()) {
            true -> allocations
            false -> distribute(units.drop(1), allocator.allocate(units.first(), allocations))
        }

    /**
     * A [Distribute] factory.
     */
    public companion object Factory {
        private val onFirstGreatest: Distribute by lazy { Distribute(OnFirstGreatest) }
        private val onFirstSmallest: Distribute by lazy { Distribute(OnFirstSmallest) }
        private val onLastGreatest: Distribute by lazy { Distribute(OnLastGreatest) }
        private val onLastSmallest: Distribute by lazy { Distribute(OnLastSmallest) }

        /**
         * Creates a [Distribute] configured with an [OnFirstGreatest] to distribute/allocate the
         * difference units in the allocations list.
         *
         * @return A [Distribute].
         */
        public fun onFirstGreatest(): Distribute = onFirstGreatest

        /**
         * Creates a [Distribute] configured with an [OnFirstSmallest] to distribute/allocate the
         * difference units in the allocations list.
         *
         * @return A [Distribute].
         */
        public fun onFirstSmallest(): Distribute = onFirstSmallest

        /**
         * Creates a [Distribute] configured with an [OnLastGreatest] to distribute/allocate the
         * difference units in the allocations list.
         *
         * @return A [Distribute].
         */
        public fun onLastGreatest(): Distribute = onLastGreatest

        /**
         * Creates a [Distribute] configured with an [OnLastGreatest] to distribute/allocate the
         * difference units in the allocations list.
         *
         * @return A [Distribute].
         */
        public fun onLastSmallest(): Distribute = onLastSmallest
    }
}
