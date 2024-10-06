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

/**
 * Creates a [Money] based on this [Number]. Calculations using it are rounded using a [Rounding] strategy configured
 * with [RoundingMode.HALF_EVEN].
 *
 * @receiver[Number]
 * @param[currency] The code or secondary code of the currency.
 * @throws[IllegalArgumentException] When no currency is found for [currency].
 * @return A [Money].
 */
public infix fun Number.money(currency: String): Money = money(currency, defaultRoundingMode, null)

/**
 * Creates a [Money] based on this [Number]. Calculations using it are rounded using a [Rounding] strategy configured
 * with the specified [mode].
 *
 * @receiver[Number]
 * @param[currency] The code or secondary code of the currency.
 * @param[mode] The rounding mode to configure the [Rounding] strategy.
 * @throws[IllegalArgumentException] When no currency is found for [currency].
 * @return A [Money].
 */
public fun Number.money(currency: String, mode: RoundingMode): Money = money(currency, mode, null)

/**
 * Creates a [Money] based on this [Number]. Calculations using it are rounded using a [Rounding] strategy configured
 * with [RoundingMode.HALF_EVEN].
 *
 * @receiver[Number]
 * @param[currency] The code or secondary code of the currency.
 * @param[minorUnits] The minor units (i.e., the decimal places) of the currency. If defined, the returned [Money]
 *   will use a [CustomCurrency] based on a [StandardizedCurrency].
 * @throws[IllegalArgumentException] When no currency is found for [currency].
 * @throws[IllegalArgumentException] When [minorUnits] is lower than a zero.
 * @return A [Money].
 */
public fun Number.money(currency: String, minorUnits: Int): Money = money(currency, defaultRoundingMode, minorUnits)

/**
 * Creates a [Money] based on this [Number]. Calculations using it are rounded using a [Rounding] strategy configured
 * with the specified [mode].
 *
 * @receiver[Number]
 * @param[currency] The code or secondary code of the currency.
 * @param[mode] The rounding mode to configure the [Rounding] strategy.
 * @param[minorUnits] The minor units (i.e., the decimal places) of the currency. If defined, the returned [Money]
 *   will use a [CustomCurrency] based on a [StandardizedCurrency].
 * @throws[IllegalArgumentException] When no currency is found for [currency].
 * @throws[IllegalArgumentException] When [minorUnits] is lower than a zero.
 * @return A [Money].
 */
public fun Number.money(currency: String, mode: RoundingMode, minorUnits: Int? = null): Money =
    Money.of(this, Currency.of(currency).toCustomCurrency(minorUnits), mode)

/**
 * Creates a [Money] based on this [Number]. Calculations using it are rounded using a [Rounding] strategy configured
 * with [RoundingMode.HALF_EVEN].
 *
 * @receiver[Number]
 * @param[currency] A [Currency].
 * @return A [Money].
 */
public infix fun Number.money(currency: Currency): Money = money(currency, defaultRoundingMode)

/**
 * Creates a [Money] based on this [Number]. Calculations using it are rounded using a [Rounding] strategy configured
 * with the specified [mode].
 *
 * @receiver[Number]
 * @param[currency] A [Currency].
 * @param[mode] The rounding mode to configure the [Rounding] strategy.
 * @return A [Money].
 */
public fun Number.money(currency: Currency, mode: RoundingMode): Money = Money.of(this, currency, mode)

/**
 * Multiplies a given [Money] by this [Number].
 *
 * @receiver[Number]
 * @param[money] A [Money] to multiply by this [Number].
 * @return The resulting [Money].
 */
public operator fun Number.times(money: Money): Money = money * this

/**
 * Transforms a collection of [Number] into [Money]. Calculations using them are rounded using a [Rounding] strategy
 * configured with [RoundingMode.HALF_EVEN].
 *
 * @receiver[Iterable]
 * @param[currency] The code or secondary code of the currency.
 * @return A collection of [Money].
 * @throws[IllegalArgumentException] When no currency is found for [currency].
 */
public infix fun Iterable<Number>.money(currency: String): Iterable<Money> =
    money(Currency.of(currency), defaultRoundingMode)

/**
 * Transforms a collection of [Number] into [Money]. Calculations using it are rounded using a [Rounding] strategy
 * configured with the specified [mode].
 *
 * @receiver[Iterable]
 * @param[currency] The code or secondary code of the currency.
 * @param[mode] The rounding mode to configure the [Rounding] strategy.
 * @return A collection of [Money].
 * @throws[IllegalArgumentException] When no currency is found for [currency].
 */
public fun Iterable<Number>.money(currency: String, mode: RoundingMode): Iterable<Money> =
    money(Currency.of(currency), mode)

/**
 * Transforms a collection of [Number] into [Money]. Calculations using them are rounded using a [Rounding] strategy
 * configured with [RoundingMode.HALF_EVEN].
 *
 * @receiver[Iterable]
 * @param[currency] A [Currency].
 * @return A collection of [Money].
 */
public infix fun Iterable<Number>.money(currency: Currency): Iterable<Money> = money(currency, defaultRoundingMode)

/**
 * Transforms a collection of [Number] into [Money]. Calculations using it are rounded using a [Rounding] strategy
 * configured with the specified [mode].
 *
 * @receiver[Iterable]
 * @param[currency] A [Currency].
 * @param[mode] The rounding mode to configure the [Rounding] strategy.
 * @return A collection of [Money].
 */
public fun Iterable<Number>.money(currency: Currency, mode: RoundingMode): Iterable<Money> = map {
    Money.of(it, currency, mode)
}

internal fun Number.toBigDecimal(): BigDecimal = toString().toBigDecimal()

internal fun Number.toStrippedBigDecimal(): BigDecimal = toString().toBigDecimal().stripTrailingZeros()
