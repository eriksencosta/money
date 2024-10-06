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

package com.eriksencosta.money

import com.eriksencosta.math.percentage.Percentage

/**
 * Converts a [Money] to another currency by the given exchange [rate]. Calculations using the resulting [Money] will be
 * rounded with the strategy of the given [rate] object.
 *
 * @receiver[Money]
 * @param[rate] The exchange rate.
 * @throws[IllegalArgumentException] When [rate] is negative.
 * @return The resulting [Money].
 */
public infix fun Money.exchange(rate: Money): Money = require(rate.isPositiveOrZero) {
    "The exchange rate must be positive"
}.let { rate * noRounding().amount }

/**
 * Creates a [Money] based on this instance but with the monetary amount set to zero. Example:
 *
 * ```
 * val money = "USD 1.50".money()
 * money.zero() // USD 0.00
 * ```
 *
 * @receiver[Money]
 * @return A [Money] with the monetary amount set to zero.
 */
public fun Money.zero(): Money = currency.zero(rounding)

/**
 * Creates a [Money] based on this instance but with the monetary amount set to the smallest unit of the currency.
 * Example:
 *
 * ```
 * val money = "USD 1.50".money()
 * money.smallestUnit() // USD 0.01
 * ```
 *
 * The returned [Money] is always positive.
 *
 * @receiver[Money]
 * @return A [Money] with the monetary amount set to the smallest unit.
 */
public fun Money.smallestUnit(): Money = currency.smallestUnit(rounding)

/**
 * Multiplies this [Money] by the given [Percentage].
 *
 * @receiver[Money]
 * @param[by] The [Percentage] to multiply this [Money] by.
 * @return The resulting [Money].
 */
public operator fun Money.times(by: Percentage): Money = this * by.decimal

/**
 * Increases this [Money] by the given [Percentage].
 *
 * @receiver[Money]
 * @param[by] The [Percentage] to increase this [Money] by.
 * @return The resulting [Money].
 */
public infix fun Money.increaseBy(by: Percentage): Money = when {
    by.isZero -> this
    by.isOneHundred && by.isNegative -> zero()
    else -> this + (this * by)
}

/**
 * Decreases this [Money] by the given [Percentage].
 *
 * @receiver[Money]
 * @param[by] The [Percentage] to decrease this [Money] by.
 * @return The resulting [Money].
 */
public infix fun Money.decreaseBy(by: Percentage): Money = when {
    by.isZero -> this
    by.isOneHundred && by.isPositive -> zero()
    else -> this - (this * by)
}

/**
 * Returns a string representation of the object, including all public properties.
 *
 * @receiver[Money]
 * @return A formatted string.
 */
public fun Money.toDetailedString(): String = "Money[amount=${formattedAmount()} currency=[" +
    "${currency.toDetailedString()}] rounding=[$rounding]"

/**
 * Sums all [Money] elements in the collection.
 *
 * @receiver[Iterable]
 * @throws[UnsupportedOperationException] When the collection is empty.
 * @return A [Money].
 */
public fun Iterable<Money>.sum(): Money = (this as Collection<Money>).sum()

/**
 * Sums all [Money] elements in the collection.
 *
 * @receiver[Collection]
 * @throws[UnsupportedOperationException] When the collection is empty.
 * @return The resulting [Money].
 */
public fun Collection<Money>.sum(): Money = when (isEmpty()) {
    true -> throw UnsupportedOperationException("Empty collection can not be summed")

    // Rounding is done by last; otherwise, the end result would accumulate rounding errors.
    false -> reduce { acc, money -> acc.noRounding() + money }.with(first().rounding).round()
}
