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

import com.eriksencosta.math.common.NoRounding
import com.eriksencosta.math.common.PreciseRounding
import com.eriksencosta.math.common.Rounding
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.Objects.hash
import kotlin.math.max

/**
 * Represents a monetary value.
 *
 * [Money] is an implementation of the [pattern with the same name](https://martinfowler.com/eaaCatalog/money.html) and
 * provides methods for mathematical operations, allocation, and currency exchange rate. The [Money] type is intended to
 * be used as a base data type.
 */
@Suppress("TooManyFunctions")
public class Money private constructor(
    monetaryAmount: BigDecimal,

    /**
     * The monetary currency.
     */
    public val currency: Currency,

    /**
     * The rounding strategy to use after applying a mathematical calculation in this [Money].
     */
    internal val rounding: Rounding
) : Comparable<Money> {
    /**
     * The monetary amount.
     */
    public val amount: BigDecimal = monetaryAmount.stripTrailingZeros()

    /**
     * `true` if the monetary amount is zero.
     */
    public val isZero: Boolean = 0 == amount.signum()

    /**
     * `true` if the monetary amount is not zero.
     */
    public val isNotZero: Boolean = !isZero

    /**
     * `true` if the monetary amount is positive.
     */
    public val isPositive: Boolean = 1 == amount.signum()

    /**
     * `true` if the monetary amount is positive or zero.
     */
    public val isPositiveOrZero: Boolean = isPositive || isZero

    /**
     * `true` if the monetary amount is negative.
     */
    public val isNegative: Boolean = -1 == amount.signum()

    /**
     * `true` if the monetary amount is negative or zero.
     */
    public val isNegativeOrZero: Boolean = isNegative || isZero

    /**
     * `true` if it has rounding support.
     */
    public val hasRounding: Boolean = rounding is PreciseRounding

    private val mathContext: MathContext by lazy { MathContext(MAX_PRECISION, rounding.mode) }

    /**
     * Creates a positive [Money] based on this instance.
     *
     * @return A positive [Money].
     */
    public operator fun unaryPlus(): Money = if (isPositive) this else -this

    /**
     * Creates a [Money] by negating this instance.
     *
     * @return A negated [Money].
     */
    public operator fun unaryMinus(): Money = of(amount.negate())

    /**
     * Sums this [Money] with a second one.
     *
     * @param[other] The [Money] value to sum to this value.
     * @throws[IllegalArgumentException] When the [other] [currency] is different from this one.
     * @return The resulting [Money].
     */
    public operator fun plus(other: Money): Money = runIfSameCurrency(other, "+") {
        if (other.isZero) this
        else round { amount.add(other.amount, mathContext) }
    }

    /**
     * Subtracts this [Money] with a second one.
     *
     * @param[other] The [Money] value to subtract from this value.
     * @throws[IllegalArgumentException] When the [other] [currency] is different from this one.
     * @return The resulting [Money].
     */
    public operator fun minus(other: Money): Money = runIfSameCurrency(other, "-") {
        if (other.isZero) this
        else round { amount.subtract(other.amount, mathContext) }
    }

    /**
     * Multiplies this [Money].
     *
     * @param[by] The number of times to multiply this [Money] by.
     * @return The resulting [Money].
     */
    public operator fun times(by: Number): Money = when (val factor = by.toStrippedBigDecimal()) {
        BigDecimal.ZERO -> zero()
        BigDecimal.ONE -> round()
        else -> round { amount.multiply(factor, mathContext) }
    }

    /**
     * Divides this [Money].
     *
     * @param[by] The divisor to divide this [Money] by.
     * @return The resulting [Money].
     */
    public operator fun div(by: Number): Money = when (val divisor = by.toStrippedBigDecimal()) {
        BigDecimal.ONE -> this
        else -> round { amount.divide(divisor, mathContext) }
    }

    /**
     * Calculates the ratio between this [Money] and a second one.
     *
     * @param[other] The [Money] value to calculate the ratio.
     * @throws[IllegalArgumentException] When the [other] [currency] is different from this one.
     * @return The ratio between the monetary amounts.
     */
    public infix fun ratio(other: Money): Double = runIfSameCurrency(other, "ratio") {
        amount.divide(other.amount, mathContext).toDouble()
    }

    /**
     * Rounds the [Money] using the current [rounding] strategy.
     *
     * @return The rounded [Money].
     */
    public fun round(): Money = round { amount }

    /**
     * Rounds the [Money] using the specified [mode].
     *
     * @param[mode] The rounding mode to round the monetary amount.
     * @return The rounded [Money].
     */
    public infix fun round(mode: RoundingMode): Money = with(mode).round()

    /**
     * Rounds the [Money] using the specified [rounding] strategy.
     *
     * @param[rounding] The rounding strategy to round the monetary amount.
     * @return The rounded [Money].
     */
    public infix fun round(rounding: Rounding): Money = with(rounding).round()

    /**
     * Creates a [Money] based on this instance but with the specified [mode] to configure the [rounding] strategy.
     *
     * The returned [Money] will use a [Rounding] strategy even if the currency instance doesn't support rounding. In
     * this case, the rounding will use the [Currency] minor units as the precision.
     *
     * @param[mode] The rounding mode to configure the [rounding] strategy.
     * @return A [Money] configured with the [rounding] strategy using the specified [mode].
     */
    public infix fun with(mode: RoundingMode): Money = of(
        amount,
        currency,
        when (hasRounding) {
            true -> Rounding.to(rounding.precision, mode)
            else -> Rounding.to(currency.minorUnits, mode)
        }
    )

    /**
     * Creates a [Money] based on this instance one using the specified [rounding] strategy.
     *
     * @param[rounding] The rounding strategy.
     * @return A [Money] configured with the [rounding] strategy.
     */
    public infix fun with(rounding: Rounding): Money = if (rounding == this.rounding)
        this else of(amount, currency, rounding)

    /**
     * Creates a [Money] based on this instance with the [NoRounding] strategy.
     *
     * @return A [Money] configured with the [NoRounding] strategy.
     */
    public fun noRounding(): Money = if (!hasRounding) this else with(Rounding.no())

    override fun compareTo(other: Money): Int = when (val comparison = currency.compareTo(other.currency)) {
        0 -> amount.compareTo(other.amount)
        else -> comparison
    }

    override fun equals(other: Any?): Boolean = this === other || (
        other is Money &&
            0 == amount.compareTo(other.amount) &&
            currency == other.currency &&
            rounding == other.rounding
        )

    override fun hashCode(): Int = hash(currency.code, amount)

    override fun toString(): String = "%s %s".format(currency.code, formattedAmount())

    internal fun of(amount: BigDecimal): Money = of(amount, currency, rounding)

    internal fun formattedAmount(): String = "%.${formattingDigits()}f".format(amount)

    private inline fun <T> runIfSameCurrency(other: Money, operation: String, block: () -> T): T =
        require(currency.code == other.currency.code) {
            "Currencies mismatch: %s %s %s. The operation must be done using values with the same currency"
                .format(this, operation, other)
        }.let {
            block()
        }

    private inline fun round(block: () -> BigDecimal): Money = of(rounding.round(block()))

    private fun formattingDigits(): Int =
        if (0.0 == (amount.toDouble() - amount.toLong()) && 0 == currency.minorUnits) 0
        else max(currency.minorUnits, amount.scale())

    /**
     * A [Money] factory.
     */
    public companion object Factory {
        private const val MAX_PRECISION: Int = 256

        /**
         * The [RoundingMode] to be used in case the client code explicitly chooses no rounding strategy. Public methods
         * or functions that create a [Money] object must use this mode as the default option.
         */
        internal val defaultRoundingMode: RoundingMode = RoundingMode.HALF_EVEN

        /**
         * Creates a [Money]. Calculations using it are rounded using a [Rounding] strategy configured with
         * [RoundingMode.HALF_EVEN].
         *
         * @param[amount] The monetary amount.
         * @param[currency] The code or secondary code of the currency.
         * @throws[IllegalArgumentException] When no currency is found for [currency].
         * @return A [Money].
         */
        public fun of(amount: Number, currency: String): Money = of(amount, Currency.of(currency), defaultRoundingMode)

        /**
         * Creates a [Money]. Calculations using it are rounded using a [Rounding] strategy configured with the
         * specified [mode].
         *
         * @param[amount] The monetary amount.
         * @param[currency] The code or secondary code of the currency.
         * @param[mode] The rounding mode to configure the [Rounding] strategy.
         * @throws[IllegalArgumentException] When no currency is found for [currency].
         * @return A [Money].
         */
        public fun of(amount: Number, currency: String, mode: RoundingMode): Money =
            of(amount, Currency.of(currency), mode)

        /**
         * Creates a [Money]. Calculations using it are rounded using the specified [Rounding] strategy.
         *
         * @param[amount] The monetary amount.
         * @param[currency] The code or secondary code of the currency.
         * @param[rounding] The rounding strategy.
         * @throws[IllegalArgumentException] When no currency is found for [currency].
         * @return A [Money].
         */
        public fun of(amount: Number, currency: String, rounding: Rounding): Money =
            of(amount, Currency.of(currency), rounding)

        /**
         * Creates a [Money]. Calculations using it are rounded using a [Rounding] strategy configured with
         * [RoundingMode.HALF_EVEN].
         *
         * @param[amount] The monetary amount.
         * @param[currency] The monetary currency.
         * @return A [Money].
         */
        public fun of(amount: Number, currency: Currency): Money = of(amount, currency, defaultRoundingMode)

        /**
         * Creates a [Money]. Calculations using it are rounded using a [Rounding] strategy configured with the
         * specified [mode].
         *
         * @param[amount] The monetary amount.
         * @param[currency] The monetary currency.
         * @param[mode] The rounding mode to configure the [Rounding] strategy.
         * @return A [Money].
         */
        public fun of(amount: Number, currency: Currency, mode: RoundingMode): Money =
            of(amount, currency, Rounding.to(currency.minorUnits, mode))

        /**
         * Creates a [Money]. Calculations using it are rounded using the specified [Rounding] strategy.
         *
         * @param[amount] The monetary amount.
         * @param[currency] The monetary currency.
         * @param[rounding] The rounding strategy.
         * @return A [Money].
         */
        public fun of(amount: Number, currency: Currency, rounding: Rounding): Money =
            Money(BigDecimal(amount.toString()), currency, rounding)
    }
}
