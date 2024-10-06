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

import com.eriksencosta.money.Money
import com.eriksencosta.money.allocation.Fixtures.oneCent
import com.eriksencosta.money.allocation.Fixtures.threeCents
import com.eriksencosta.money.allocation.Fixtures.twoCents
import com.eriksencosta.money.allocation.Fixtures.zeroCents
import com.eriksencosta.money.allocation.difference.Expectations.Case.Expectation
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import kotlin.test.assertEquals

internal object Expectations {
    private val allocations = listOf(
        listOf(-oneCent),
        listOf(zeroCents),
        listOf(oneCent),
        listOf(zeroCents, zeroCents),
        listOf(zeroCents, oneCent),
        listOf(oneCent, zeroCents),
        listOf(oneCent, zeroCents, zeroCents),
        listOf(oneCent, oneCent, zeroCents),
        listOf(oneCent, oneCent, oneCent),
        listOf(oneCent, oneCent, twoCents, oneCent),
    )

    private fun List<Case>.validate(): List<Case> = run {
        val casesAllocations = map { it.allocations }

        if (!casesAllocations.containsAll(allocations)) {
            val missing = allocations.subtract(casesAllocations.toSet())
            throw IllegalArgumentException("The list of cases is missing the following allocation tests: $missing")
        }

        this
    }

    internal data class AllocationExpectations(
        val negativeThreeCents: List<Money>,
        val negativeTwoCents: List<Money>,
        val negativeOneCent: List<Money>,
        val oneCent: List<Money>,
        val twoCents: List<Money>,
        val threeCents: List<Money>,
    )

    internal abstract class Case(open val allocations: List<Money>) {
        enum class Expectation {
            // Biased cases
            ON_FIRST_GREATEST,
            ON_FIRST_NON_ZERO,
            ON_FIRST_SMALLEST,
            ON_LAST_GREATEST,
            ON_LAST_NON_ZERO,
            ON_LAST_SMALLEST,

            // Distribution cases
            DISTRIBUTION,

            // Directional cases
            NEGATIVE_ON_FIRST,
            NEGATIVE_ON_GREATEST,
            POSITIVE_ON_FIRST,
            POSITIVE_ON_GREATEST,
            DISTRIBUTE_BY_SIGN,

            // Discard cases
            DISCARD,

            // Indexed-based cases
            ON_FIRST,
            ON_LAST,
        }

        abstract fun expectations(name: Expectation): Map<Money, List<Money>>

        protected fun toMap(expectations: AllocationExpectations): Map<Money, List<Money>> = mapOf(
            -threeCents to expectations.negativeThreeCents,
            -twoCents to expectations.negativeTwoCents,
            -oneCent to expectations.negativeOneCent,
            zeroCents to allocations,
            oneCent to expectations.oneCent,
            twoCents to expectations.twoCents,
            threeCents to expectations.threeCents,
        )
    }

    internal data class BiasedCase(
        override val allocations: List<Money>,
        val onFirstGreatest: AllocationExpectations,
        val onFirstNonZero: AllocationExpectations,
        val onFirstSmallest: AllocationExpectations,
        val onLastGreatest: AllocationExpectations,
        val onLastNonZero: AllocationExpectations,
        val onLastSmallest: AllocationExpectations
    ) : Case(allocations) {
        @SuppressWarnings("CyclomaticComplexMethod", "ElseCaseInsteadOfExhaustiveWhen")
        override fun expectations(name: Expectation): Map<Money, List<Money>> = when (name) {
            Expectation.ON_FIRST_GREATEST -> toMap(onFirstGreatest)
            Expectation.ON_FIRST_NON_ZERO -> toMap(onFirstNonZero)
            Expectation.ON_FIRST_SMALLEST -> toMap(onFirstSmallest)
            Expectation.ON_LAST_GREATEST -> toMap(onLastGreatest)
            Expectation.ON_LAST_NON_ZERO -> toMap(onLastNonZero)
            Expectation.ON_LAST_SMALLEST -> toMap(onLastSmallest)
            else -> throw IllegalArgumentException("Not a biased case")
        }
    }

    internal data class DiscardCase(override val allocations: List<Money>) : Case(allocations) {
        @Suppress("ElseCaseInsteadOfExhaustiveWhen")
        override fun expectations(name: Expectation): Map<Money, List<Money>> = when (name) {
            Expectation.DISCARD -> toMap(
                AllocationExpectations(allocations, allocations, allocations, allocations, allocations, allocations)
            )
            else -> throw IllegalArgumentException("Not a discard case")
        }
    }

    internal data class DistributionCase(
        override val allocations: List<Money>,
        val distribution: AllocationExpectations,
    ) : Case(allocations) {
        @Suppress("ElseCaseInsteadOfExhaustiveWhen")
        override fun expectations(name: Expectation): Map<Money, List<Money>> = when (name) {
            Expectation.DISTRIBUTION -> toMap(distribution)
            else -> throw IllegalArgumentException("Not a distribution case")
        }
    }

    internal data class IndexBasedCase(
        override val allocations: List<Money>,
        val onFirst: AllocationExpectations,
        val onLast: AllocationExpectations
    ) : Case(allocations) {
        @Suppress("ElseCaseInsteadOfExhaustiveWhen")
        override fun expectations(name: Expectation): Map<Money, List<Money>> = when (name) {
            Expectation.ON_FIRST -> toMap(onFirst)
            Expectation.ON_LAST -> toMap(onLast)
            else -> throw IllegalArgumentException("Not an index based case")
        }
    }

    internal data class SignBasedCase(
        override val allocations: List<Money>,
        val negativeOnFirst: AllocationExpectations,
        val negativeOnGreatest: AllocationExpectations,
        val positiveOnFirst: AllocationExpectations,
        val positiveOnGreatest: AllocationExpectations,
        val distributeBySign: AllocationExpectations,
    ) : Case(allocations) {
        @Suppress("ElseCaseInsteadOfExhaustiveWhen")
        override fun expectations(name: Expectation): Map<Money, List<Money>> = when (name) {
            Expectation.NEGATIVE_ON_FIRST -> toMap(negativeOnFirst)
            Expectation.NEGATIVE_ON_GREATEST -> toMap(negativeOnGreatest)
            Expectation.POSITIVE_ON_FIRST -> toMap(positiveOnFirst)
            Expectation.POSITIVE_ON_GREATEST -> toMap(positiveOnGreatest)
            Expectation.DISTRIBUTE_BY_SIGN -> toMap(distributeBySign)
            else -> throw IllegalArgumentException("Not a sign-based case")
        }
    }

    fun assertions(cases: List<Case>, allocator: DifferenceAllocation, name: Expectation): List<DynamicTest> =
        cases.validate().map { case ->
            case.expectations(name).map {
                val allocations = case.allocations
                val difference = it.key
                val expected = it.value

                dynamicTest("given $allocations when I allocate $difference then I should get $expected") {
                    assertEquals(expected, allocator.allocate(difference, allocations))
                }
            }
        }.flatten()
}
