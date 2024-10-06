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

import com.eriksencosta.money.gradle.PlainFileSpec
import com.eriksencosta.money.gradle.currency.SourceCurrencies.RawCurrency
import com.eriksencosta.money.gradle.dump

internal abstract class CurrenciesDocumentationGenerator {
    private val documentationTemplate = """
        |@@FILE_NOTICE@@
        |# @@TITLE@@
        |@@CONTENTS@@
        |
    """.trimMargin()

    protected abstract fun gradleTask(): String
    protected abstract fun currenciesSource(): SourceCurrencies
    protected abstract fun fileName(): String
    protected abstract fun title(): String
    protected abstract fun secondaryCodeName(): String
    protected abstract fun fileNotice(): String
    protected abstract fun contentsTemplate(): String

    protected fun generateFile(basePath: String) {
        val path = "$basePath/${fileName()}"

        PlainFileSpec(
            documentationTemplate,
            mapOf(
                "@@TITLE@@" to title(),
                "@@CONTENTS@@" to contents(),
                "@@NUMBER_OF_CURRENCIES@@" to currencies().size.toString(),
            )
        )
            .mergeReplacements(fileNotice())
            .toProcessedPlainFileSpec(path).let {
                it.file.dump(it.contents)
            }
    }

    private fun currencies(): List<RawCurrency> = currenciesSource().currencies.values.toList()

    private fun contents(): String = contentsTemplate() + "\n" + table()

    private fun table(): String = run {
        val columns = columns().map { Column(it.key, it.value) }

        val headers = columns.joinToString("|", "|", "|") { it.pad(it.name) }
        val separators = columns.joinToString("|", "|", "|") { it.separator() }
        val rows = columns.rows().joinToString("\n") {
            it.joinToString("|", "|", "|")
        }

        "%s\n%s\n%s".format(headers, separators, rows)
    }

    private fun columns(): Map<String, List<String>> = currencies().let { currencies ->
        val uniqueSecondaryCodes = currenciesSource().uniqueSecondaryCodes
        val prioritizedCurrencies = currenciesSource().prioritizedCurrencies

        mapOf(
            "Code" to currencies.map { "`%s`".format(it.code) },
            secondaryCodeName() to currencies.map {
                val supportsCreationBySecondaryCode = uniqueSecondaryCodes.contains(it.secondaryCode) ||
                    prioritizedCurrencies.containsKey(it.code)

                "`${it.secondaryCode}`" + when (supportsCreationBySecondaryCode) {
                    true -> " ✅"
                    false -> ""
                }
            },
            "Name" to currencies.map { it.name },
            "Symbol" to currencies.map { it.symbol },
            "Type" to currencies.map { it.type.capitalize() },
            "Minor units" to currencies.map { it.minorUnits.toString() },
        )
    }

    private data class Column(val name: String, val values: List<String>) {
        val maxLength = (values + name).maxOf { it.length }

        fun pad(value: String): String = " " + value.padEnd(maxLength, ' ') + " "
        fun separator(): String = "-".repeat(maxLength + 2)
    }

    private fun List<Column>.rows(): List<List<String>> = (0..<this[0].values.size).map { row ->
        map { column -> column.pad(column.values[row]) }
    }

    private fun String.capitalize(): String = lowercase().replaceFirstChar { it.uppercase() }
}
