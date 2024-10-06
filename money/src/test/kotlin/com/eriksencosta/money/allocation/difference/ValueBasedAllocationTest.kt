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

import com.eriksencosta.money.allocation.difference.BiasedAllocationFixtures.cases
import com.eriksencosta.money.allocation.difference.Expectations.Case.Expectation.ON_FIRST_GREATEST
import com.eriksencosta.money.allocation.difference.Expectations.Case.Expectation.ON_FIRST_NON_ZERO
import com.eriksencosta.money.allocation.difference.Expectations.Case.Expectation.ON_FIRST_SMALLEST
import com.eriksencosta.money.allocation.difference.Expectations.Case.Expectation.ON_LAST_GREATEST
import com.eriksencosta.money.allocation.difference.Expectations.Case.Expectation.ON_LAST_NON_ZERO
import com.eriksencosta.money.allocation.difference.Expectations.Case.Expectation.ON_LAST_SMALLEST
import com.eriksencosta.money.allocation.difference.Expectations.assertions
import org.junit.jupiter.api.TestFactory

class ValueBasedAllocationTest {
    @TestFactory
    fun `Allocate the difference to the first item with the greatest value`() =
        assertions(cases, OnFirstGreatest, ON_FIRST_GREATEST)

    @TestFactory
    fun `Allocate the difference to the first item with non-zero value`() =
        assertions(cases, OnFirstNonZero, ON_FIRST_NON_ZERO)

    @TestFactory
    fun `Allocate the difference to the first item with the smallest value`() =
        assertions(cases, OnFirstSmallest, ON_FIRST_SMALLEST)

    @TestFactory
    fun `Allocate the difference to the last item with the greatest value`() =
        assertions(cases, OnLastGreatest, ON_LAST_GREATEST)

    @TestFactory
    fun `Allocate the difference to the last item with non-zero value`() =
        assertions(cases, OnLastNonZero, ON_LAST_NON_ZERO)

    @TestFactory
    fun `Allocate the difference to the last item with the smallest value`() =
        assertions(cases, OnLastSmallest, ON_LAST_SMALLEST)
}
