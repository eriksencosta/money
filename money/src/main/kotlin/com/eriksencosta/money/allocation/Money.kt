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
import com.eriksencosta.money.Money

/**
 * Allocates a [Money] according to the [by] parameter:
 *
 * * If [by] is of type [EvenParts], allocates the [Money] in equal parts
 * * If [by] is of type [Ratios], allocate the [Money] in the given ratios
 *
 * Example:
 *
 * ```
 * val money = 100 money "USD"
 * val evenParts = EvenParts(3)
 * val ratios = Ratios(75.percent(), 25.percent())
 *
 * money.allocate(evenParts).result() // [USD 33.34, USD 33.33, USD 33.33]
 * money.allocate(ratios).result()    // [USD 75.00, USD 25.00]
 * ```
 *
 * @receiver[Money]
 * @param[by] The parameter to calculate the allocations.
 * @return A [Result] with the allocation result.
 */
public infix fun Money.allocate(by: AllocationBy): Result = when (by) {
    is EvenParts -> EvenAllocator.default().allocate(this, by)
    is Ratios -> ProportionalAllocator.default().allocate(this, by)
}

/**
 * Allocates a [Money] in equal parts. Example:
 *
 * ```
 * val money = 100 money "USD"
 *
 * money.allocate(3) // [USD 33.34, USD 33.33, USD 33.33]
 * ```
 *
 * @receiver[Money]
 * @param[parts] The number of parts to allocate.
 * @throws[IllegalArgumentException] When [parts] is zero or negative.
 * @return A [Result] with the allocation result.
 */
public infix fun Money.allocate(parts: Int): Result = allocate(EvenParts(parts))

/**
 * Allocates a [Money] in ratios. Example:
 *
 * ```
 * val money = 100 money "USD"
 * val ratios = Ratios(75.percent(), 25.percent())
 *
 * money.allocate(ratios) // [USD 75.00, USD 25.00]
 * ```
 *
 * @receiver[Money]
 * @param[ratios] The ratios to allocate.
 * @throws[IllegalArgumentException] When the sum of [ratios] is not 100%.
 * @throws[IllegalArgumentException] When [ratios] contain elements with a negative value.
 * @return A [Result] with the allocation result.
 */
public infix fun Money.allocate(ratios: List<Percentage>): Result = allocate(Ratios(ratios))

/**
 * Allocates a [Money] in ratios. Example:
 *
 * ```
 * val money = 100 money "USD"
 *
 * money.allocate(75.percent(), 25.percent())) // [USD 75.00, USD 25.00]
 * ```
 *
 * @receiver[Money]
 * @param[ratios] The ratios to allocate.
 * @throws[IllegalArgumentException] When the sum of [ratios] is not 100%.
 * @throws[IllegalArgumentException] When [ratios] contain elements with a negative value.
 * @return A [Result] with the allocation result.
 */
public fun Money.allocate(vararg ratios: Percentage): Result = allocate(Ratios(ratios.toList()))
