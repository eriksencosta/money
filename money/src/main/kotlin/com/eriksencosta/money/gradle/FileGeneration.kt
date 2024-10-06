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

package com.eriksencosta.money.gradle

import com.eriksencosta.money.gradle.currency.SourceCurrencies.RawCurrency
import java.io.File

internal typealias SecondaryLookup = Map<String, Pair<String, Int>>
internal typealias GroupedCurrencies = Map<Int, List<RawCurrency>>
internal typealias CurrencyMap = Map<String, RawCurrency>

private val stripWhitespacePattern = Regex(" +$", RegexOption.MULTILINE)

internal data class KotlinFileSpec(
    val packageName: String,
    val className: String,
    val contents: String,
    val replacements: Map<String, String> = emptyMap()
) {
    fun mergeReplacements(fileNotice: String, packageDeclaration: String): KotlinFileSpec = copy(
        replacements = replacements + mapOf(
            "@@FILE_NOTICE@@" to fileNotice,
            "@@MAIN_PACKAGE@@" to packageDeclaration,
            "@@SUB_PACKAGE@@" to subPackageForDeclaration(),
            "@@CLASS_NAME@@" to className,
        )
    )

    fun toProcessedKotlinFileSpec(path: String): ProcessedKotlinFileSpec = ProcessedKotlinFileSpec(
        File("$path/${subPackageForFilePath()}$className.kt"),
        className,
        replacements.toList().fold(contents) { acc, pair ->
            acc.replace(pair.first, pair.second)
        }
    )

    private fun subPackageForDeclaration() = if (packageName.isNotBlank()) ".$packageName" else ""
    private fun subPackageForFilePath() = if (packageName.isNotBlank()) "$packageName/" else ""
}

internal data class ProcessedKotlinFileSpec(
    val file: File,
    val className: String,
    val contents: String
)

internal data class PlainFileSpec(
    val contents: String,
    val replacements: Map<String, String> = emptyMap()
) {
    fun mergeReplacements(fileNotice: String = ""): PlainFileSpec = copy(
        replacements = replacements + mapOf("@@FILE_NOTICE@@" to fileNotice)
    )

    fun toProcessedPlainFileSpec(path: String): ProcessedPlainFileSpec = ProcessedPlainFileSpec(
        File(path),
        replacements.toList().fold(contents) { acc, pair ->
            acc.replace(pair.first, pair.second)
        }
    )
}

internal data class ProcessedPlainFileSpec(
    val file: File,
    val contents: String
)

internal fun File.dump(contents: String) = delete().let {
    writeText(stripWhitespacePattern.replace(contents, ""))
}

internal fun List<String>.joinNewline(indentLevel: Int = 0) = joinToString("\n").indentBy(indentLevel)

@Suppress("MagicNumber")
internal fun String.indentBy(level: Int = 0) = run {
    val indent = if (level == 0) "" else " ".repeat(level * 4)
    replaceIndent(indent)
}

internal fun String.escape(): String = replace("$", "\\\$")

internal fun String.escapeForRegex(): String = replace("$", "\\\$").replace("-", ("\\" + "\\-"))

internal fun String.optimizeForRegex(): String = replace("ABCDEFGHIJKLMNOPQRSTUVWXYZ", "A-Z")
    .replace("abcdefghijklmnopqrstuvwxyz", "a-z")
    .replace("0123456789", "0-9")
