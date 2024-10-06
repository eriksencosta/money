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

import com.eriksencosta.math.percentage.Percentage
import com.eriksencosta.math.percentage.sum
import com.eriksencosta.money.Money

/**
 * Allocates a [Money] in ratios.
 */
public interface ProportionalAllocation : Allocation<Ratios> {
    /**
     * Allocates a [Money] in ratios.
     *
     * @param[money] The [Money] amount to allocate.
     * @param[ratios] The ratios to allocate.
     * @throws[IllegalArgumentException] When the sum of [ratios] is not 100%.
     * @throws[IllegalArgumentException] When [ratios] contain elements with a negative value.
     * @return A [Result] with the allocation result.
     */
    public fun allocate(money: Money, ratios: List<Percentage>): Result = allocate(money, Ratios(ratios))

    /**
     * Allocates a [Money] in ratios.
     *
     * @param[money] The [Money] amount to allocate.
     * @param[ratios] The ratios to allocate.
     * @throws[IllegalArgumentException] When the sum of [ratios] is not 100%.
     * @throws[IllegalArgumentException] When [ratios] contain elements with a negative value.
     * @return A [Result] with the allocation result.
     */
    public fun allocate(money: Money, vararg ratios: Percentage): Result = allocate(money, Ratios(ratios.toList()))
}

/**
 * Parameter type for proportional allocation.
 *
 * @param[ratios] A list of [Percentage]. The sum of the list items must be equal to 100%.
 * @throws[IllegalArgumentException] When the sum of [ratios] is not 100%.
 * @throws[IllegalArgumentException] When [ratios] contain elements with a negative value.
 */
public class Ratios(public val ratios: List<Percentage>) : AllocationBy {
    /**
     * Creates a [Ratios] instance with the given [Percentage] parameters.
     *
     * @param[ratios] A list of [Percentage]. The sum of the list items must be equal to 100%.
     * @throws[IllegalArgumentException] When the sum of [ratios] is not 100%.
     * @throws[IllegalArgumentException] When [ratios] contain elements with a negative value.
     */
    public constructor(vararg ratios: Percentage) : this(ratios.toList())

    init {
        val sumOfRatios = ratios.sum()
        val negativeRatios = ratios.filter { it.isNegative }

        require(sumOfRatios.isOneHundred) {
            "The sum of the ratios must be 100%. The provided ratios sum up to " +
                "$sumOfRatios. Consider normalizing the list using the function " +
                "com.eriksencosta.money.allocation.adjustForAllocate()"
        }
        require(negativeRatios.isEmpty()) {
            "The ratios list must contain only positive percentages. Invalid values found: " +
                (negativeRatios.joinToString(", ") { it.toString() })
        }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is Ratios && ratios == other.ratios)

    override fun hashCode(): Int = ratios.hashCode()

    override fun toString(): String = ratios.toString()
}
