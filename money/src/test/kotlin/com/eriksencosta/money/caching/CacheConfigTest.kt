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

import org.junit.jupiter.api.assertThrows
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals

class CacheConfigTest {
    @Test
    fun `Throw exception when maximum items is lower than or equal to zero`() =
        assertThrows<IllegalArgumentException> { CacheConfig().apply { maximumItems = 0 } }.run {
            assertEquals("The maximumItems value must be greater than 0", message)
        }

    @Test
    fun `Throw exception when expiration is lower than or equal to zero`() =
        assertThrows<IllegalArgumentException> { CacheConfig().apply { expirationTime = 0 } }.run {
            assertEquals("The expirationTime value must be greater than 0", message)
        }

    @Test
    fun `Create a CacheConfig`() {
        val config = CacheConfig().apply {
            maximumItems = 100
            expirationTime = 1
            expirationTimeUnit = TimeUnit.HOURS
        }

        assertEquals(100, config.maximumItems)
        assertEquals(1, config.expirationTime)
        assertEquals(TimeUnit.HOURS, config.expirationTimeUnit)
    }
}
