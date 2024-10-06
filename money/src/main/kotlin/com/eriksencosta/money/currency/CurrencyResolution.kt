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

package com.eriksencosta.money.currency

import com.eriksencosta.money.CirculatingCurrency
import com.eriksencosta.money.CryptoCurrency
import com.eriksencosta.money.StandardizedCurrency
import com.eriksencosta.money.currency.circulating.CirculatingCurrencyBundle
import com.eriksencosta.money.currency.crypto.CryptoCurrencyBundle

/**
 * A Chain of Responsibility capable of looking up currencies in the library dataset.
 */
internal sealed class CurrencyResolution {
    protected abstract val next: CurrencyResolution?
    protected abstract val supportedCodePattern: Regex

    protected abstract fun lookup(code: String): CurrencyData
    protected abstract fun transform(data: CurrencyData): StandardizedCurrency

    /**
     * Lookup for a currency in the dataset by a given code.
     *
     * @param[code] The code or secondary code for a currency.
     * @return A [StandardizedCurrency].
     * @throws[IllegalArgumentException] When no currency is found for the given [code].
     */
    fun resolve(code: String): StandardizedCurrency = run {
        val resolved = when (supports(code)) {
            true -> lookup(code)
            false -> UndefinedCurrencyData()
        }

        resolved.transform({ transform(it) }) {
            next?.resolve(code) ?: throw IllegalArgumentException(
                "The currency code $code does not represent a standardized currency. If you're trying to create a " +
                    "custom currency, create a CustomCurrency object through Currency.custom() factory method"
            )
        }
    }

    /**
     * Identify a currency code in a string.
     *
     * @param[value] String where to lookup for a currency code.
     * @return A possible currency code or an empty string if no code is found.
     */
    fun identify(value: String): String = extractCurrencyCode(value).let {
        when (it.isNotBlank()) {
            true -> it
            false -> next?.identify(value).orEmpty()
        }
    }

    private fun supports(code: String) = code.isNotEmpty() && supportedCodePattern.matches(code)

    private fun extractCurrencyCode(value: String) = value.split(" ").let { parts ->
        when (parts.size) {
            1 -> ""
            else -> {
                parts
                    .filter { it.isValidCode() && supports(it) }
                    .sortedBy { it.codeRank() }
                    .takeCurrencyCode()
            }
        }
    }

    private fun String.isValidCode(): Boolean = !(
        numericValuesPattern.replace(this, "").isEmpty() ||
            !validStartersPattern.containsMatchIn(this) ||
            (length == 1 && !validSingleCharCodePattern.matches(this))
        )

    private fun String.codeRank(): Int = if (uppercaseLetterPattern.containsMatchIn(this)) 0 else 1

    private fun List<String>.takeCurrencyCode(): String = when (size) {
        0 -> ""
        else -> get(0)
    }

    internal companion object Factory {
        // Period, comma, middle dot, and apostrophe (or single quote) are commonly used as decimal or grouping
        // separators with Western Arabic numerals.
        // See: https://en.wikipedia.org/wiki/Decimal_separator
        private val numericValuesPattern = Regex("[0-9.,·'’]+")

        // $ is used only in CryptoCurrency's secondary code. This could be moved to the Chain of Responsibility flow
        // (i.e., to the supports() method), but it's enough for now.
        private val validStartersPattern = Regex("^[0-9A-Za-z$]")
        private val validSingleCharCodePattern = Regex("^[A-Za-z]")
        private val uppercaseLetterPattern = Regex("[A-Z]")

        /**
         * A [CurrencyResolution] capable of looking up data by primary and secondary code for circulating currencies.
         */
        val circulating: CirculatingCurrencyResolution by lazy {
            CirculatingCurrencyByPrimaryCode(CirculatingCurrencyBySecondaryCode(null))
        }

        /**
         * A [CurrencyResolution] capable of looking up data by primary and secondary code for cryptocurrencies.
         */
        val crypto: CryptoCurrencyResolution by lazy {
            CryptoCurrencyByPrimaryCode(CryptoCurrencyBySecondaryCode(null))
        }

        /**
         * A [CurrencyResolution] capable of looking up data by primary and secondary code for circulating currencies
         * and cryptocurrencies.
         */
        val thorough: CurrencyResolution by lazy {
            CirculatingCurrencyByPrimaryCode(
                CryptoCurrencyByPrimaryCode(
                    CirculatingCurrencyBySecondaryCode(
                        CryptoCurrencyBySecondaryCode(null)
                    )
                )
            )
        }
    }
}

internal sealed class CirculatingCurrencyResolution : CurrencyResolution() {
    override fun transform(data: CurrencyData): StandardizedCurrency = CirculatingCurrency(
        data.code,
        data.secondaryCode,
        data.name,
        data.symbol,
        CirculatingCurrency.Type.valueOf(data.type),
        data.minorUnits
    )
}

internal sealed class CryptoCurrencyResolution : CurrencyResolution() {
    override fun transform(data: CurrencyData): StandardizedCurrency = CryptoCurrency(
        data.code,
        data.secondaryCode,
        data.name,
        data.symbol,
        CryptoCurrency.Type.valueOf(data.type),
        data.minorUnits
    )
}

private class CirculatingCurrencyByPrimaryCode(override val next: CurrencyResolution?) :
    CirculatingCurrencyResolution() {
    override val supportedCodePattern = CirculatingCurrencyBundle.patternForCode()
    override fun lookup(code: String): CurrencyData = CirculatingCurrencyBundle.ofCode(code)
}

private class CirculatingCurrencyBySecondaryCode(override val next: CurrencyResolution?) :
    CirculatingCurrencyResolution() {
    override val supportedCodePattern = CirculatingCurrencyBundle.patternForSecondaryCode()
    override fun lookup(code: String): CurrencyData = CirculatingCurrencyBundle.ofSecondaryCode(code)
}

private class CryptoCurrencyByPrimaryCode(override val next: CurrencyResolution?) :
    CryptoCurrencyResolution() {
    override val supportedCodePattern = CryptoCurrencyBundle.patternForCode()
    override fun lookup(code: String): CurrencyData = CryptoCurrencyBundle.ofCode(code)
}

private class CryptoCurrencyBySecondaryCode(override val next: CurrencyResolution?) :
    CryptoCurrencyResolution() {
    override val supportedCodePattern = CryptoCurrencyBundle.patternForSecondaryCode()
    override fun lookup(code: String): CurrencyData = CryptoCurrencyBundle.ofSecondaryCode(code)
}
