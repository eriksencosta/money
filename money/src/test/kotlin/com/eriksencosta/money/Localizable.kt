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

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.util.Locale

internal val defaultLocale: Locale = Locale.getDefault()
internal val brazil: Locale = Locale("pt", "br")
internal val france: Locale = Locale.FRANCE
internal val switzerland: Locale = Locale("de", "ch")
internal val uk: Locale = Locale.UK

/**
 * Changes the environment's [Locale] before running each test method and then reverts it to the environment's default.
 */
internal interface Localizable {
    @BeforeEach
    fun setUp(): Unit = Locale.setDefault(Locale.ENGLISH)

    @AfterEach
    fun tearDown(): Unit = revertToDefaultLocale()

    fun revertToDefaultLocale(): Unit = Locale.setDefault(defaultLocale)
}

/**
 * Runs a [block] after setting the environment [Locale] and then reverts it to the environment's default.
 */
internal inline fun <T> locale(locale: Locale = Locale.ENGLISH, block: () -> T): T = run {
    Locale.setDefault(locale)
    block().also {
        Locale.setDefault(defaultLocale)
    }
}
