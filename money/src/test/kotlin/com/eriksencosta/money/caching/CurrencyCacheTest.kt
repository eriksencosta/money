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

package com.eriksencosta.money.caching

import com.eriksencosta.money.Currency
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class CurrencyCacheTest {
    @BeforeEach
    fun setup() = Currency.resetCache()

    @Test
    fun `Throw exception when overriding a previously configured cache`() {
        assertThrows<IllegalStateException> {
            configureCache { maximumItems = 5 }
            configureCache { maximumItems = 10 }
        }.run {
            assertEquals("The factory cache can't be replaced once it is configured or initialized", message)
        }
    }

    @Test
    fun `Throw exception when overriding a previously disabled cache`() {
        assertThrows<IllegalStateException> {
            disableCache()
            configureCache { maximumItems = 10 }
        }.run {
            assertEquals("The factory cache can't be replaced once it is configured or initialized", message)
        }
    }

    @Test
    fun `Throw exception when overriding an initialized cache`() {
        assertThrows<IllegalStateException> {
            Currency.of("USD")
            configureCache { maximumItems = 10 }
        }.run {
            assertEquals("The factory cache can't be replaced once it is configured or initialized", message)
        }
    }

    @Test
    fun `Throw exception when disabling an initialized cache`() {
        assertThrows<IllegalStateException> {
            Currency.of("USD")
            disableCache()
        }.run {
            assertEquals("The factory cache can't be replaced once it is configured or initialized", message)
        }
    }

    @Test
    fun `Disable the cache`() {
        disableCache()

        assertNotSame(Currency.of("USD"), Currency.circulating("USD"))
        assertNotSame(Currency.of("BTC"), Currency.crypto("BTC"))
        assertNotSame(Currency.custom("CUSTOM", 4), Currency.custom("CUSTOM", 4))
    }

    @Test
    fun `Return a cached object`() {
        assertSame(Currency.of("USD"), Currency.circulating("USD"))
        assertSame(Currency.of("BTC"), Currency.crypto("BTC"))
        assertSame(Currency.custom("CUSTOM", 4), Currency.custom("CUSTOM", 4))
    }

    companion object {
        @JvmStatic
        @AfterAll
        fun tearDown(): Unit = Currency.resetCache()
    }
}
