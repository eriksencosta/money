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

import com.eriksencosta.math.percentage.percent
import com.eriksencosta.money.allocation.EvenAllocator
import com.eriksencosta.money.allocation.allocate
import com.eriksencosta.money.allocation.difference.Discard
import com.eriksencosta.money.allocation.difference.Distribute
import com.eriksencosta.money.allocation.difference.DistributeBySign
import com.eriksencosta.money.allocation.difference.IndexBasedAllocation
import com.eriksencosta.money.allocation.difference.NegativeOnGreatest
import com.eriksencosta.money.allocation.difference.OnFirst
import com.eriksencosta.money.allocation.difference.OnFirstSmallest
import com.eriksencosta.money.allocation.difference.OnLastGreatest
import com.eriksencosta.money.allocation.difference.OnLastNonZero
import com.eriksencosta.money.caching.configureCache
import com.eriksencosta.money.caching.disableCache
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UsageExamples {
    @Test
    fun `At glance code example`() {
        run { // Overview
            val price = 100 money "USD"
            val shipping = 5 money "USD"
            val subtotal = price + shipping
            val discount = 10.percent()
            val total = subtotal decreaseBy discount

            val ratios = listOf(60.percent(), 40.percent())

            val evenInstallments = (total allocate 2).allocations()
            val proportionalInstallments = (total allocate ratios).allocations()

            assertEquals(listOf(47.25 money "USD", 47.25 money "USD"), evenInstallments)
            assertEquals(listOf(56.70 money "USD", 37.80 money "USD"), proportionalInstallments)
            assertEquals(total, evenInstallments.sum())
            assertEquals(total, proportionalInstallments.sum())
        }

        run { // Cryptocurrency support
            val price = 0.01607580 money "BTC"
            val transactionFee = 1.25.percent()
            val total = price increaseBy transactionFee
            val installments = total allocate 3

            val rate = 62_555.60 money "USD"
            val totalInUsd = price exchange rate

            assertEquals(
                listOf(0.00542559 money "BTC", 0.00542558 money "BTC", 0.00542558 money "BTC"),
                installments.allocations()
            )
            assertEquals(total, installments.allocations().sum())
            assertEquals(1005.63 money "USD", totalInUsd)
        }
    }

    @Test
    fun `Creating instances of Money code example`() {
        run { // Using Money.of()
            val oneDollar = Money.of(1, "USD")
            val oneEuro = Money.of(1, "EUR")

            assertEquals(BigDecimal(1), oneDollar.amount)
            assertEquals("USD", oneDollar.currency.code)
            assertEquals(BigDecimal(1), oneEuro.amount)
            assertEquals("EUR", oneEuro.currency.code)
        }

        run { // Using Number.money()
            val dollars = 1.99 money "USD"
            val euros = 11.01 money "EUR"

            assertEquals(Money.of(1.99, "USD"), dollars)
            assertEquals(Money.of(11.01, "EUR"), euros)
        }

        run { // Using String.money()
            val us = Locale.US
            val italy = Locale.ITALY

            val value1 = "USD 1,234.56" money us
            val value2 = "USD 1.234,56" money us
            val value3 = "USD 1,234.56" money italy
            val value4 = "USD 1.234,56" money italy

            assertEquals(Money.of(1234.56, "USD"), value1)
            assertEquals(Money.of(1.234, "USD"), value2)
            assertEquals(Money.of(1.234, "USD"), value3)
            assertEquals(Money.of(1234.56, "USD"), value4)
        }

        run { // Using Currency.money
            val usd = Currency of "USD"
            val eur = Currency of "EUR"
            val jpy = Currency of "JPY"

            val dollars = usd money 1.99
            val euros = eur money 2.99
            val yen = jpy money 3999

            assertEquals(Money.of(1.99, "USD"), dollars)
            assertEquals(Money.of(2.99, "EUR"), euros)
            assertEquals(Money.of(3999, "JPY"), yen)
        }
    }

    @Test
    fun `Currencies code example`() {
        run { // Introduction
            val usd = Currency.of("USD")
            val eur = Currency.of("EUR")
            val bre = Currency.of("BRE")
            val xau = Currency.of("XAU")
            val btc = Currency.of("BTC")
            val eth = Currency.of("ETH")

            assertEquals("US Dollar", usd.name)
            assertEquals("Euro", eur.name)
            assertEquals("Brazilian Cruzeiro (1990–1993)", bre.name)
            assertEquals("Gold", xau.name)
            assertEquals("Bitcoin", btc.name)
            assertEquals("Ethereum Ether", eth.name)
        }

        run { // Circulating currencies
            val usd = Currency.of("USD")
            val eur = Currency.of("978")

            assertEquals("US Dollar", usd.name)
            assertEquals("Euro", eur.name)
        }

        run { // Cryptocurrencies
            val polkadot = Currency.of("P5B46MFPP")
            val dogecoin = Currency.of("DOGE")

            assertEquals("Polkadot DOT", polkadot.name)
            assertEquals("Dogecoin", dogecoin.name)
        }
    }

    @Test
    fun `Querying currency data code example`() {
        val usd = Currency of "USD"

        assertEquals("USD", usd.code)
        assertEquals("840", usd.secondaryCode)
        assertEquals("US Dollar", usd.name)
        assertEquals("$", usd.symbol)
        assertEquals(CirculatingCurrency.Type.TENDER, usd.type)
        assertEquals(2, usd.minorUnits)
    }

    @Test
    fun `Minor units and rounding code example`() {
        run { // USD rounding
            val price = 6.6512 money "USD"
            val items = 2
            val total = price * items

            assertEquals(13.30 money "USD", total)
        }

        run { // JPY rounding
            val price = 6.6512 money "JPY"
            val items = 2
            val total = price * items

            assertEquals(13 money "JPY", total)
        }

        run { // Gas price rounding in the USA
            val price = 3.557.money("USD", RoundingMode.HALF_UP)
            val gallons = 5
            val total = price * gallons

            assertEquals(17.79.money("USD", RoundingMode.HALF_UP), total)
        }

        run { // Gas price rounding in Brazil
            val price = 5.968.money("BRL", RoundingMode.DOWN)
            val liters = 19
            val total = price * liters

            assertEquals(113.39.money("BRL", RoundingMode.DOWN), total)
        }
    }

    @Test
    fun `Custom currencies code example`() {
        run { // Introduction
            val custom = Currency.custom("CUSTOM", 4)
            val price = 1.2345 money custom
            val items = 4
            val total = items * price

            assertEquals(4.9380 money custom, total)
        }

        run { // Customizing a CustomCurrency
            val custom = Currency.custom("CUSTOM", 4) {
                secondaryCode = "CSTC"
                name = "CustomCoin"
                symbol = "C$"
                type = CryptoCurrency.Type.AUXILIARY
            }

            assertEquals("CSTC", custom.secondaryCode)
            assertEquals("CustomCoin", custom.name)
            assertEquals("C$", custom.symbol)
            assertEquals(CryptoCurrency.Type.AUXILIARY, custom.type)
        }

        run { // Modeling exchange rates
            val usd = Currency.of("USD")
            val standardBrl = Currency.of("BRL")
            val exchangeBrl = Currency.of("BRL").toCustomCurrency(4)

            val amount = 1 money usd
            val standardRate = 5.49 money standardBrl
            val exchangeRate = 5.4905 money exchangeBrl

            assertEquals(5.49 money standardBrl, amount exchange standardRate)
            assertEquals(5.4905 money exchangeBrl, amount exchange exchangeRate)
        }
    }

    @Test
    fun `Addition code example`() {
        val value = 1 money "USD"
        val other = 2 money "USD"
        val result = value + other

        assertEquals(3 money "USD", result)
    }

    @Test
    fun `Subtraction code example`() {
        val value = 1 money "USD"
        val other = 2 money "USD"
        val result = value - other

        assertEquals(-1 money "USD", result)
    }

    @Test
    fun `Multiplication code example`() {
        run { // By a Number
            val value = 1.23 money "USD"
            val result = value * 3.14

            assertEquals(3.86 money "USD", result)
        }

        run { // By a Percentage
            val value = 150 money "USD"
            val percentage = 10.percent()
            val result = value * percentage

            assertEquals(15 money "USD", result)
        }
    }

    @Test
    fun `Division code example`() {
        val value = 1.23 money "USD"
        val result = value / 3.14

        assertEquals(0.39 money "USD", result)
    }

    @Test
    fun `Negation code example`() {
        val value = 1 money "USD"
        val other = -value

        assertEquals(true, value.isPositive)
        assertEquals(true, other.isNegative)
    }

    @Test
    fun `Comparison code example`() {
        val value = 1 money "USD"
        val other = 2 money "USD"
        val greaterThan = value > other
        val lowerThan = value < other

        assertEquals(false, greaterThan)
        assertEquals(true, lowerThan)
    }

    @Test
    fun `Ratio code example`() {
        val value = 1.23 money "USD"
        val other = 2.46 money "USD"
        val result = value ratio other

        assertEquals(0.5, result)
    }

    @Test
    fun `Increase by a percentage code example`() {
        val dueAmount = 1250 money "USD"
        val lateFine = 5.percent()
        val overdue = dueAmount increaseBy lateFine

        assertEquals(1312.50 money "USD", overdue)
    }

    @Test
    fun `Decrease by a percentage code example`() {
        val subtotal = 2355.98 money "USD"
        val discount = 10.percent()
        val total = subtotal decreaseBy discount

        assertEquals(2120.38 money "USD", total)
    }

    @Test
    fun `Currency exchange code example`() {
        val amount = 1 money "USD"
        val rate = 5.4905 money "BRL"
        val exchange = amount exchange rate

        assertEquals(5.49 money "BRL", exchange)
    }

    @Test
    fun `Zero amount creation code example`() {
        val usd = Currency of "USD"

        assertEquals(0 money usd, usd.zero())
    }

    @Test
    fun `Smallest unit creation code example`() {
        val usd = Currency of "USD"

        assertEquals(0.01 money usd, usd.smallestUnit())
    }

    @Test
    fun `Transforming a list of numbers code example`() {
        val usd = Currency of "USD"
        val result = usd money listOf(1, 2, 3)
        val expected = listOf(1 money usd, 2 money usd, 3 money usd)

        assertEquals(expected, result)
    }

    @Test
    fun `Rounding code example`() {
        run { // Introduction
            val price = 6.6512 money "USD"
            val items = 2
            val total = price * items

            assertEquals(13.30 money "USD", total)
        }

        run { // Minor units and rounding
            val price = 6.6512 money "JPY"
            val items = 2
            val total = price * items

            assertEquals(13 money "JPY", total)
        }

        run { // Using a specific RoundingMode
            val price = 6.6512.money("USD", RoundingMode.CEILING)
            val items = 2
            val total = price * items

            assertEquals(13.31.money("USD", RoundingMode.CEILING), total)
        }

        run { // RoundingMode preservation
            val subtotal = 123.4321.money("USD", RoundingMode.CEILING)
            val transactionFee = 0.99.money("USD", RoundingMode.FLOOR)
            val total = subtotal + transactionFee

            assertEquals(124.43.money("USD", RoundingMode.CEILING), total)
        }

        run { // Changing the RoundingMode
            val grossIncome = 8.43 money "USD"
            val taxRate = 12.5.percent()
            val capitalGains = grossIncome.with(RoundingMode.UP) * taxRate

            assertEquals(1.06.money("USD", RoundingMode.UP), capitalGains)
        }
    }

    @Test
    fun `Disabling rounding code example`() {
        run { // Introduction
            val price = 6.6512 money "USD"
            val items = 2
            val total = price.noRounding() * items

            assertEquals(13.3024.money("USD").noRounding(), total)
        }

        run { // Disabling rounding
            val amount = 70 money "USD"
            val exchangeRate = (5.4905 money "BRL").noRounding()
            val converted = amount exchange exchangeRate
            val exchangeFeeRate = 2.15.percent()
            val exchangeFee = converted.with(RoundingMode.UP) * exchangeFeeRate
            val result = converted - exchangeFee
            val actualResult = result.round(RoundingMode.DOWN)

            assertEquals((384.335 money "BRL").noRounding(), converted)
            assertEquals(8.27.money("BRL", RoundingMode.UP), exchangeFee)
            assertEquals((376.065 money "BRL").noRounding(), result)
            assertEquals(376.06.money("BRL", RoundingMode.DOWN), actualResult)
        }

        run { // Previous example with rounding enabled
            val amount = 70 money "USD"
            val exchangeRate = 5.4905 money "BRL"
            val converted = amount exchange exchangeRate
            val exchangeFeeRate = 2.15.percent()
            val exchangeFee = converted.with(RoundingMode.UP) * exchangeFeeRate
            val result = converted - exchangeFee
            val actualResult = result.round(RoundingMode.DOWN)

            assertEquals(384.34 money "BRL", converted)
            assertEquals(8.27.money("BRL", RoundingMode.UP), exchangeFee)
            assertEquals(376.07 money "BRL", result)
            assertEquals(376.07.money("BRL", RoundingMode.DOWN), actualResult)
        }
    }

    @Test
    fun `Allocations code example`() {
        run { // Introduction
            val price = 100 money "USD"
            val number = 3
            val installment = price / number
            val installments = List(number) { installment }
            val total = installments.sum()

            assertEquals(List(3) { 33.33 money "USD" }, installments)
            assertEquals(99.99 money "USD", total)
        }

        run { // Overcharging due to rounding issue
            val price = 9973 money "USD"
            val number = 6
            val installment = price / number
            val installments = List(number) { installment }
            val total = installments.sum()

            assertEquals(List(6) { 1662.17 money "USD" }, installments)
            assertEquals(9973.02 money "USD", total)
        }

        run { // Using the allocate() method
            val price = 100 money "USD"
            val number = 3
            val installments = price allocate number
            val total = installments.allocations().sum()

            assertEquals(listOf(33.34 money "USD", 33.33 money "USD", 33.33 money "USD"), installments.allocations())
            assertEquals(100 money "USD", total)
        }

        run { // Overcharging fixed by using the allocate() method
            val price = 9973 money "USD"
            val number = 6
            val installments = price allocate number
            val total = installments.allocations().sum()

            assertEquals(9973 money "USD", total)
        }
    }

    @Test
    fun `Even and proportional allocation code example`() {
        run { // Even allocation
            val price = 100 money "USD"
            val result = price allocate 3
            val allocations = result.allocations()
            val expected = listOf(33.34 money "USD", 33.33 money "USD", 33.33 money "USD")

            assertEquals(expected, allocations)
            assertEquals(100 money "USD", allocations.sum())
        }

        run { // Proportional allocation
            val amount = 2345.89 money "USD"
            val result = amount allocate listOf(50.percent(), 30.percent(), 20.percent())
            val allocations = result.allocations()
            val expected = listOf(1172.94 money "USD", 703.77 money "USD", 469.18 money "USD")

            assertEquals(expected, allocations)
            assertEquals(2345.89 money "USD", allocations.sum())
        }
    }

    @Test
    fun `Inspecting the result code example`() {
        val price = 100 money "USD"
        val result = price allocate 3
        val details = result.details()

        assertEquals(List(3) { 33.33 money "USD" }, details.calculations)
        assertEquals(listOf(0.01 money "USD", 0.00 money "USD", 0.00 money "USD"), details.adjustments)
        assertEquals(listOf(33.34 money "USD", 33.33 money "USD", 33.33 money "USD"), details.allocations)

        assertEquals(99.99 money "USD", details.calculationsTotals)
        assertEquals(0.01 money "USD", details.adjustmentsTotals)
        assertEquals(100 money "USD", details.allocationsTotals)
    }

    @Test
    fun `Configuring the difference allocation code example`() {
        run { // Even allocation default behavior
            val price = 100 money "USD"
            val result = price allocate 7
            val details = result.details()

            assertEquals(List(7) { 14.29 money "USD" }, details.calculations)
            assertEquals(List(6) { 0 money "USD" } + listOf(-0.03 money "USD"), details.adjustments)
            assertEquals(List(6) { 14.29 money "USD" } + listOf(14.26 money "USD"), details.allocations)
        }

        run { // Using a custom EvenAllocator
            val price = 100 money "USD"
            val allocator = EvenAllocator(OnFirst)
            val result = allocator.allocate(price, 7)
            val details = result.details()

            assertEquals(List(7) { 14.29 money "USD" }, details.calculations)
            assertEquals(listOf(-0.03 money "USD") + List(6) { 0 money "USD" }, details.adjustments)
            assertEquals(listOf(14.26 money "USD") + List(6) { 14.29 money "USD" }, details.allocations)
        }

        run { // Using a custom EvenAllocator to distribute the difference
            val price = 100 money "USD"
            val allocator = EvenAllocator(
                DistributeBySign(
                    OnFirstSmallest,
                    OnLastGreatest
                )
            )
            val result = allocator.allocate(price, 7)
            val details = result.details()

            assertEquals(List(7) { 14.29 money "USD" }, details.calculations)
            assertEquals(
                List(4) { 0 money "USD" } + List(3) { -0.01 money "USD" },
                details.adjustments
            )
            assertEquals(
                List(4) { 14.29 money "USD" } + List(3) { 14.28 money "USD" },
                details.allocations
            )
        }
    }

    @Test
    fun `Difference allocation strategies code example`() {
        val randomAllocator = object : IndexBasedAllocation {
            override fun allocate(difference: Money, allocations: List<Money>): List<Money> = run {
                val index = Random.nextInt(0, allocations.size - 1)
                applyDifferenceAtIndex(difference, index, allocations)
            }
        }

        val price = 100 money "USD"
        val allocator = EvenAllocator(randomAllocator)
        val result = allocator.allocate(price, 7)
        val details = result.details()

        assertEquals(100.03 money "USD", details.calculationsTotals)
        assertEquals(-0.03 money "USD", details.adjustmentsTotals)
        assertEquals(100 money "USD", details.allocationsTotals)
    }

    @Test
    fun `Index based allocation code example`() {
        val price = 100 money "USD"
        val allocator = EvenAllocator(OnFirst)
        val result = allocator.allocate(price, 7)
        val details = result.details()

        assertEquals(List(7) { 14.29 money "USD" }, details.calculations)
        assertEquals(listOf(-0.03 money "USD") + List(6) { 0 money "USD" }, details.adjustments)
        assertEquals(listOf(14.26 money "USD") + List(6) { 14.29 money "USD" }, details.allocations)
    }

    @Test
    fun `Biased allocation code example`() {
        val price = 100 money "USD"
        val allocator = EvenAllocator(OnLastNonZero)
        val result = allocator.allocate(price, 7)
        val details = result.details()

        assertEquals(List(7) { 14.29 money "USD" }, details.calculations)
        assertEquals(List(6) { 0 money "USD" } + listOf(-0.03 money "USD"), details.adjustments)
        assertEquals(List(6) { 14.29 money "USD" } + listOf(14.26 money "USD"), details.allocations)
    }

    @Test
    fun `Directional allocation code example`() {
        val price = 100 money "USD"
        val allocator = EvenAllocator(NegativeOnGreatest)
        val result = allocator.allocate(price, 7)
        val details = result.details()

        assertEquals(List(7) { 14.29 money "USD" }, details.calculations)
        assertEquals(listOf(-0.03 money "USD") + List(6) { 0 money "USD" }, details.adjustments)
        assertEquals(listOf(14.26 money "USD") + List(6) { 14.29 money "USD" }, details.allocations)
    }

    @Test
    fun `Distributable allocation code example`() {
        val price = 100 money "USD"
        val allocator = EvenAllocator(Distribute(OnLastGreatest))
        val result = allocator.allocate(price, 7)
        val details = result.details()

        assertEquals(List(7) { 14.29 money "USD" }, details.calculations)
        assertEquals(
            List(4) { 0 money "USD" } + List(3) { -0.01 money "USD" },
            details.adjustments
        )
        assertEquals(
            List(4) { 14.29 money "USD" } + List(3) { 14.28 money "USD" },
            details.allocations
        )
    }

    @Test
    fun `Disabling the difference allocation code example`() {
        val price = 100 money "USD"
        val allocator = EvenAllocator(Discard)
        val result = allocator.allocate(price, 7)
        val details = result.details()

        assertEquals(List(7) { 14.29 money "USD" }, details.calculations)
        assertEquals(List(7) { 0 money "USD" }, details.adjustments)
        assertEquals(List(7) { 14.29 money "USD" }, details.allocations)
    }

    @Test
    fun `Performance code example`() {
        run { // Configuring the cache
            // resetCache() is called for testing purposes. The method is part of the internal API and can't be called
            // by client code.
            Currency.resetCache()

            configureCache {
                maximumItems = 100
                expirationTime = 2
                expirationTimeUnit = TimeUnit.HOURS
            }

            assertTrue { Currency of "USD" === Currency of "USD" }
        }

        run { // Disabling the cache
            // resetCache() is called for testing purposes. The method is part of the internal API and can't be called
            // by client code.
            Currency.resetCache()

            disableCache()

            assertFalse { Currency of "USD" === Currency of "USD" }
        }
    }
}
