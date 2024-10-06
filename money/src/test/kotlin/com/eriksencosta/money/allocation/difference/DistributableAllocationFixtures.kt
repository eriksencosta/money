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
import com.eriksencosta.money.allocation.difference.Expectations.DistributionCase

internal object DistributableAllocationFixtures {
    val cases = listOf(
        DistributionCase(
            listOf(-oneCent),
            AllocationExpectations(
                listOf(-fourCents),
                listOf(-threeCents),
                listOf(-twoCents),
                listOf(zeroCents),
                listOf(oneCent),
                listOf(twoCents),
            ),
        ),

        DistributionCase(
            listOf(zeroCents),
            AllocationExpectations(
                listOf(-threeCents),
                listOf(-twoCents),
                listOf(-oneCent),
                listOf(oneCent),
                listOf(twoCents),
                listOf(threeCents),
            ),
        ),

        DistributionCase(
            listOf(oneCent),
            AllocationExpectations(
                listOf(-twoCents),
                listOf(-oneCent),
                listOf(zeroCents),
                listOf(twoCents),
                listOf(threeCents),
                listOf(fourCents),
            ),
        ),

        DistributionCase(
            listOf(zeroCents, zeroCents),
            AllocationExpectations(
                listOf(-oneCent, -twoCents),
                listOf(-oneCent, -oneCent),
                listOf(zeroCents, -oneCent),
                listOf(zeroCents, oneCent),
                listOf(oneCent, oneCent),
                listOf(oneCent, twoCents),
            ),
        ),

        DistributionCase(
            listOf(zeroCents, oneCent),
            AllocationExpectations(
                listOf(-oneCent, -oneCent),
                listOf(zeroCents, -oneCent),
                listOf(zeroCents, zeroCents),
                listOf(oneCent, oneCent),
                listOf(oneCent, twoCents),
                listOf(twoCents, twoCents),
            ),
        ),

        DistributionCase(
            listOf(oneCent, zeroCents),
            AllocationExpectations(
                listOf(-oneCent, -oneCent),
                listOf(zeroCents, -oneCent),
                listOf(zeroCents, zeroCents),
                listOf(oneCent, oneCent),
                listOf(oneCent, twoCents),
                listOf(twoCents, twoCents),
            ),
        ),

        DistributionCase(
            listOf(oneCent, zeroCents, zeroCents),
            AllocationExpectations(
                listOf(zeroCents, -oneCent, -oneCent),
                listOf(zeroCents, zeroCents, -oneCent),
                listOf(zeroCents, zeroCents, zeroCents),
                listOf(oneCent, zeroCents, oneCent),
                listOf(oneCent, oneCent, oneCent),
                listOf(oneCent, oneCent, twoCents),
            ),
        ),

        DistributionCase(
            listOf(oneCent, oneCent, zeroCents),
            AllocationExpectations(
                listOf(zeroCents, zeroCents, -oneCent),
                listOf(zeroCents, zeroCents, zeroCents),
                listOf(oneCent, zeroCents, zeroCents),
                listOf(oneCent, oneCent, oneCent),
                listOf(oneCent, oneCent, twoCents),
                listOf(oneCent, twoCents, twoCents),
            ),
        ),

        DistributionCase(
            listOf(oneCent, oneCent, oneCent),
            AllocationExpectations(
                listOf(zeroCents, zeroCents, zeroCents),
                listOf(oneCent, zeroCents, zeroCents),
                listOf(oneCent, oneCent, zeroCents),
                listOf(oneCent, oneCent, twoCents),
                listOf(oneCent, twoCents, twoCents),
                listOf(twoCents, twoCents, twoCents),
            ),
        ),

        DistributionCase(
            listOf(oneCent, oneCent, twoCents, oneCent),
            AllocationExpectations(
                listOf(zeroCents, zeroCents, twoCents, zeroCents),
                listOf(oneCent, zeroCents, twoCents, zeroCents),
                listOf(oneCent, oneCent, twoCents, zeroCents),
                listOf(oneCent, oneCent, twoCents, twoCents),
                listOf(oneCent, twoCents, twoCents, twoCents),
                listOf(twoCents, twoCents, twoCents, twoCents),
            ),
        ),
    )
}
