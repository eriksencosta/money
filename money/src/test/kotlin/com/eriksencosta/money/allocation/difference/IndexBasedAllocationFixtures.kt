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

import com.eriksencosta.money.allocation.Fixtures.fourCents
import com.eriksencosta.money.allocation.Fixtures.oneCent
import com.eriksencosta.money.allocation.Fixtures.threeCents
import com.eriksencosta.money.allocation.Fixtures.twoCents
import com.eriksencosta.money.allocation.Fixtures.zeroCents
import com.eriksencosta.money.allocation.difference.Expectations.AllocationExpectations
import com.eriksencosta.money.allocation.difference.Expectations.IndexBasedCase

@SuppressWarnings("LargeClass")
internal object IndexBasedAllocationFixtures {
    val cases = listOf(
        IndexBasedCase(
            listOf(-oneCent),
            AllocationExpectations(
                listOf(-fourCents),
                listOf(-threeCents),
                listOf(-twoCents),
                listOf(zeroCents),
                listOf(oneCent),
                listOf(twoCents),
            ),
            AllocationExpectations(
                listOf(-fourCents),
                listOf(-threeCents),
                listOf(-twoCents),
                listOf(zeroCents),
                listOf(oneCent),
                listOf(twoCents),
            ),
        ),

        IndexBasedCase(
            listOf(zeroCents),
            AllocationExpectations(
                listOf(-threeCents),
                listOf(-twoCents),
                listOf(-oneCent),
                listOf(oneCent),
                listOf(twoCents),
                listOf(threeCents),
            ),
            AllocationExpectations(
                listOf(-threeCents),
                listOf(-twoCents),
                listOf(-oneCent),
                listOf(oneCent),
                listOf(twoCents),
                listOf(threeCents),
            ),
        ),

        IndexBasedCase(
            listOf(oneCent),
            AllocationExpectations(
                listOf(-twoCents),
                listOf(-oneCent),
                listOf(zeroCents),
                listOf(twoCents),
                listOf(threeCents),
                listOf(fourCents),
            ),
            AllocationExpectations(
                listOf(-twoCents),
                listOf(-oneCent),
                listOf(zeroCents),
                listOf(twoCents),
                listOf(threeCents),
                listOf(fourCents),
            ),
        ),

        IndexBasedCase(
            listOf(zeroCents, zeroCents),
            AllocationExpectations(
                listOf(-threeCents, zeroCents),
                listOf(-twoCents, zeroCents),
                listOf(-oneCent, zeroCents),
                listOf(oneCent, zeroCents),
                listOf(twoCents, zeroCents),
                listOf(threeCents, zeroCents),
            ),
            AllocationExpectations(
                listOf(zeroCents, -threeCents),
                listOf(zeroCents, -twoCents),
                listOf(zeroCents, -oneCent),
                listOf(zeroCents, oneCent),
                listOf(zeroCents, twoCents),
                listOf(zeroCents, threeCents),
            ),
        ),

        IndexBasedCase(
            listOf(zeroCents, oneCent),
            AllocationExpectations(
                listOf(-threeCents, oneCent),
                listOf(-twoCents, oneCent),
                listOf(-oneCent, oneCent),
                listOf(oneCent, oneCent),
                listOf(twoCents, oneCent),
                listOf(threeCents, oneCent),
            ),
            AllocationExpectations(
                listOf(zeroCents, -twoCents),
                listOf(zeroCents, -oneCent),
                listOf(zeroCents, zeroCents),
                listOf(zeroCents, twoCents),
                listOf(zeroCents, threeCents),
                listOf(zeroCents, fourCents),
            ),
        ),

        IndexBasedCase(
            listOf(oneCent, zeroCents),
            AllocationExpectations(
                listOf(-twoCents, zeroCents),
                listOf(-oneCent, zeroCents),
                listOf(zeroCents, zeroCents),
                listOf(twoCents, zeroCents),
                listOf(threeCents, zeroCents),
                listOf(fourCents, zeroCents),
            ),
            AllocationExpectations(
                listOf(oneCent, -threeCents),
                listOf(oneCent, -twoCents),
                listOf(oneCent, -oneCent),
                listOf(oneCent, oneCent),
                listOf(oneCent, twoCents),
                listOf(oneCent, threeCents),
            ),
        ),

        IndexBasedCase(
            listOf(oneCent, zeroCents, zeroCents),
            AllocationExpectations(
                listOf(-twoCents, zeroCents, zeroCents),
                listOf(-oneCent, zeroCents, zeroCents),
                listOf(zeroCents, zeroCents, zeroCents),
                listOf(twoCents, zeroCents, zeroCents),
                listOf(threeCents, zeroCents, zeroCents),
                listOf(fourCents, zeroCents, zeroCents),
            ),
            AllocationExpectations(
                listOf(oneCent, zeroCents, -threeCents),
                listOf(oneCent, zeroCents, -twoCents),
                listOf(oneCent, zeroCents, -oneCent),
                listOf(oneCent, zeroCents, oneCent),
                listOf(oneCent, zeroCents, twoCents),
                listOf(oneCent, zeroCents, threeCents),
            ),
        ),

        IndexBasedCase(
            listOf(oneCent, oneCent, zeroCents),
            AllocationExpectations(
                listOf(-twoCents, oneCent, zeroCents),
                listOf(-oneCent, oneCent, zeroCents),
                listOf(zeroCents, oneCent, zeroCents),
                listOf(twoCents, oneCent, zeroCents),
                listOf(threeCents, oneCent, zeroCents),
                listOf(fourCents, oneCent, zeroCents),
            ),
            AllocationExpectations(
                listOf(oneCent, oneCent, -threeCents),
                listOf(oneCent, oneCent, -twoCents),
                listOf(oneCent, oneCent, -oneCent),
                listOf(oneCent, oneCent, oneCent),
                listOf(oneCent, oneCent, twoCents),
                listOf(oneCent, oneCent, threeCents),
            ),
        ),

        IndexBasedCase(
            listOf(oneCent, oneCent, oneCent),
            AllocationExpectations(
                listOf(-twoCents, oneCent, oneCent),
                listOf(-oneCent, oneCent, oneCent),
                listOf(zeroCents, oneCent, oneCent),
                listOf(twoCents, oneCent, oneCent),
                listOf(threeCents, oneCent, oneCent),
                listOf(fourCents, oneCent, oneCent),
            ),
            AllocationExpectations(
                listOf(oneCent, oneCent, -twoCents),
                listOf(oneCent, oneCent, -oneCent),
                listOf(oneCent, oneCent, zeroCents),
                listOf(oneCent, oneCent, twoCents),
                listOf(oneCent, oneCent, threeCents),
                listOf(oneCent, oneCent, fourCents),
            ),
        ),

        IndexBasedCase(
            listOf(oneCent, oneCent, twoCents, oneCent),
            AllocationExpectations(
                listOf(-twoCents, oneCent, twoCents, oneCent),
                listOf(-oneCent, oneCent, twoCents, oneCent),
                listOf(zeroCents, oneCent, twoCents, oneCent),
                listOf(twoCents, oneCent, twoCents, oneCent),
                listOf(threeCents, oneCent, twoCents, oneCent),
                listOf(fourCents, oneCent, twoCents, oneCent),
            ),
            AllocationExpectations(
                listOf(oneCent, oneCent, twoCents, -twoCents),
                listOf(oneCent, oneCent, twoCents, -oneCent),
                listOf(oneCent, oneCent, twoCents, zeroCents),
                listOf(oneCent, oneCent, twoCents, twoCents),
                listOf(oneCent, oneCent, twoCents, threeCents),
                listOf(oneCent, oneCent, twoCents, fourCents),
            ),
        ),
    )
}
