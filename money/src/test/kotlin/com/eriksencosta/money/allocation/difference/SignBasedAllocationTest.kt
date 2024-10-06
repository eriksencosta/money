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

import com.eriksencosta.money.allocation.difference.Expectations.Case.Expectation.DISTRIBUTE_BY_SIGN
import com.eriksencosta.money.allocation.difference.Expectations.Case.Expectation.NEGATIVE_ON_FIRST
import com.eriksencosta.money.allocation.difference.Expectations.Case.Expectation.NEGATIVE_ON_GREATEST
import com.eriksencosta.money.allocation.difference.Expectations.Case.Expectation.POSITIVE_ON_FIRST
import com.eriksencosta.money.allocation.difference.Expectations.Case.Expectation.POSITIVE_ON_GREATEST
import com.eriksencosta.money.allocation.difference.Expectations.assertions
import com.eriksencosta.money.allocation.difference.SignBasedAllocationFixtures.cases
import org.junit.jupiter.api.TestFactory

class SignBasedAllocationTest {
    @TestFactory
    fun `Allocate a negative difference to the first item and a positive on the last non-zero`() =
        assertions(cases, NegativeOnFirst, NEGATIVE_ON_FIRST)

    @TestFactory
    fun `Allocate a negative difference to the first item with the greatest value and distribute a positive`() =
        assertions(cases, NegativeOnGreatest, NEGATIVE_ON_GREATEST)

    @TestFactory
    fun `Allocate a positive difference to the first item and a negative on the last non-zero`() =
        assertions(cases, PositiveOnFirst, POSITIVE_ON_FIRST)

    @TestFactory
    fun `Allocate a positive difference to the first item with the greatest value and distribute a negative`() =
        assertions(cases, PositiveOnGreatest, POSITIVE_ON_GREATEST)

    @TestFactory
    fun `Distribute the difference according to its sign`() =
        assertions(cases, DistributeBySign.positiveOnFirstSmallest(), DISTRIBUTE_BY_SIGN)
}
