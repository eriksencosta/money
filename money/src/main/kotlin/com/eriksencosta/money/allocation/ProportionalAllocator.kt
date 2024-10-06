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
import com.eriksencosta.money.allocation.difference.DistributeBySign
import com.eriksencosta.money.sum

/**
 * Allocates a [Money] in ratios. Example:
 *
 * ```
 * val money = 100 money "USD"
 * val ratios = Ratios(75.percent(), 25.percent())
 * val allocator = ProportionalAllocator.default()
 *
 * allocator.allocate(money, ratios).result() // [USD 75.00, USD 25.00]
 * ```
 *
 * @param[differenceAllocator] Strategy to allocate the difference generated when allocating a [Money] amount.
 */
public class ProportionalAllocator(
    override val differenceAllocator: DifferenceAllocation
) : ProportionalAllocation, Allocator<Ratios>() {
    override fun calculate(money: Money, by: Ratios): List<Money> = by.ratios.map { money * it.decimal }

    override fun calculateReverse(allocations: List<Money>, by: Ratios): Money = allocations.sum()

    /**
     * A [ProportionalAllocator] factory.
     */
    public companion object Factory {
        private val instance: ProportionalAllocator by lazy {
            ProportionalAllocator(DistributeBySign.positiveOnFirstGreatest())
        }

        /**
         * Creates a [ProportionalAllocator] object using [DistributeBySign] to distribute the difference in the
         * allocation list as follows:
         *
         * * Positive difference units: on the first item of greatest value
         * * Negative difference units: on the last item of smallest value
         *
         * This strategy distorts the ratios distribution towards the element of greatest value, i.e., the greatest
         * value in the allocation list will increase while the smallest one will decrease.
         *
         * @return A [ProportionalAllocator] object.
         */
        public fun default(): ProportionalAllocator = instance
    }
}
