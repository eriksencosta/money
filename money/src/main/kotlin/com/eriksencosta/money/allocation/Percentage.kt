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

import com.eriksencosta.math.percentage.Percentage
import com.eriksencosta.math.percentage.percent
import com.eriksencosta.math.percentage.sum

private const val TOLERANCE: Double = 9E-14
private const val ONE_HUNDRED: Double = 100.0

/**
 * Adjust a list of [Percentage] to sum up to 100% if the difference is within the given [tolerance]. Useful when
 * working with a list of [Percentage] calculated using floating-point arithmetic.
 *
 * The difference is always added to the first list item.
 *
 * @receiver[List]
 * @param[tolerance] The difference [tolerance]. Defaults to 9E-14.
 * @throws[IllegalArgumentException] If the difference is not within the [tolerance].
 * @return A list of [Percentage].
 */
public fun List<Percentage>.adjustForAllocate(tolerance: Double = TOLERANCE): List<Percentage> = run {
    val total = sum()
    val difference = ONE_HUNDRED - total.value
    val isWithinTolerance = difference <= tolerance

    when {
        total.isOneHundred -> this
        isWithinTolerance -> listOf({ this[0].value + difference }.percent()) + subList(1, size)
        else -> throw IllegalArgumentException(
            "Can not adjust the ratios. The difference between $total and 100% is not within the tolerance ($tolerance)"
        )
    }
}
