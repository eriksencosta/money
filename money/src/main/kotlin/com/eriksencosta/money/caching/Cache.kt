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

internal sealed interface Cache<T> {
    fun isInitialized(): Boolean
    fun get(key: String, block: () -> T): T
    fun clean()
}

internal class NoCache<T> : Cache<T> {
    override fun isInitialized(): Boolean = true
    override fun get(key: String, block: () -> T): T = block()
    override fun clean() = Unit
}

internal class DefaultCache<T>(private val cache: Cache<T>) : Cache<T> {
    override fun isInitialized(): Boolean = cache.isInitialized()
    override fun get(key: String, block: () -> T): T = cache.get(key, block)
    override fun clean() = cache.clean()
}
