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
import com.eriksencosta.money.gradle.GroupedCurrencies
import com.eriksencosta.money.gradle.KotlinFileSpec
import com.eriksencosta.money.gradle.SecondaryLookup
import com.eriksencosta.money.gradle.currency.SourceCurrencies.RawCurrency
import com.eriksencosta.money.gradle.dump
import com.eriksencosta.money.gradle.escape
import com.eriksencosta.money.gradle.escapeForRegex
import com.eriksencosta.money.gradle.joinNewline
import com.eriksencosta.money.gradle.optimizeForRegex
import kotlin.math.ceil

@Suppress("StringLiteralDuplication")
internal abstract class CurrenciesDataClassesGenerator {
    protected abstract fun gradleTask(): String
    protected abstract fun currencyGroup(): String
    protected abstract fun currenciesPerFile(): Int
    protected abstract fun currenciesSource(): SourceCurrencies
    protected abstract fun fileNotice(): String

    private val dataClassTemplate = """
        |@@FILE_NOTICE@@
        |
        |package @@MAIN_PACKAGE@@@@SUB_PACKAGE@@
        |
        |import com.eriksencosta.money.currency.CurrencyData
        |
        |// @@NUMBER_OF_CURRENCIES@@ currencies
        |@Suppress("MagicNumber", "MaxLineLength", "StringLiteralDuplication")
        |internal class @@CLASS_NAME@@ {
        |    val currencies: Map<String, CurrencyData> get() = mapOf(
        |@@MAP_ENTRIES@@
        |    )
        |}
        |
    """.trimMargin()

    private val lookupClassTemplate = """
        |@@FILE_NOTICE@@
        |
        |package @@MAIN_PACKAGE@@@@SUB_PACKAGE@@
        |
        |// @@NUMBER_OF_CURRENCIES@@ currencies
        |@Suppress("StringLiteralDuplication")
        |internal class @@CLASS_NAME@@ {
        |    val currencies: Map<String, String> get() = mapOf(
        |@@MAP_ENTRIES@@
        |    )
        |}
        |
    """.trimMargin()

    private val rootDataClassTemplate = """
        |@@FILE_NOTICE@@
        |
        |package @@MAIN_PACKAGE@@@@SUB_PACKAGE@@
        |
        |import com.eriksencosta.money.caching.Cache
        |import com.eriksencosta.money.currency.CurrencyData
        |import com.eriksencosta.money.currency.UndefinedCurrencyData
        |import com.eriksencosta.money.currency.createSizedCache
        |import com.eriksencosta.money.currency.findByCode
        |
        |@Suppress("CyclomaticComplexMethod")
        |internal object @@CLASS_NAME@@ {
        |    private const val NUMBER_OF_DATA_CLASSES = @@NUMBER_OF_DATA_CLASSES@@
        |
        |    private val cache: Cache<Map<String, CurrencyData>> by lazy {
        |        createSizedCache(NUMBER_OF_DATA_CLASSES)
        |    }
        |
        |    private val prioritizedCurrencies = CurrenciesData0().currencies
        |
        |    fun of(code: String): CurrencyData = prioritizedCurrencies.getOrElse(code) {
        |        when (code) {
        |@@WHEN_BRANCHES@@
        |            else -> @@ELSE_BRANCH@@
        |        }
        |    }
        |
        |    private fun findByCode(key: String, code: String, block: () -> Map<String, CurrencyData>): CurrencyData =
        |        cache.get(key) { block() }.findByCode(code)
        |}
        |
    """.trimMargin()

    private val rootLookupClassTemplate = """
        |@@FILE_NOTICE@@
        |
        |package @@MAIN_PACKAGE@@@@SUB_PACKAGE@@
        |
        |import com.eriksencosta.money.caching.Cache
        |import com.eriksencosta.money.currency.createSizedCache
        |import com.eriksencosta.money.currency.findCodeBySecondaryCode
        |
        |@Suppress("CyclomaticComplexMethod")
        |internal object @@CLASS_NAME@@ {
        |    private const val NUMBER_OF_LOOKUP_CLASSES = @@NUMBER_OF_LOOKUP_CLASSES@@
        |
        |    private val cache: Cache<Map<String, String>> by lazy {
        |        createSizedCache(NUMBER_OF_LOOKUP_CLASSES)
        |    }
        |
        |    private val prioritizedCurrencies = CurrenciesLookup0().currencies
        |
        |    fun of(code: String): String = prioritizedCurrencies.getOrElse(code) {
        |        when (code) {
        |@@WHEN_BRANCHES@@
        |            else -> @@ELSE_BRANCH@@
        |        }
        |    }
        |
        |    private fun findCodeBySecondaryCode(key: String, code: String, block: () -> Map<String, String>): String =
        |        cache.get(key) { block() }.findCodeBySecondaryCode(code)
        |}
        |
    """.trimMargin()

    private val codesClassTemplate = """
        |@@FILE_NOTICE@@
        |
        |package @@MAIN_PACKAGE@@@@SUB_PACKAGE@@
        |
        |// @@NUMBER_OF_CURRENCIES@@ currencies
        |@Suppress("LargeClass")
        |internal class @@CLASS_NAME@@ {
        |    val codes: Set<String> get() = setOf(
        |@@SET_ENTRIES@@
        |    )
        |}
        |
    """.trimMargin()

    private val bundleClassTemplate = """
        |@@FILE_NOTICE@@
        |
        |package @@MAIN_PACKAGE@@@@SUB_PACKAGE@@
        |
        |import com.eriksencosta.money.currency.CurrencyBundle
        |import com.eriksencosta.money.currency.CurrencyData
        |import com.eriksencosta.money.currency.UndefinedCurrencyData
        |import @@MAIN_PACKAGE@@@@SUB_PACKAGE@@.data.CurrenciesCodes
        |import @@MAIN_PACKAGE@@@@SUB_PACKAGE@@.data.CurrenciesData
        |import @@MAIN_PACKAGE@@@@SUB_PACKAGE@@.data.CurrenciesLookup
        |import @@MAIN_PACKAGE@@@@SUB_PACKAGE@@.data.CurrenciesSecondaryCodes
        |
        |internal object @@CLASS_NAME@@ : CurrencyBundle {
        |    // private const val NUMBER_OF_CURRENCIES: Int = @@NUMBER_OF_CURRENCIES@@
        |    private const val CODES_REGEX_PATTERN: String = "[@@CODES_CHARS_PATTERN@@]{@@CODES_CHARS_PATTERN_LENGTH@@}"
        |    private const val SECONDARY_CODES_REGEX_PATTERN: String = "[@@SECONDARY_CODES_CHARS_PATTERN@@]{@@SECONDARY_CODES_CHARS_PATTERN_LENGTH@@}"
        |
        |    // fun currencies(): List<CurrencyData> = codes().map { ofCode(it) }
        |    // override fun numberOfCurrencies(): Int = NUMBER_OF_CURRENCIES
        |    override fun codes(): Set<String> = CurrenciesCodes().codes
        |    override fun secondaryCodes(): Set<String> = CurrenciesSecondaryCodes().codes
        |
        |    override fun patternForCode(): Regex = Regex("^${'$'}@@CODES_REGEX_PATTERN_VARIABLE_NAME@@\${'$'}")
        |    override fun patternForSecondaryCode(): Regex = Regex("^$@@SECONDARY_CODES_REGEX_PATTERN_VARIABLE_NAME@@\$")
        |
        |    override fun ofCode(code: String): CurrencyData = CurrenciesData.of(code)
        |    override fun ofSecondaryCode(code: String): CurrencyData = CurrenciesLookup.of(code).let {
        |        if (it.isNotBlank()) ofCode(it) else UndefinedCurrencyData()
        |    }
        |}
        |
    """.trimMargin()

    protected fun generateFiles(basePath: String) {
        val path = "$basePath/currency/${packageName()}"
        val source = currenciesSource()

        val currenciesGroups = groupCurrencies(
            source.prioritizedCurrencies,
            source.fallbackCurrencies
        )

        val currenciesGroupsSecondary = groupCurrencies(
            source.prioritizedCurrenciesBySecondaryCode,
            source.fallbackCurrenciesBySecondaryCode
        )

        val prioritizedSecondaryCodes = source.prioritizedCurrencies
            .map { it.value.secondaryCode to it.value.code }
            .toMap()

        val uniqueSecondaryCodes = source.uniqueSecondaryCodes

        val secondaryCodeToPrimary = mutableMapOf<String, Pair<String, Int>>()
        val files = mutableListOf<KotlinFileSpec>()

        currenciesGroups.forEach { (group, currencies) ->
            secondaryCodeToPrimary.putAll(
                currencies
                    .filter {
                        prioritizedSecondaryCodes.contains(it.secondaryCode) ||
                            uniqueSecondaryCodes.contains(it.secondaryCode)
                    }
                    .map {
                        when (prioritizedSecondaryCodes.contains(it.secondaryCode)) {
                            true -> it.secondaryCode to (prioritizedSecondaryCodes.getValue(it.secondaryCode) to 0)
                            false -> it.secondaryCode to (it.code to group)
                        }
                    }
            )

            files += dataFileSpec(group, currencies)
        }

        currenciesGroupsSecondary.forEach { (group, currencies) ->
            files += lookupFileSpec(group, currencies, secondaryCodeToPrimary)
        }

        files += rootDataFileSpec(currenciesGroups)
        files += rootLookupFileSpec(currenciesGroupsSecondary)
        files += codesFileSpec(source.currencies)
        files += secondaryCodesFileSpec(source.currencies, secondaryCodeToPrimary)
        files += factoryFileSpec(source)

        files
            .map { it.mergeReplacements(fileNotice(), packageDeclaration()) }
            .map { it.toProcessedKotlinFileSpec(path) }
            .forEach {
                println("Generating file ${it.file}...")
                it.file.dump(it.contents)
            }
            .also { println("Done.") }
    }

    @Suppress("CognitiveComplexMethod", "NestedBlockDepth")
    private fun groupCurrencies(
        prioritizedCurrencies: CurrencyMap,
        fallbackCurrencies: CurrencyMap
    ): GroupedCurrencies = run {
        val totalCurrencies = fallbackCurrencies.size
        val totalFiles = ceil(totalCurrencies / currenciesPerFile().toDouble()).toInt()
        val allCurrencies = fallbackCurrencies.values.toList()
        val currenciesGroups = mutableMapOf<Int, List<RawCurrency>>()

        for (group in 0..totalFiles) {
            val currencies: List<RawCurrency> = when (group) {
                0 -> prioritizedCurrencies.map { it.value }
                else -> {
                    val startIndex = (group - 1) * currenciesPerFile()
                    val endIndex = startIndex + currenciesPerFile()

                    if (endIndex > totalCurrencies)
                        allCurrencies.subList(startIndex, totalCurrencies)
                    else
                        allCurrencies.subList(startIndex, endIndex)
                }
            }

            currenciesGroups[group] = currencies
        }

        currenciesGroups
    }

    private fun dataFileSpec(group: Int, currencies: List<RawCurrency>) = run {
        val entries = currencies.map {
            """"%s" to CurrencyData("%s", "%s", "%s", "%s", "%s", %d),""".format(
                it.code,
                it.code,
                it.secondaryCode.escape(),
                it.name.escape(),
                it.symbol.escape(),
                it.type,
                it.minorUnits
            )
        }.joinNewline(2)

        KotlinFileSpec(
            "data",
            "CurrenciesData$group",
            dataClassTemplate,
            mapOf(
                "@@NUMBER_OF_CURRENCIES@@" to currencies.size.toString(),
                "@@MAP_ENTRIES@@" to entries
            )
        )
    }

    private fun lookupFileSpec(group: Int, currencies: List<RawCurrency>, secondaryLookup: SecondaryLookup) = run {
        val entries = currencies.map {
            val lookup = secondaryLookup.getValue(it.secondaryCode)

            """"%s" to "%s", // %s available in CurrenciesData%d""".format(
                it.secondaryCode.escape(),
                lookup.first,
                it.name,
                lookup.second
            )
        }.joinNewline(2)

        KotlinFileSpec(
            "data",
            "CurrenciesLookup$group",
            lookupClassTemplate,
            mapOf(
                "@@NUMBER_OF_CURRENCIES@@" to currencies.size.toString(),
                "@@MAP_ENTRIES@@" to entries
            )
        )
    }

    @Suppress("MagicNumber")
    private fun rootDataFileSpec(groupedCurrencies: GroupedCurrencies) = run {
        val entries = groupedCurrencies.filterKeys { it > 0 }.map { (group, currencies) ->
            """in "%s".."%s" -> findByCode("Data%d", code) { CurrenciesData%d().currencies }""".format(
                currencies.first().code,
                currencies.last().code,
                group,
                group
            )
        }.joinNewline(3)

        KotlinFileSpec(
            "data",
            "CurrenciesData",
            rootDataClassTemplate,
            mapOf(
                "@@NUMBER_OF_DATA_CLASSES@@" to groupedCurrencies.size.toString(),
                "@@WHEN_BRANCHES@@" to entries,
                "@@ELSE_BRANCH@@" to "UndefinedCurrencyData(code)"
            )
        )
    }

    @Suppress("MagicNumber")
    private fun rootLookupFileSpec(groupedCurrencies: GroupedCurrencies) = run {
        val entries = groupedCurrencies.filterKeys { it > 0 }.map { (group, currencies) ->
            """in "%s".."%s" -> findCodeBySecondaryCode("Lookup%d", code) { CurrenciesLookup%d().currencies }""".format(
                currencies.first().secondaryCode.escape(),
                currencies.last().secondaryCode.escape(),
                group,
                group
            )
        }.joinNewline(3)

        KotlinFileSpec(
            "data",
            "CurrenciesLookup",
            rootLookupClassTemplate,
            mapOf(
                "@@NUMBER_OF_LOOKUP_CLASSES@@" to groupedCurrencies.size.toString(),
                "@@WHEN_BRANCHES@@" to entries,
                "@@ELSE_BRANCH@@" to "\"\""
            )
        )
    }

    private fun codesFileSpec(currencies: CurrencyMap) = run {
        val entries = currencies.map { """"%s", // %s""".format(it.key, it.value.name) }.joinNewline(2)

        KotlinFileSpec(
            "data",
            "CurrenciesCodes",
            codesClassTemplate,
            mapOf(
                "@@NUMBER_OF_CURRENCIES@@" to currencies.size.toString(),
                "@@SET_ENTRIES@@" to entries,
            )
        )
    }

    private fun secondaryCodesFileSpec(currencies: CurrencyMap, secondaryLookup: SecondaryLookup) = run {
        val entries = secondaryLookup
            .map { it.key to it.value.first }
            .toMap()
            .toSortedMap()
            .map { """"%s", // %s""".format(it.key.escape(), currencies.getValue(it.value).name) }
            .joinNewline(2)

        KotlinFileSpec(
            "data",
            "CurrenciesSecondaryCodes",
            codesClassTemplate,
            mapOf(
                "@@NUMBER_OF_CURRENCIES@@" to currencies.size.toString(),
                "@@SET_ENTRIES@@" to entries,
            )
        )
    }

    private fun factoryFileSpec(source: SourceCurrencies) = KotlinFileSpec(
        "",
        "${currencyGroup()}CurrencyBundle",
        bundleClassTemplate,
        mapOf(
            "@@NUMBER_OF_CURRENCIES@@" to source.currencies.size.toString(),

            "@@CODES_CHARS_PATTERN@@" to source.codesUniqueChars.toRegexPattern(),
            "@@CODES_CHARS_PATTERN_LENGTH@@" to source.codes.first().length.toString(),
            "@@CODES_REGEX_PATTERN_VARIABLE_NAME@@" to "CODES_REGEX_PATTERN",

            "@@SECONDARY_CODES_CHARS_PATTERN@@" to source.secondaryCodesUniqueChars.toRegexPattern(),
            "@@SECONDARY_CODES_CHARS_PATTERN_LENGTH@@" to run {
                val min = source.secondaryCodesMinMaxLengths.first
                val max = source.secondaryCodesMinMaxLengths.second

                when (min != max) {
                    true -> "$min,$max"
                    false -> "$min"
                }
            },
            "@@SECONDARY_CODES_REGEX_PATTERN_VARIABLE_NAME@@" to "SECONDARY_CODES_REGEX_PATTERN",
        )
    )

    private fun packageName() = currencyGroup().lowercase()

    private fun packageDeclaration() = "com.eriksencosta.money.currency.${packageName()}"

    private fun Set<String>.toRegexPattern() = joinToString("") { it.escapeForRegex() }.optimizeForRegex()
}
