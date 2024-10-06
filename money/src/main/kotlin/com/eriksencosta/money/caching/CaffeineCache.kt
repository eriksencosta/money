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

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Scheduler
import com.github.benmanes.caffeine.cache.Cache as CaffeineCache

internal class CaffeineCache<T>(config: CacheConfig) : Cache<T> {
    private val cache: CaffeineCache<String, T> = Caffeine.newBuilder()
        .scheduler(Scheduler.systemScheduler())
        .maximumSize(config.maximumItems)
        .expireAfterAccess(config.expirationTime, config.expirationTimeUnit)
        .build()

    override fun isInitialized(): Boolean = 0 < cache.estimatedSize()

    @Suppress("UNCHECKED_CAST")
    override fun get(key: String, block: () -> T): T = cache.get(key) { block() } as T

    override fun clean() = cache.invalidateAll()
}

internal fun <T> createCache(config: CacheConfig.() -> Unit = {}): Cache<T> = CaffeineCache(CacheConfig().apply(config))
