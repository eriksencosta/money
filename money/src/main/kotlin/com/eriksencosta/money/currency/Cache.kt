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

@file:Suppress("MagicNumber")

package com.eriksencosta.money.currency

import com.eriksencosta.money.caching.Cache
import com.eriksencosta.money.caching.createCache
import kotlin.math.ceil
import kotlin.math.log

/**
 * Creates a cache for big-sized structures. The cache size is determined by using a logarithmic function, which
 * prevents the creation of a big cache when the number of classes is high.
 *
 * @param[numberOfClasses] The number of classes of the structure to cache.
 */
internal fun <T> createSizedCache(numberOfClasses: Int): Cache<T> = createCache {
    maximumItems = cacheSize(numberOfClasses).toLong()
    expirationTime = 10
}

private fun cacheSize(value: Int): Int = when (15 >= value) {
    true -> calculateCacheSize(value, 10.0, 3)
    false -> calculateCacheSize(value, 2.0, 2)
}

private fun calculateCacheSize(value: Int, logBase: Double, multiplier: Int): Int =
    ceil(ceil(log(value.toDouble(), logBase)) * multiplier).toInt()
