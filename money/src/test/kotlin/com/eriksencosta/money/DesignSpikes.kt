package com.eriksencosta.money

import com.eriksencosta.money.CirculatingCurrencyCode.*
import com.eriksencosta.money.CryptoCurrencyCode.BTC
import com.eriksencosta.money.allocation.EvenParts
import com.eriksencosta.money.allocation.Ratios
import com.eriksencosta.money.allocation.allocate
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import kotlin.test.Test
import kotlin.test.assertEquals

class DesignSpikes {
    @Test
    fun constructors() {
        // Using Enum's function
        val fiftyUsdCents = USD money 0.50
        val fiftyBrlCents = BRL money 0.50
        val fiftyTryCents = TRY money 0.50
        val halfABitcoin = BTC money 0.50

        // Using Number extension function
        val fiftyDollars = 50 money USD
        val fiftyReais = 50 money BRL
        val fiftyLiras = 50 money TRY
        val fiftyCoins = 50 money BTC

        assertEquals(Money.of(0.50, "USD"), fiftyUsdCents)
        assertEquals(Money.of(0.50, "BRL"), fiftyBrlCents)
        assertEquals(Money.of(0.50, "TRY"), fiftyTryCents)
        assertEquals(Money.of(0.50, "BTC"), halfABitcoin)
        assertEquals(Money.of(50, "USD"), fiftyDollars)
        assertEquals(Money.of(50, "BRL"), fiftyReais)
        assertEquals(Money.of(50, "TRY"), fiftyLiras)
        assertEquals(Money.of(50, "BTC"), fiftyCoins)
    }

    @TestFactory
    fun percentage() = listOf(
        Triple(
            50 money USD,
            EvenParts(3),
            listOf(
                Money.of(16.67, "USD"),
                Money.of(16.67, "USD"),
                Money.of(16.66, "USD"),
            )
        ),
        Triple(
            71.50 money BRL,
            Ratios(25.percent, 10.percent, 5.percent, 60.percent),
            // BRL 17.88, BRL 7.15, BRL 3.57, BRL 42.90
            listOf(
                Money.of(17.88, "BRL"),
                Money.of(7.15, "BRL"),
                Money.of(3.57, "BRL"),
                Money.of(42.90, "BRL"),
            )
        ),
    ).map { (money, by, expected) ->
        DynamicTest.dynamicTest("given $money when allocated in $by then I should get $expected") {
            val allocations = (money allocate by).allocations()
            assertEquals(expected, allocations)
            assertEquals(money, allocations.sum())
        }
    }
}