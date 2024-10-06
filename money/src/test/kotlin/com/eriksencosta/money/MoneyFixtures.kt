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

import com.eriksencosta.math.common.Rounding
import com.eriksencosta.math.percentage.Percentage
import java.math.BigDecimal
import java.math.RoundingMode

@Suppress("LargeClass")
internal object MoneyFixtures {
    internal data class AccessorsTestTable(
        val money: Money,
        val isZero: Boolean,
        val isNotZero: Boolean,
        val isPositive: Boolean,
        val isPositiveOrZero: Boolean,
        val isNegative: Boolean,
        val isNegativeOrZero: Boolean
    )

    internal data class ExchangeCase(
        val source: Money,
        val rate: Money,
        val expected: Money
    )

    internal interface PercentageRoundingCase {
        val amount: Number
        val currency: String
        val percentage: Percentage
        val halfEven: Number
        val ceiling: Number
        val up: Number
        val halfUp: Number
        val halfDown: Number
        val down: Number
        val floor: Number

        fun expectations(): List<Triple<Money, Percentage, Money>> = mapOf(
            RoundingMode.HALF_EVEN to halfEven,
            RoundingMode.CEILING to ceiling,
            RoundingMode.UP to up,
            RoundingMode.HALF_UP to halfUp,
            RoundingMode.HALF_DOWN to halfDown,
            RoundingMode.DOWN to down,
            RoundingMode.FLOOR to floor,
        ).map { (key, value) ->
            Triple(Money.of(amount, currency, key), percentage, Money.of(value, currency, key))
        }
    }

    internal data class IncreaseByPercentageCase(
        override val amount: Number,
        override val currency: String,
        override val percentage: Percentage,
        override val halfEven: Number,
        override val ceiling: Number,
        override val up: Number,
        override val halfUp: Number,
        override val halfDown: Number,
        override val down: Number,
        override val floor: Number,
    ) : PercentageRoundingCase

    internal data class DecreaseByPercentageCase(
        override val amount: Number,
        override val currency: String,
        override val percentage: Percentage,
        override val halfEven: Number,
        override val ceiling: Number,
        override val up: Number,
        override val halfUp: Number,
        override val halfDown: Number,
        override val down: Number,
        override val floor: Number,
    ) : PercentageRoundingCase

    internal data class DisabledRoundingCase(
        val money: Money,
        val expected: Money,
        val expectedRounding: Money,
        val expectedRoundingMode: Money
    )

    private val zero = Money.of(0.00, "USD")
    private val one = Money.of(1.00, "USD")
    private val minusOne = Money.of(-1.00, "USD")

    val accessors = listOf(
        AccessorsTestTable(
            minusOne,
            isZero = false,
            isNotZero = true,
            isPositive = false,
            isPositiveOrZero = false,
            isNegative = true,
            isNegativeOrZero = true
        ),
        AccessorsTestTable(
            zero,
            isZero = true,
            isNotZero = false,
            isPositive = false,
            isPositiveOrZero = true,
            isNegative = false,
            isNegativeOrZero = true
        ),
        AccessorsTestTable(
            one,
            isZero = false,
            isNotZero = true,
            isPositive = true,
            isPositiveOrZero = true,
            isNegative = false,
            isNegativeOrZero = false
        ),
    )

    val roundingSupport = listOf(
        Pair(Money.of(100.00, "USD"), true),
        Pair(Money.of(100.00, "USD", RoundingMode.UP), true),
        Pair(Money.of(100.00, "USD", Rounding.no()), false),
    )

    val unaryPlus = listOf(
        Money.of(100.00, "USD") to Money.of(100.00, "USD"),
        Money.of(-100.00, "USD") to Money.of(100.00, "USD"),

        // Rounding cases
        Pair(
            Money.of(-100.00, "USD", RoundingMode.UP),
            Money.of(100.00, "USD", RoundingMode.UP)
        ),
        Pair(
            Money.of(-100.00, "USD", Rounding.to(4, RoundingMode.DOWN)),
            Money.of(100.00, "USD", Rounding.to(4, RoundingMode.DOWN))
        ),
    )

    val unaryMinus = listOf(
        Money.of(100.00, "USD") to Money.of(-100.00, "USD"),
        Money.of(-100.00, "USD") to Money.of(100.00, "USD"),

        // Rounding cases
        Pair(
            Money.of(100.00, "USD", RoundingMode.UP),
            Money.of(-100.00, "USD", RoundingMode.UP)
        ),
        Pair(
            Money.of(100.00, "USD", Rounding.to(4, RoundingMode.DOWN)),
            Money.of(-100.00, "USD", Rounding.to(4, RoundingMode.DOWN))
        ),
    )

    val plus = listOf(
        Triple(zero, one, one),
        Triple(one, zero, one),

        Triple(one, one, Money.of(2.00, "USD")),
        Triple(Money.of(2.00, "USD"), minusOne, one),
        Triple(Money.of(-2.00, "USD"), one, minusOne),

        // Rounding cases
        // 1.0065 + 1.0099 = 2.0164

        // Default rounding
        Triple(
            Money.of(1.0065, "USD"),
            Money.of(1.0099, "USD"),
            Money.of(2.02, "USD"),
        ),

        // Mixed rounding (receiver rounding is used)
        Triple(
            Money.of(1.0065, "USD", RoundingMode.DOWN),
            Money.of(1.0099, "USD", RoundingMode.UP),
            Money.of(2.01, "USD", RoundingMode.DOWN),
        ),

        // Mixed precision and rounding (receiver rounding is used)
        Triple(
            Money.of(1.0065, "USD", Rounding.to(4, RoundingMode.DOWN)),
            Money.of(1.0099, "USD", Rounding.to(6, RoundingMode.UP)),
            Money.of(2.0164, "USD", Rounding.to(4, RoundingMode.DOWN)),
        ),

        // No rounding
        Triple(
            Money.of(1.0065, "USD", Rounding.no()),
            Money.of(1.00991, "USD", Rounding.no()),
            Money.of(2.01641, "USD", Rounding.no()),
        ),
    )

    val minus = listOf(
        Triple(zero, one, minusOne),
        Triple(one, zero, one),
        Triple(one, one, zero),

        Triple(Money.of(2.00, "USD"), minusOne, Money.of(3.00, "USD")),
        Triple(Money.of(-2.00, "USD"), one, Money.of(-3.00, "USD")),

        // Rounding cases
        // 1.0065 - 1.0099 = 0.0034

        // Default rounding
        Triple(
            Money.of(1.0065, "USD"),
            Money.of(1.0099, "USD"),
            zero
        ),

        // Mixed rounding (receiver rounding is used)
        Triple(
            Money.of(1.0065, "USD", RoundingMode.UP),
            Money.of(1.0099, "USD", RoundingMode.DOWN),
            Money.of(-0.01, "USD", RoundingMode.UP),
        ),

        // Mixed precision and rounding (receiver rounding is used)
        Triple(
            Money.of(1.0065, "USD", Rounding.to(4, RoundingMode.UP)),
            Money.of(1.0099, "USD", Rounding.to(6, RoundingMode.DOWN)),
            Money.of(-0.0034, "USD", Rounding.to(4, RoundingMode.UP)),
        ),

        // No rounding
        Triple(
            Money.of(1.0065, "USD", Rounding.no()),
            Money.of(1.00991, "USD", Rounding.no()),
            Money.of(-0.00341, "USD", Rounding.no()),
        ),
    )

    val times = listOf(
        Triple(zero, 1.0, zero),
        Triple(one, 0.0, zero),
        Triple(one, 1.0, one),

        Triple(Money.of(2.00, "USD"), -1.0, Money.of(-2.00, "USD")),
        Triple(Money.of(-2.00, "USD"), 1.0, Money.of(-2.00, "USD")),

        // Rounding cases
        Triple(
            Money.of(33.3333, "USD"),
            3.0,
            Money.of(100.00, "USD"),
        ),
        Triple(
            Money.of(33.3333, "USD", RoundingMode.DOWN),
            3.0,
            Money.of(99.99, "USD", RoundingMode.DOWN),
        ),
        Triple(
            Money.of(33.3333, "USD", Rounding.to(4, RoundingMode.UP)),
            3.0,
            Money.of(99.9999, "USD", Rounding.to(4, RoundingMode.UP)),
        ),

        Triple(
            Money.of(100.00, "USD"),
            1.0 / 3,
            Money.of(33.33, "USD"),
        ),
        Triple(
            Money.of(100.00, "USD", RoundingMode.DOWN),
            1.0 / 3,
            Money.of(33.33, "USD", RoundingMode.DOWN),
        ),
        Triple(
            Money.of(100.00, "USD", Rounding.to(4, RoundingMode.UP)),
            1.0 / 3,
            Money.of(33.3334, "USD", Rounding.to(4, RoundingMode.UP)),
        ),

        // No rounding
        Triple(
            Money.of(100.00, "USD", Rounding.no()),
            1.0 / 3,
            Money.of(33.33333333333333, "USD", Rounding.no()),
        ),
    )

    val division = listOf(
        Triple(zero, 1.0, zero),
        // Triple(one, 0.0, +inf),
        Triple(one, 1.0, one),

        Triple(Money.of(2.00, "USD"), -1.0, Money.of(-2.00, "USD")),
        Triple(Money.of(-2.00, "USD"), 1.0, Money.of(-2.00, "USD")),

        // Rounding cases
        Triple(
            Money.of(33.3333, "USD"),
            3.0,
            Money.of(11.11, "USD"),
        ),
        Triple(
            Money.of(33.3333, "USD", RoundingMode.DOWN),
            3.0,
            Money.of(11.11, "USD", RoundingMode.DOWN),
        ),
        Triple(
            Money.of(33.3333, "USD", Rounding.to(4, RoundingMode.UP)),
            3.0,
            Money.of(11.1111, "USD", Rounding.to(4, RoundingMode.UP)),
        ),

        Triple(
            Money.of(100.00, "USD"),
            3.0,
            Money.of(33.33, "USD"),
        ),
        Triple(
            Money.of(100.00, "USD", RoundingMode.DOWN),
            3.0,
            Money.of(33.33, "USD", RoundingMode.DOWN),
        ),
        Triple(
            Money.of(100.00, "USD", Rounding.to(4, RoundingMode.UP)),
            3.0,
            Money.of(33.3334, "USD", Rounding.to(4, RoundingMode.UP)),
        ),

        // No rounding
        Triple(
            Money.of(100.00, "USD", Rounding.no()),
            3.0,
            Money.of(
                BigDecimal(
                    "33.333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333" +
                        "33333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333" +
                        "333333333333333333333333333333333333333333333333333333333333333333333"
                ),
                "USD",
                Rounding.no()
            ),
        ),
    )

    val ratio = listOf(
        Triple(zero, one, 0.0),
        // Triple(one, zero, +inf),
        Triple(one, one, 1.0),

        Triple(Money.of(2.00, "USD"), Money.of(-2.00, "USD"), -1.0),
        Triple(Money.of(-2.00, "USD"), Money.of(-2.00, "USD"), 1.0),

        // Rounding cases
        Triple(
            Money.of(33.3333, "USD"),
            Money.of(11.11, "USD"),
            3.00029702970297
        ),
        Triple(
            Money.of(33.3333, "USD", RoundingMode.DOWN),
            Money.of(11.11, "USD", RoundingMode.DOWN),
            3.00029702970297
        ),
        Triple(
            Money.of(33.3333, "USD", Rounding.to(4, RoundingMode.UP)),
            Money.of(11.1111, "USD", Rounding.to(4, RoundingMode.UP)),
            3.0
        ),

        Triple(
            Money.of(100.00, "USD"),
            Money.of(33.33, "USD"),
            3.0003000300030003
        ),
        Triple(
            Money.of(100.00, "USD", RoundingMode.DOWN),
            Money.of(33.33, "USD", RoundingMode.DOWN),
            3.0003000300030003
        ),
        Triple(
            Money.of(100.00, "USD", Rounding.to(4, RoundingMode.UP)),
            Money.of(33.3334, "USD", Rounding.to(4, RoundingMode.UP)),
            2.999994000012
        ),

        // No rounding
        Triple(
            Money.of(100.00, "USD", Rounding.no()),
            Money.of(
                BigDecimal(
                    "33.333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333" +
                        "33333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333" +
                        "333333333333333333333333333333333333333333333333333333333333333333333"
                ),
                "USD",
                Rounding.no()
            ),
            3.0,
        ),
    )

    val exchange = listOf(
        ExchangeCase(
            Money.of(1.00, "USD"),
            Money.of(0.00, "EUR"),
            Money.of(0.00, "EUR"),
        ),
        ExchangeCase(
            Money.of(1.00, "USD"),
            Money.of(1.00, "EUR"),
            Money.of(1.00, "EUR"),
        ),
        ExchangeCase(
            Money.of(10.00, "USD"),
            Money.of(0.91607166, "EUR"),
            Money.of(9.16, "EUR"),
        ),
        ExchangeCase(
            Money.of(9.16, "EUR"),
            Money.of(1.0916177, "USD"),
            Money.of(10.00, "USD"),
        ),

        // Use a CustomCurrency to preserve the decimal places.
        ExchangeCase(
            Money.of(10.00, "USD"),
            Money.of(0.91607166, Currency.of("EUR").toCustomCurrency(4)),
            Money.of(9.1607, Currency.of("EUR").toCustomCurrency(4)),
        ),
        ExchangeCase(
            Money.of(10.00, "USD"),
            Money.of(0.91607166, Currency.of("EUR").toCustomCurrency(8)),
            Money.of(9.16071660, Currency.of("EUR").toCustomCurrency(8)),
        ),

        // Use a Rounding strategy to preserve the decimal places.
        ExchangeCase(
            Money.of(10.00, "USD"),
            Money.of(0.91607166, Currency.of("EUR"), Rounding.to(4)),
            Money.of(9.1607, Currency.of("EUR"), Rounding.to(4)),
        ),
        ExchangeCase(
            Money.of(10.00, "USD"),
            Money.of(0.91607166, Currency.of("EUR"), Rounding.to(8)),
            Money.of(9.16071660, Currency.of("EUR"), Rounding.to(8)),
        ),
        ExchangeCase(
            Money.of(10.00, "USD"),
            Money.of(0.91607166, Currency.of("EUR"), Rounding.no()),
            Money.of(9.16071660, Currency.of("EUR"), Rounding.no()),
        ),

        // Use a Rounding strategy with a specific mode to change how the result is rounded.
        ExchangeCase(
            Money.of(10.00, "USD"),
            Money.of(0.91607166, Currency.of("EUR"), Rounding.to(4, RoundingMode.UP)),
            Money.of(9.1608, Currency.of("EUR"), Rounding.to(4, RoundingMode.UP)),
        ),
    )

    val timesPercentage = listOf(
        Triple(
            Money.of(100.00, "USD"),
            Percentage.of(25),
            Money.of(25.00, "USD")
        ),

        // The Money rounding is always used for the operation. The Percentage parameters are ignored.
        Triple(
            Money.of(1.1234, "USD"),
            Percentage.of(100, Rounding.to(4)),
            Money.of(1.12, "USD")
        ),
        Triple(
            Money.of(1.1234, "USD", RoundingMode.DOWN),
            Percentage.of(100, Rounding.to(4, RoundingMode.UP)),
            Money.of(1.12, "USD", RoundingMode.DOWN)
        ),
        Triple(
            Money.of(1.1234, "USD", RoundingMode.UP),
            Percentage.of(100, Rounding.to(4, RoundingMode.DOWN)),
            Money.of(1.13, "USD", RoundingMode.UP)
        ),

        Triple(
            Money.of(1.1234, "USD"),
            Percentage.of(50, Rounding.to(4)),
            Money.of(0.56, "USD")
        ),
        Triple(
            Money.of(1.1234, "USD", RoundingMode.DOWN),
            Percentage.of(50, Rounding.to(4, RoundingMode.UP)),
            Money.of(0.56, "USD", RoundingMode.DOWN)
        ),
        Triple(
            Money.of(1.1234, "USD", RoundingMode.UP),
            Percentage.of(50, Rounding.to(4, RoundingMode.DOWN)),
            Money.of(0.57, "USD", RoundingMode.UP)
        ),
    )

    val increaseByPercentage = listOf(
        // Positive amount and positive percentage.
        IncreaseByPercentageCase(
            1.1234,
            "USD",
            Percentage.of(100),
            2.24,
            2.26,
            2.26,
            2.24,
            2.24,
            2.24,
            2.24,
        ),
        IncreaseByPercentageCase(
            1.1234,
            "USD",
            Percentage.of(50),
            1.68,
            1.70,
            1.70,
            1.68,
            1.68,
            1.68,
            1.68,
        ),

        // Positive amount and negative percentage.
        IncreaseByPercentageCase(
            1.1234,
            "USD",
            Percentage.of(-100),
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
        ),
        IncreaseByPercentageCase(
            1.1234,
            "USD",
            Percentage.of(-50),
            0.56,
            0.57,
            0.56,
            0.56,
            0.56,
            0.56,
            0.55,
        ),

        // Negative amount and positive percentage.
        IncreaseByPercentageCase(
            -1.1234,
            "USD",
            Percentage.of(100),
            -2.24,
            -2.24,
            -2.26,
            -2.24,
            -2.24,
            -2.24,
            -2.26,
        ),
        IncreaseByPercentageCase(
            -1.1234,
            "USD",
            Percentage.of(50),
            -1.68,
            -1.68,
            -1.70,
            -1.68,
            -1.68,
            -1.68,
            -1.70,
        ),

        // Negative amount and negative percentage.
        IncreaseByPercentageCase(
            -1.1234,
            "USD",
            Percentage.of(-100),
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
        ),
        IncreaseByPercentageCase(
            -1.1234,
            "USD",
            Percentage.of(-50),
            -0.56,
            -0.55,
            -0.56,
            -0.56,
            -0.56,
            -0.56,
            -0.57,
        ),

        // Special cases

        // Zero percentage
        IncreaseByPercentageCase(
            100.00,
            "USD",
            Percentage.of(0),
            100.00,
            100.00,
            100.00,
            100.00,
            100.00,
            100.00,
            100.00,
        ),

        // Percentage's rounding strategy is ignored.
        IncreaseByPercentageCase(
            1.1234,
            "USD",
            Percentage.of(33, Rounding.no()),
            1.49,
            1.51,
            1.51,
            1.49,
            1.49,
            1.49,
            1.49,
        ),
        IncreaseByPercentageCase(
            1.1234,
            "USD",
            Percentage.of(33, Rounding.to(4, RoundingMode.CEILING)),
            1.49,
            1.51,
            1.51,
            1.49,
            1.49,
            1.49,
            1.49,
        ),
    )

    val decreaseByPercentage = listOf(
        // Positive amount and positive percentage.
        DecreaseByPercentageCase(
            1.1234,
            "USD",
            Percentage.of(100),
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
        ),
        DecreaseByPercentageCase(
            1.1234,
            "USD",
            Percentage.of(50),
            0.56,
            0.56,
            0.56,
            0.56,
            0.56,
            0.56,
            0.56,
        ),

        // Positive amount and negative percentage.
        DecreaseByPercentageCase(
            1.1234,
            "USD",
            Percentage.of(-100),
            2.24,
            2.25,
            2.26,
            2.24,
            2.24,
            2.24,
            2.25,
        ),
        DecreaseByPercentageCase(
            1.1234,
            "USD",
            Percentage.of(-50),
            1.68,
            1.69,
            1.70,
            1.68,
            1.68,
            1.68,
            1.69,
        ),

        // Negative amount and positive percentage.
        DecreaseByPercentageCase(
            -1.1234,
            "USD",
            Percentage.of(100),
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
            0.00,
        ),
        DecreaseByPercentageCase(
            -1.1234,
            "USD",
            Percentage.of(50),
            -0.56,
            -0.56,
            -0.56,
            -0.56,
            -0.56,
            -0.56,
            -0.56,
        ),

        // Negative amount and negative percentage.
        DecreaseByPercentageCase(
            -1.1234,
            "USD",
            Percentage.of(-100),
            -2.24,
            -2.25,
            -2.26,
            -2.24,
            -2.24,
            -2.24,
            -2.25,
        ),
        DecreaseByPercentageCase(
            -1.1234,
            "USD",
            Percentage.of(-50),
            -1.68,
            -1.69,
            -1.70,
            -1.68,
            -1.68,
            -1.68,
            -1.69,
        ),

        // Special cases

        // Zero percentage
        DecreaseByPercentageCase(
            100.00,
            "USD",
            Percentage.of(0),
            100.00,
            100.00,
            100.00,
            100.00,
            100.00,
            100.00,
            100.00,
        ),

        // Percentage's rounding strategy is ignored.
        DecreaseByPercentageCase(
            1.1234,
            "USD",
            Percentage.of(33, Rounding.no()),
            0.75,
            0.75,
            0.75,
            0.75,
            0.75,
            0.75,
            0.75,
        ),
        DecreaseByPercentageCase(
            1.1234,
            "USD",
            Percentage.of(33, Rounding.to(4, RoundingMode.CEILING)),
            0.75,
            0.75,
            0.75,
            0.75,
            0.75,
            0.75,
            0.75,
        ),
    )

    val rounding = listOf(
        Money.of(1.1234, "USD") to
            Money.of(1.12, "USD"),

        Money.of(1.1234, "USD", RoundingMode.UP) to
            Money.of(1.13, "USD", RoundingMode.UP),

        Money.of(1.1234, "USD", Rounding.to(4, RoundingMode.DOWN)) to
            Money.of(1.1234, "USD", Rounding.to(4, RoundingMode.DOWN)),

        Money.of(3.4567, "USD") to
            Money.of(3.46, "USD"),

        Money.of(3.4567, "USD", RoundingMode.DOWN) to
            Money.of(3.45, "USD", RoundingMode.DOWN),

        Money.of(3.4567, "USD", Rounding.to(4, RoundingMode.UP)) to
            Money.of(3.4567, "USD", Rounding.to(4, RoundingMode.UP)),

        Money.of(5.6789, "JPY") to
            Money.of(6, "JPY"),

        Money.of(5.6789, "JPY", RoundingMode.DOWN) to
            Money.of(5, "JPY", RoundingMode.DOWN),

        Money.of(5.6789, "JPY", Rounding.to(4, RoundingMode.UP)) to
            Money.of(5.6789, "JPY", Rounding.to(4, RoundingMode.UP)),
    )

    val disabledRounding = listOf(
        DisabledRoundingCase(
            Money.of(3.4567, "USD", Rounding.no()),
            Money.of(3.4567, "USD", Rounding.no()),
            Money.of(3.4567, "USD", Rounding.no()),
            Money.of(3.46, "USD"),
        ),

        DisabledRoundingCase(
            Money.of(5.6789, "JPY", Rounding.no()),
            Money.of(5.6789, "JPY", Rounding.no()),
            Money.of(5.6789, "JPY", Rounding.no()),
            Money.of(6, "JPY"),
        )
    )

    val withRoundingMode = listOf(
        Triple(
            Money.of(1.1234, "USD"),
            RoundingMode.CEILING,
            Money.of(1.1234, "USD", RoundingMode.CEILING),
        ),
        Triple(
            Money.of(1.1234, "USD"),
            RoundingMode.UP,
            Money.of(1.1234, "USD", RoundingMode.UP),
        ),
        Triple(
            Money.of(1.1234, "USD"),
            RoundingMode.HALF_UP,
            Money.of(1.1234, "USD", RoundingMode.HALF_UP),
        ),
        Triple(
            Money.of(1.1234, "USD"),
            RoundingMode.HALF_DOWN,
            Money.of(1.1234, "USD", RoundingMode.HALF_DOWN),
        ),

        // With custom rounding strategy.
        Triple(
            Money.of(1.1234, "USD", Rounding.to(8)),
            RoundingMode.HALF_DOWN,
            Money.of(1.1234, "USD", Rounding.to(8, RoundingMode.HALF_DOWN)),
        ),

        // No rounding.
        Triple(
            Money.of(1.1234, "USD", Rounding.no()),
            RoundingMode.HALF_DOWN,
            Money.of(1.1234, "USD", RoundingMode.HALF_DOWN),
        ),
    )

    val withRounding = listOf(
        Triple(
            Money.of(1.1234, "USD"),
            Rounding.no(),
            Money.of(1.1234, "USD", Rounding.no()),
        ),

        Triple(
            Money.of(1.1234, "USD", Rounding.no()),
            Rounding.to(2, RoundingMode.HALF_UP),
            Money.of(1.1234, "USD", Rounding.to(2, RoundingMode.HALF_UP)),
        ),

        // Same rounding as the current one.
        Triple(
            Money.of(1.1234, "USD", Rounding.to(4, RoundingMode.HALF_UP)),
            Rounding.to(4, RoundingMode.HALF_UP),
            Money.of(1.1234, "USD", Rounding.to(4, RoundingMode.HALF_UP)),
        ),
    )

    val collections = listOf(
        listOf(Money.of(1.524, "USD")) to
            Money.of(1.52, "USD"),

        listOf(Money.of(1.524, "USD"), Money.of(1.529, "USD", RoundingMode.UP)) to
            Money.of(3.05, "USD"),

        listOf(Money.of(1.524, "USD", RoundingMode.UP), Money.of(1.529, "USD", RoundingMode.DOWN)) to
            Money.of(3.06, "USD", RoundingMode.UP),

        listOf(Money.of(1.524, "USD", Rounding.no()), Money.of(1.529, "USD", RoundingMode.UP)) to
            Money.of(3.053, "USD", Rounding.no()),

        listOf(Money.of(1.524, "USD", Rounding.no()), Money.of(1.529, "USD", RoundingMode.DOWN)) to
            Money.of(3.053, "USD", Rounding.no())
    )
}
