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

package com.eriksencosta.money.allocation.difference

import com.eriksencosta.money.allocation.Fixtures.oneCent
import com.eriksencosta.money.allocation.Fixtures.twoCents
import com.eriksencosta.money.allocation.Fixtures.zeroCents
import com.eriksencosta.money.allocation.difference.Expectations.DiscardCase

internal object DiscardFixtures {
    val cases = listOf(
        DiscardCase(listOf(-oneCent)),
        DiscardCase(listOf(zeroCents)),
        DiscardCase(listOf(oneCent)),
        DiscardCase(listOf(zeroCents, zeroCents)),
        DiscardCase(listOf(zeroCents, oneCent)),
        DiscardCase(listOf(oneCent, zeroCents)),
        DiscardCase(listOf(oneCent, zeroCents, zeroCents)),
        DiscardCase(listOf(oneCent, oneCent, zeroCents)),
        DiscardCase(listOf(oneCent, oneCent, oneCent)),
        DiscardCase(listOf(oneCent, oneCent, twoCents, oneCent)),
    )
}
