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
import com.eriksencosta.money.MoneyFixtures.accessors
import com.eriksencosta.money.MoneyFixtures.collections
import com.eriksencosta.money.MoneyFixtures.decreaseByPercentage
import com.eriksencosta.money.MoneyFixtures.disabledRounding
import com.eriksencosta.money.MoneyFixtures.division
import com.eriksencosta.money.MoneyFixtures.exchange
import com.eriksencosta.money.MoneyFixtures.increaseByPercentage
import com.eriksencosta.money.MoneyFixtures.minus
import com.eriksencosta.money.MoneyFixtures.plus
import com.eriksencosta.money.MoneyFixtures.ratio
import com.eriksencosta.money.MoneyFixtures.rounding
import com.eriksencosta.money.MoneyFixtures.roundingSupport
import com.eriksencosta.money.MoneyFixtures.times
import com.eriksencosta.money.MoneyFixtures.timesPercentage
import com.eriksencosta.money.MoneyFixtures.unaryMinus
import com.eriksencosta.money.MoneyFixtures.unaryPlus
import com.eriksencosta.money.MoneyFixtures.withRounding
import com.eriksencosta.money.MoneyFixtures.withRoundingMode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.RoundingMode.DOWN
import java.math.RoundingMode.HALF_UP
import java.math.RoundingMode.UP
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class MoneyTest {
    @Test
    fun `Create a Money for a zero amount`() {
        assertEquals(
            Money.of(0.00, "USD"),
            Money.of(100.00, "USD").zero()
        )

        assertEquals(
            Money.of(0.00, "USD", UP),
            Money.of(100.00, "USD", UP).zero()
        )

        assertEquals(
            Money.of(0.00, "USD", Rounding.to(4, DOWN)),
            Money.of(100.00, "USD", Rounding.to(4, DOWN)).zero()
        )
    }

    @Test
    fun `Create the smallest unit of a Money`() {
        assertEquals(
            Money.of(0.01, "USD"),
            Money.of(100.00, "USD").smallestUnit()
        )

        assertEquals(
            Money.of(0.01, "USD", UP),
            Money.of(100.00, "USD", UP).smallestUnit()
        )

        assertEquals(
            Money.of(0.01, "USD", Rounding.to(4, DOWN)),
            Money.of(100.00, "USD", Rounding.to(4, DOWN)).smallestUnit()
        )

        assertEquals(
            Money.of(1, "JPY"),
            Money.of(1000, "JPY").smallestUnit(),
        )

        // Tunisian Dinar (Tunisia)
        assertEquals(
            Money.of(0.001, "TND"),
            Money.of(2000.000, "TND").smallestUnit(),
        )

        // Unidad de Fomento (Chile)
        assertEquals(
            Money.of(0.0001, "CLF"),
            Money.of(3000.0000, "CLF").smallestUnit(),
        )

        // Ethereum
        assertEquals(
            Money.of(0.000000000000000001, "ETH"),
            Money.of(4000.000000000000000000, "ETH").smallestUnit(),
        )

        // Custom currency
        assertEquals(
            Money.of(0.0001, Currency.custom("USD", 4)),
            Money.of(4000.00, Currency.custom("USD", 4)).smallestUnit()
        )

        // The smallest unit is always positive as the only valid result for a negative one would be negative infinite.
        assertEquals(
            Money.of(0.01, "USD"),
            Money.of(-5000.00, "USD").smallestUnit()
        )
    }

    @TestFactory
    fun `Return true when Money is zero`() = accessors
        .map {
            dynamicTest("given ${it.money} then I should get ${it.isZero}") {
                assertEquals(it.isZero, it.money.isZero)
            }
        }

    @TestFactory
    fun `Return true when Money is not zero`() = accessors
        .map {
            dynamicTest("given ${it.money} then I should get ${it.isNotZero}") {
                assertEquals(it.isNotZero, it.money.isNotZero)
            }
        }

    @TestFactory
    fun `Return true when Money is positive`() = accessors
        .map {
            dynamicTest("given ${it.money} then I should get ${it.isPositive}") {
                assertEquals(it.isPositive, it.money.isPositive)
            }
        }

    @TestFactory
    fun `Return true when Money is positive or zero`() = accessors
        .map {
            dynamicTest("given ${it.money} then I should get ${it.isPositiveOrZero}") {
                assertEquals(it.isPositiveOrZero, it.money.isPositiveOrZero)
            }
        }

    @TestFactory
    fun `Return true when Money is negative`() = accessors
        .map {
            dynamicTest("given ${it.money} then I should get ${it.isNegative}") {
                assertEquals(it.isNegative, it.money.isNegative)
            }
        }

    @TestFactory
    fun `Return true when Money is negative or zero`() = accessors
        .map {
            dynamicTest("given ${it.money} then I should get ${it.isNegativeOrZero}") {
                assertEquals(it.isNegativeOrZero, it.money.isNegativeOrZero)
            }
        }

    @TestFactory
    fun `Return true when Money has rounding support`() = roundingSupport
        .map { (money, expected) ->
            val with = if (expected) "with rounding support" else "without rounding support"

            dynamicTest("given $money $with then I should get $expected") {
                assertEquals(expected, money.hasRounding)
            }
        }

    @TestFactory
    fun `Cast the Money to its positive value`() = unaryPlus
        .map { (money, expected) ->
            dynamicTest("given $money when I cast it to positive then I should get $expected") {
                assertEquals(expected, +money)
            }
        }

    @TestFactory
    fun `Cast the Money to its negative value`() = unaryMinus
        .map { (money, expected) ->
            dynamicTest("given $money when I cast it to negative then I should get $expected") {
                assertEquals(expected, -money)
            }
        }

    @TestFactory
    fun `Sum a Money to another Money`() = plus
        .map { (money, other, expected) ->
            dynamicTest("given $money when I sum to $other then I should get $expected") {
                assertEquals(expected, money + other)
            }
        }

    @Test
    fun `Throw exception when summing two different currencies`() =
        assertThrows<IllegalArgumentException> { Money.of(1, "USD") + Money.of(1000, "JPY") }.run {
            val expected = "Currencies mismatch: USD 1.00 + JPY 1000. The operation must be done using values with " +
                "the same currency"

            assertEquals(expected, message)
        }

    @TestFactory
    fun `Subtract a Money from another Money`() = minus
        .map { (money, other, expected) ->
            dynamicTest("given $money when I subtract $other then I should get $expected") {
                assertEquals(expected, money - other)
            }
        }

    @Test
    fun `Throw exception when subtracting two different currencies`() =
        assertThrows<IllegalArgumentException> { Money.of(1, "USD") - Money.of(1000, "JPY") }.run {
            val expected = "Currencies mismatch: USD 1.00 - JPY 1000. The operation must be done using values with " +
                "the same currency"

            assertEquals(expected, message)
        }

    @TestFactory
    fun `Multiply a Money by a factor`() = times
        .map { (money, by, expected) ->
            dynamicTest("given $money when I multiply by $by then I should get $expected") {
                assertEquals(expected, money * by)
                assertEquals(expected, by * money, "Commutative check")
            }
        }

    @TestFactory
    fun `Divide a Money by a divisor`() = division
        .map { (money, by, expected) ->
            dynamicTest("given $money when I divide by $by then I should get $expected") {
                assertEquals(expected, money / by)
            }
        }

    @TestFactory
    fun `Calculate the ratio of two Money`() = ratio
        .map { (money, other, expected) ->
            dynamicTest("given $money when I calculate the ratio with $other then I should get $expected") {
                assertEquals(expected, money ratio other)
            }
        }

    @Test
    fun `Throw exception when calculating the ratio between two different currencies`() =
        assertThrows<IllegalArgumentException> { Money.of(1, "USD") ratio Money.of(1000, "JPY") }.run {
            val expected = "Currencies mismatch: USD 1.00 ratio JPY 1000. The operation must be done using values " +
                "with the same currency"

            assertEquals(expected, message)
        }

    @TestFactory
    fun `Exchange a Money`() = exchange
        .map { (money, rate, expected) ->
            dynamicTest("given $money when I exchange with $rate then I should get $expected") {
                assertEquals(expected, money exchange rate)
            }
        }

    @Test
    fun `Throw exception when exchanging a Money with a negative rate amount`() =
        assertThrows<IllegalArgumentException> {
            Money.of(1.00, "USD") exchange Money.of(-1, "JPY")
        }.run {
            assertEquals("The exchange rate must be positive", message)
        }

    @TestFactory
    fun `Multiply a Money by a Percentage`() = timesPercentage
        .map { (money, percentage, expected) ->
            dynamicTest("given $money when I multiply by $percentage then I should get $expected") {
                assertEquals(expected, money * percentage)
                assertEquals(expected, percentage * money, "Commutative check")
            }
        }

    @TestFactory
    fun `Increase a Money by a Percentage`() = increaseByPercentage
        .map {
            it.expectations().map { (money, percentage, expected) ->
                val rounding = money.rounding

                dynamicTest("given $money with $rounding when I increase by $percentage then I should get $expected") {
                    assertEquals(expected, money increaseBy percentage)
                    assertEquals(expected, percentage increase money, "Commutative check")
                }
            }
        }.flatten()

    @TestFactory
    fun `Decrease a Money by a Percentage`() = decreaseByPercentage
        .map {
            it.expectations().map { (money, percentage, expected) ->
                val rounding = money.rounding

                dynamicTest("given $money with $rounding when I decrease by $percentage then I should get $expected") {
                    assertEquals(expected, money decreaseBy percentage)
                    assertEquals(expected, percentage decrease money, "Commutative check")
                }
            }
        }.flatten()

    @TestFactory
    fun `Round a Money`() = rounding
        .map { (money, expected) ->
            dynamicTest("given $money when I round it then I should get $expected") {
                assertEquals(expected, money.round())
                assertEquals(expected, money.round(money.rounding), "Overloaded Rounding check")
                assertEquals(expected, money.round(money.rounding.mode), "Overloaded RoundingMode check")
            }
        }

    @TestFactory
    fun `Round a Money with rounding disabled`() = disabledRounding
        .map { (money, expected, expectedRounding, expectedRoundingMode) ->
            dynamicTest("given $money when I round it then I should get $expected") {
                assertEquals(expected, money.round())
                assertEquals(expectedRounding, money.round(money.rounding), "Overloaded Rounding check")

                // round(RoundingMode) works differently as it returns a Money object with rounding support, even if the
                // original instance didn't support rounding.
                assertEquals(expectedRoundingMode, money.round(money.rounding.mode), "Overloaded RoundingMode check")
            }
        }

    @TestFactory
    fun `Change the Money rounding mode`() = withRoundingMode
        .map { (money, mode, expected) ->
            dynamicTest("given $money and $mode when I apply it then I should get $expected") {
                assertEquals(expected, money with mode)
            }
        }

    @TestFactory
    fun `Change the Money rounding strategy`() = withRounding
        .map { (money, rounding, expected) ->
            dynamicTest("given $money and $rounding when I apply it then I should get $expected") {
                assertEquals(expected, money with rounding)
            }
        }

    @TestFactory
    fun `Sum a Money collection`() = collections
        .map { (collection, expected) ->
            dynamicTest("given $collection when I sum its elements then I should get $expected") {
                assertEquals(
                    expected,
                    (collection as Iterable<Money>).sum(),
                    "The items are summed with no rounding applied to prevent the accumulation of rounding errors"
                )

                assertEquals(
                    expected,
                    collection.sum(),
                    "The items are summed with no rounding applied to prevent the accumulation of rounding errors"
                )
            }
        }

    @Test
    fun `Throw exception when summing an empty collection`() =
        assertThrows<UnsupportedOperationException> { emptyList<Money>().sum() }.run {
            assertEquals("Empty collection can not be summed", message)
        }

    @Test
    fun `Order a collection of Money`() {
        val money = listOf(
            Money.of(BigDecimal("1"), Currency.custom("USD", 4), Rounding.to(4)),
            Money.of(1.00, "USD"),
            Money.of(1.00, "EUR"),
            Money.of(1, "JPY"),
            Money.of(0.00, "USD"),
            Money.of(1.00, "BRL"),
            Money.of(-1.00, "USD"),
        )

        val expected = listOf(
            Money.of(1.00, "BRL"),
            Money.of(1.00, "EUR"),
            Money.of(1, "JPY"),
            Money.of(-1.00, "USD"),
            Money.of(0.00, "USD"),
            Money.of(1.00, "USD"),
            Money.of(BigDecimal("1"), Currency.custom("USD", 4), Rounding.to(4)),
        )

        assertEquals(expected, money.sorted())
    }

    @Test
    fun `Check for equality`() {
        val money1 = Money.of(1.00, "USD")
        val money2 = Money.of(1.00, "USD")
        val money3 = Money.of(1.00, "USD", UP)
        val money4 = Money.of(1.00, Currency.custom("USD", 4), Rounding.no())
        val money5 = Money.of(1.00, Currency.of("USD").toCustomCurrency(4), Rounding.to(2, HALF_UP))
        val money6 = Money.of(1.00, Currency.of("USD").toCustomCurrency(4), Rounding.to(4, HALF_UP))
        val money7 = Money.of(1.00, "EUR")
        val money8 = Money.of(1000, "JPY")

        assertEquals(money1, money1, "Same instance")
        assertEquals(money1, money2, "Same values")
        assertNotEquals(money1, money3, "Different rounding mode")
        assertNotEquals(money1, money4, "Different rounding strategy")
        assertNotEquals(money1, money5, "Different currency")
        assertNotEquals(money1, money6, "Different rounding precision")
        assertNotEquals(money5, money6, "Different rounding precision")
        assertNotEquals(money1, money7, "Different currencies")
        assertNotEquals(money7, money8, "Different currencies")

        @Suppress("EqualsNullCall")
        assertFalse(money1.equals(null), "Different instances types: null")
        assertFalse(money1.equals("USD 1.00"), "Different instances types: String")
    }

    @TestFactory
    fun `Calculate the hash code`() = listOf(
        Money.of(1, "USD") to 2_615_098,
        Money.of(1, "EUR") to 2_140_798,
    )
        .map { (money, expected) ->
            dynamicTest("given $money then the hash code should be $expected") {
                assertEquals(expected, money.hashCode())
            }
        }

    @TestFactory
    fun `Convert Money to string`() = listOf(
        Money.of(1, "USD") to "USD 1.00",
        Money.of(1.55, "USD") to "USD 1.55",
        Money.of(1.5555, "USD") to "USD 1.5555",
        Money.of(1.5555, "USD", Rounding.to(2, UP)) to "USD 1.5555",
        Money.of(BigDecimal("1.5555"), Currency.custom("USD", 2), Rounding.no()) to "USD 1.5555",

        Money.of(1, "JPY") to "JPY 1",
        Money.of(1.55, "JPY") to "JPY 1.55",
        Money.of(1.5555, "JPY") to "JPY 1.5555",
        Money.of(1.5555, "JPY", Rounding.to(2, UP)) to "JPY 1.5555",
        Money.of(BigDecimal("1.5555"), Currency.custom("JPY", 2), Rounding.no()) to "JPY 1.5555",
    )
        .map { (money, expected) ->
            dynamicTest("given $money when I convert it to a string then I should get $expected") {
                assertEquals(expected, locale { money.toString() })
            }
        }

    @TestFactory
    fun `Convert Money to a detailed string`() = listOf(
        Money.of(1, "USD") to
            "Money[amount=1.00 currency=[CirculatingCurrency[code=USD secondaryCode=840 name=US Dollar symbol=$ " +
            "type=TENDER minorUnits=2]] rounding=[PreciseRounding[2 HALF_EVEN]]",
        Money.of(1.55, "USD") to
            "Money[amount=1.55 currency=[CirculatingCurrency[code=USD secondaryCode=840 name=US Dollar symbol=$ " +
            "type=TENDER minorUnits=2]] rounding=[PreciseRounding[2 HALF_EVEN]]",
        Money.of(1.5555, "USD") to
            "Money[amount=1.5555 currency=[CirculatingCurrency[code=USD secondaryCode=840 name=US Dollar symbol=$ " +
            "type=TENDER minorUnits=2]] rounding=[PreciseRounding[2 HALF_EVEN]]",
        Money.of(1.5555, "USD", Rounding.to(2, UP)) to
            "Money[amount=1.5555 currency=[CirculatingCurrency[code=USD secondaryCode=840 name=US Dollar symbol=$ " +
            "type=TENDER minorUnits=2]] rounding=[PreciseRounding[2 UP]]",
        Money.of(BigDecimal("1.5555"), Currency.custom("USD", 2), Rounding.no()) to
            "Money[amount=1.5555 currency=[CustomCurrency[code=USD secondaryCode= name= symbol=USD " +
            "type=CUSTOM minorUnits=2]] rounding=[NoRounding]",

        Money.of(1, "JPY") to
            "Money[amount=1 currency=[CirculatingCurrency[code=JPY secondaryCode=392 name=Japanese Yen " +
            "symbol=￥ type=TENDER minorUnits=0]] rounding=[PreciseRounding[0 HALF_EVEN]]",
        Money.of(1.55, "JPY") to
            "Money[amount=1.55 currency=[CirculatingCurrency[code=JPY secondaryCode=392 name=Japanese Yen " +
            "symbol=￥ type=TENDER minorUnits=0]] rounding=[PreciseRounding[0 HALF_EVEN]]",
        Money.of(1.5555, "JPY") to
            "Money[amount=1.5555 currency=[CirculatingCurrency[code=JPY secondaryCode=392 name=Japanese Yen " +
            "symbol=￥ type=TENDER minorUnits=0]] rounding=[PreciseRounding[0 HALF_EVEN]]",
        Money.of(1.5555, "JPY", Rounding.to(2, UP)) to
            "Money[amount=1.5555 currency=[CirculatingCurrency[code=JPY secondaryCode=392 name=Japanese Yen " +
            "symbol=￥ type=TENDER minorUnits=0]] rounding=[PreciseRounding[2 UP]]",
        Money.of(BigDecimal("1.5555"), Currency.custom("JPY", 2), Rounding.no()) to
            "Money[amount=1.5555 currency=[CustomCurrency[code=JPY secondaryCode= name= symbol=JPY " +
            "type=CUSTOM minorUnits=2]] rounding=[NoRounding]",
    )
        .map { (money, expected) ->
            dynamicTest("given $money when I convert it to a detailed string then I should get $expected") {
                assertEquals(expected, locale { money.toDetailedString() })
            }
        }
}
