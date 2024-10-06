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

import com.eriksencosta.money.gradle.CurrencyMap

/**
 * Models a source of currencies data. To add a new source, override the [currencies] and [prioritizedCurrencies]
 * properties.
 */
internal abstract class SourceCurrencies {
    internal interface RawCurrency {
        val code: String
        val secondaryCode: String
        val name: String
        val symbol: String
        val type: String
        val minorUnits: Int
    }

    /**
     * Map of currencies where the key is the currency code and the value holds the currency data.
     */
    abstract val currencies: CurrencyMap

    /**
     * Set of currency codes that will be searched in a prioritized way. This allows for optimization, such as faster
     * lookups for the most traded currencies.
     */
    abstract val prioritizedCurrenciesCodes: Set<String>

    val codes: Set<String> by lazy {
        currencies
            .map { it.key }
            .toSortedSet()
    }

    val codesUniqueChars: Set<String> by lazy {
        codes
            .map { it.split("") }
            .flatten()
            .filterNot { it.isBlank() }
            .toSortedSet()
    }

    val secondaryCodes: Set<String> by lazy {
        secondaryCodesCounts
            .map { it.key }
            .toSortedSet()
    }

    val uniqueSecondaryCodes: Set<String> by lazy {
        secondaryCodesCounts
            .filter { it.value == 1 }
            .map { it.key }
            .toSortedSet()
    }

    val secondaryCodesUniqueChars: Set<String> by lazy {
        secondaryCodes
            .map { it.split("") }
            .flatten()
            .filterNot { it.isBlank() }
            .toSortedSet()
    }

    val secondaryCodesMinMaxLengths: Pair<Int, Int> by lazy {
        secondaryCodes
            .filterNot { it.isBlank() }
            .sortedBy { it.length }
            .run { first().length to last().length }
    }

    val prioritizedCurrencies: CurrencyMap by lazy {
        currencies.filterKeys { prioritizedCurrenciesCodes.contains(it) }
    }

    val fallbackCurrencies: CurrencyMap by lazy {
        currencies.filterKeys { !prioritizedCurrenciesCodes.contains(it) }
    }

    val prioritizedCurrenciesBySecondaryCode: CurrencyMap by lazy {
        prioritizedCurrencies
            .filter { it.value.secondaryCode.isNotBlank() }
            .map { it.value.secondaryCode to it.value }
            .toMap()
            .toSortedMap()
    }

    val fallbackCurrenciesBySecondaryCode: CurrencyMap by lazy {
        fallbackCurrencies
            .filter { it.value.secondaryCode.isNotBlank() }
            .filter { uniqueSecondaryCodes.contains(it.value.secondaryCode) }
            .map { it.value.secondaryCode to it.value }
            .toMap()
            .toSortedMap()
    }

    private val secondaryCodesCounts: Map<String, Int> by lazy {
        currencies
            .toList()
            .groupingBy { it.second.secondaryCode }
            .eachCount()
            .toSortedMap()
    }
}
