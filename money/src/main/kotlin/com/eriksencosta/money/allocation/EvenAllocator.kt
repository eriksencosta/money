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
import com.eriksencosta.money.allocation.difference.PositiveOnGreatest

/**
 * Allocates a [Money] in equal parts. Example:
 *
 * ```
 * val money = 100 money "USD"
 * val allocator = EvenAllocator.default()
 *
 * allocator.allocate(money, 3).result() // [USD 33.34, USD 33.33, USD 33.33]
 * ```
 *
 * @param[differenceAllocator] Strategy to allocate the difference generated when allocating a [Money] amount.
 */
public class EvenAllocator(
    override val differenceAllocator: DifferenceAllocation
) : EvenAllocation, Allocator<EvenParts>() {
    override fun calculate(money: Money, by: EvenParts): List<Money> = (money / by.parts).let { result ->
        List(by.parts) { result }
    }

    override fun calculateReverse(allocations: List<Money>, by: EvenParts): Money = allocations[0] * by.parts

    /**
     * A [EvenAllocator] factory.
     */
    public companion object Factory {
        private val instance: EvenAllocator by lazy { EvenAllocator(PositiveOnGreatest) }

        /**
         * Creates an [EvenAllocator] object, using [PositiveOnGreatest] as the difference allocation strategy.
         *
         * @return A [EvenAllocator] object.
         */
        public fun default(): EvenAllocator = instance
    }
}
