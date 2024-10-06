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

package com.eriksencosta.money.allocation

import com.eriksencosta.money.Money

/**
 * Allocates a [Money] amount in even parts.
 */
public interface EvenAllocation : Allocation<EvenParts> {
    /**
     * Allocates a [Money] amount in even parts.
     *
     * @param[money] The [Money] amount to allocate.
     * @param[parts] The number of parts to allocate.
     * @throws[IllegalArgumentException] When [parts] is zero or negative.
     * @return A [Result] with the allocation result.
     */
    public fun allocate(money: Money, parts: Int): Result = allocate(money, EvenParts(parts))
}

/**
 * Parameter type for even allocation.
 *
 * @param[parts] The number of parts to allocate a [Money] amount.
 * @throws[IllegalArgumentException] When [parts] is zero or negative.
 */
public class EvenParts(public val parts: Int) : AllocationBy {
    init {
        require(0 < parts) { "The number of parts to allocate must be greater than 0" }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is EvenParts && parts == other.parts)

    override fun hashCode(): Int = parts.hashCode()

    override fun toString(): String = parts.toString()
}
