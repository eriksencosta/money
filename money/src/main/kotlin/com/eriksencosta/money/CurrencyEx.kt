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

@file:Suppress("TooManyFunctions")

package com.eriksencosta.money

import com.eriksencosta.math.common.Rounding
import com.eriksencosta.money.Money.Factory.defaultRoundingMode
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow

/**
 * Creates a [Money] based on this [Currency]. Example:
 *
 * ```
 * val usd = Currency.of("USD")
 *
 * usd money 1.23    // USD 1.23
 * usd money 1234.56 // USD 1234.56
 * ```
 *
 * Calculations using it are rounded using a [Rounding] strategy configured with [RoundingMode.HALF_EVEN].
 *
 * @receiver[Currency]
 * @param[amount] The monetary amount.
 * @return A [Money].
 */
public infix fun Currency.money(amount: Number): Money = money(amount, defaultRoundingMode)

/**
 * Creates a [Money] based on this [Currency]. Calculations using it are rounded using a [Rounding] strategy configured
 * with the specified [mode].
 *
 * @receiver[Currency]
 * @param[amount] The monetary amount.
 * @param[mode] The rounding mode to configure the [Rounding] strategy.
 * @return A [Money].
 */
public fun Currency.money(amount: Number, mode: RoundingMode): Money =
    Money.of(BigDecimal(amount.toString()), this, mode)

/**
 * Transforms a collection of [Number] into [Money] based on this [Currency]. Calculations using them are rounded using
 * a [Rounding] strategy configured with [RoundingMode.HALF_EVEN].
 *
 * @receiver[Currency]
 * @param[values] A collection of [Number] values.
 * @return A collection of [Money].
 */
public infix fun Currency.money(values: Iterable<Number>): Iterable<Money> = money(values, defaultRoundingMode)

/**
 * Transforms a collection of [Number] into [Money] based on this [Currency]. Calculations using them are rounded using
 * a [Rounding] strategy configured with the specified [mode].
 *
 * @receiver[Currency]
 * @param[values] A collection of [Number] values.
 * @param[mode] The rounding mode to configure the [Rounding] strategy.
 * @return A collection of [Money].
 */
public fun Currency.money(values: Iterable<Number>, mode: RoundingMode): Iterable<Money> =
    values.map { money(it, mode) }

/**
 * Creates a [Money] based on this [Currency] with its amount set to zero. Example:
 *
 * ```
 * val usd = Currency.of("USD")
 * usd.zero() // USD 0.00
 * ```
 *
 * Calculations using it are rounded using a [Rounding] strategy configured with [RoundingMode.HALF_EVEN].
 *
 * @receiver[Currency]
 * @return A [Money] with the monetary amount set to zero.
 */
public fun Currency.zero(): Money = zero(defaultRoundingMode)

/**
 * Creates a [Money] based on this [Currency] with its amount set to zero. Calculations using it are rounded using a
 * [Rounding] strategy configured with the specified [mode].
 *
 * @receiver[Currency]
 * @param[mode] The rounding mode to configure the [Rounding] strategy.
 * @return A [Money] with the monetary amount set to zero.
 */
public infix fun Currency.zero(mode: RoundingMode): Money = zero { Money.of(it, this, mode) }

/**
 * Creates a [Money] representing the smallest unit of value based on this [Currency]. Example:
 *
 * ```
 * val usd = Currency.of("USD")
 * usd.smallestUnit() // USD 0.01
 * ```
 *
 * Calculations using it are rounded using a [Rounding] strategy configured with [RoundingMode.HALF_EVEN].
 *
 * @receiver[Currency]
 * @return A [Money] with the monetary amount set to the smallest unit of value based on this [Currency].
 */
public fun Currency.smallestUnit(): Money = smallestUnit(defaultRoundingMode)

/**
 * Creates a [Money] representing the smallest unit of value based on this [Currency]. Calculations using it are rounded
 * using a [Rounding] strategy configured with the specified [mode].
 *
 * @receiver[Currency]
 * @param[mode] The rounding mode to configure the [Rounding] strategy.
 * @return A [Money] with the monetary amount set to the smallest unit of value based on this [Currency].
 */
public fun Currency.smallestUnit(mode: RoundingMode): Money = smallestUnit {
    Money.of(it, this, mode)
}

/**
 * Returns a string representation of the object, including all public properties.
 *
 * @receiver[Currency]
 * @return A formatted string.
 */
public fun Currency.toDetailedString(): String = "${javaClass.simpleName}[code=$code secondaryCode=$secondaryCode " +
    "name=$name symbol=$symbol type=$type minorUnits=$minorUnits]"

internal fun Currency.zero(rounding: Rounding): Money = zero { Money.of(it, this, rounding) }

private inline fun zero(block: (BigDecimal) -> Money): Money = block(BigDecimal.ZERO)

internal fun Currency.smallestUnit(rounding: Rounding): Money = smallestUnit { Money.of(it, this, rounding) }

private inline fun Currency.smallestUnit(block: (BigDecimal) -> Money): Money = when (minorUnits) {
    0 -> block(BigDecimal.ONE)
    else -> block((1.0 / 10.0.pow(minorUnits.toDouble())).toBigDecimal())
}

internal fun StandardizedCurrency.toCustomCurrency(minorUnits: Int? = null): Currency = minorUnits?.let {
    when (minorUnits == this.minorUnits) {
        true -> this
        false -> toCustomCurrency(minorUnits)
    }
} ?: this
