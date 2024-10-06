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
 * Allocates the [Money] difference according to its sign by distributing its difference units.
 *
 * @param[positiveAllocator] The [DifferenceAllocation] instance to distribute a positive difference value units.
 * @param[negativeAllocator] The [DifferenceAllocation] instance to distribute a negative difference value units.
 */
public class DistributeBySign(
    positiveAllocator: DifferenceAllocation,
    negativeAllocator: DifferenceAllocation
) : SignBasedAllocation {
    override val positiveAllocator: DistributableAllocation = positiveAllocator.wrapAsDistributable()
    override val negativeAllocator: DistributableAllocation = negativeAllocator.wrapAsDistributable()

    /**
     * A [DistributeBySign] factory.
     */
    public companion object Factory {
        private val positiveOnFirstSmallest: DistributeBySign by lazy {
            DistributeBySign(
                Distribute.onFirstSmallest(),
                Distribute.onLastGreatest()
            )
        }

        private val positiveOnFirstGreatest: DistributeBySign by lazy {
            DistributeBySign(
                Distribute.onFirstGreatest(),
                Distribute.onLastSmallest()
            )
        }

        /**
         * Creates a [DistributeBySign] configured to distribute/allocate the difference units in the allocations
         * list as follows:
         *
         * * Positive difference: on the first item with the smallest value
         * * Negative difference: on the last item with the greatest value
         *
         * @return A [DistributeBySign].
         */
        public fun positiveOnFirstSmallest(): DistributeBySign = positiveOnFirstSmallest

        /**
         * Creates a [DistributeBySign] configured to distribute/allocate the difference units in the allocations
         * list as follows:
         *
         * * Positive difference: on the first item with the greatest value
         * * Negative difference: on the last item with the smallest value
         *
         * @return A [DistributeBySign].
         */
        public fun positiveOnFirstGreatest(): DistributeBySign = positiveOnFirstGreatest

        private fun DifferenceAllocation.wrapAsDistributable(): DistributableAllocation = when (this) {
            is DistributableAllocation -> this
            else -> Distribute(this)
        }
    }
}
