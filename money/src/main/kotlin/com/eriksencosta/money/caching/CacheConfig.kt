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

import java.util.concurrent.TimeUnit

/**
 * Cache configuration options used by the [configureCache] function.
 *
 * @see[configureCache]
 */
public class CacheConfig internal constructor() {
    /**
     * The number of objects to keep in the cache. Defaults to 50.
     */
    public var maximumItems: Long = DEFAULT_MAXIMUM_ITEMS
        set(value) = require(0 < value) {
            "The maximumItems value must be greater than 0"
        }.let { field = value }

    /**
     * The amount of time to keep an object in the cache. Defaults to 60 (minutes).
     */
    public var expirationTime: Long = DEFAULT_EXPIRATION_TIME
        set(value) = require(0 < value) {
            "The expirationTime value must be greater than 0"
        }.let { field = value }

    /**
     * The time unit of [expirationTime]. Defaults to [TimeUnit.MINUTES].
     */
    public var expirationTimeUnit: TimeUnit = TimeUnit.MINUTES

    private companion object {
        private const val DEFAULT_MAXIMUM_ITEMS: Long = 50
        private const val DEFAULT_EXPIRATION_TIME: Long = 30
    }
}
