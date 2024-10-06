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

package com.eriksencosta.money.gradle.currency

import com.ibm.icu.text.CurrencyMetaInfo
import com.ibm.icu.text.CurrencyMetaInfo.CurrencyFilter.now
import com.ibm.icu.text.CurrencyMetaInfo.CurrencyFilter.onTender
import com.ibm.icu.util.Currency
import com.ibm.icu.util.ULocale
import java.util.Locale

internal object ICUCurrencies : SourceCurrencies() {
    internal data class ICUCurrency(
        override val code: String,
        override val secondaryCode: String,
        override val name: String,
        override val symbol: String,
        override val type: String,
        override val minorUnits: Int
    ) : RawCurrency

    private val locale = Locale.ENGLISH
    private val metainfo = CurrencyMetaInfo.getInstance()
    private val active = metainfo.currencies(now()).toSortedSet()
    private val tender = metainfo.currencies(onTender()).toSortedSet()

    // Prefer the symbols in a given locale for a currency code.
    private val preferentialLocales = mapOf(
        "CAD" to "fr_CA",
        "CNY" to "zh_CN_#Hans",
        "GNF" to "nqo_GN",
        "PKR" to "pa_PK_#Arab",
    )

    // Override a symbol for a currency code, no matter the available symbols in the ICU's dataset.
    private val replacements = mapOf(
        // There is no specific encoding in UTF-8 for cifrão ($ with two strokes). It is implemented in the typefaces.
        // Override the symbol available in the ICU dataset as it is returning a non-printable symbol.
        // See: https://en.wikipedia.org/wiki/Dollar_sign#Encoding
        "CVE" to "$",

        // There is no official locale for Euro.
        "EUR" to "€",
    )

    private val countriesLocales = ULocale.getAvailableLocales()
        .filter { it.country.isNotBlank() }
        .map { it.toLocale() }
        .sortedBy { it.country }

    // Includes the 20 most traded currencies plus the currencies from the top 20 economies (by GDP) tallying 24
    // currencies in total.
    //
    // See: https://en.wikipedia.org/wiki/Template:Most_traded_currencies
    // See: https://en.wikipedia.org/wiki/List_of_countries_by_GDP_(nominal)
    override val prioritizedCurrenciesCodes = setOf(
        // 20 most traded currencies
        "USD", // United States
        "EUR", // European Union (including Germany, France, Italy, Spain, and Netherlands from top 20 economies by GDP)
        "JPY", // Japan
        "GBP", // UK
        "CNY", // China
        "AUD", // Australia
        "CAD", // Canada
        "CHF", // Switzerland
        "HKD", // Hong Kong
        "SGD", // Singapore
        "SEK", // Sweden
        "KRW", // South Korea
        "NOK", // Norway
        "NZD", // New Zealand
        "INR", // India
        "MXN", // Mexico
        "TWD", // Taiwan
        "ZAR", // South Africa
        "BRL", // Brazil
        "DKK", // Denmark

        // Currencies from the top 20 economies by GDP
        "RUB", // Russia
        "IDR", // Indonesia
        "TRY", // Turkey
        "SAR", // Saudi Arabia
    ).toSortedSet()

    @Suppress("MagicNumber")
    override val currencies: Map<String, ICUCurrency> by lazy {
        Currency.getAvailableCurrencies().sortedBy { it.currencyCode }.associate { currency ->
            val currencyCode = currency.currencyCode
            val numericCode = currency.numericCode.toString().padStart(3, '0')

            val isActive = active.any { it == currencyCode }
            val isTender = tender.any { it == currencyCode }
            val isOther = 'X' == currencyCode[0]

            val type = when {
                isTender && isActive -> "TENDER"
                isOther && isActive -> "OTHER"
                else -> "HISTORICAL"
            }

            val countryCode = currency.currencyCode.subSequence(0, 2)

            val symbol = countriesLocales.asSequence()
                .sortedBy { it.toString() }
                .filter { "TENDER" == type }
                .filter { it.country == countryCode }
                .filter { currency.getSymbol(it).isNotBlank() }
                .filter { currency.getSymbol(it) != currencyCode }
                // Exclude latininized symbols.
                .filter { !Regex(".*Latn$").matches(it.toString()) }
                // Include only a preferential locale if it's the case.
                .filter {
                    if (preferentialLocales.contains(currencyCode)) preferentialLocales[currencyCode] == it.toString()
                    else true
                }
                .map { currency.getSymbol(it) }
                .toList()
                .getOrElse(0) { currencyCode }

            currencyCode to ICUCurrency(
                currencyCode,
                numericCode,
                currency.getDisplayName(locale),
                replacements.getOrDefault(currencyCode, symbol),
                type,
                currency.defaultFractionDigits
            )
        }
    }
}
