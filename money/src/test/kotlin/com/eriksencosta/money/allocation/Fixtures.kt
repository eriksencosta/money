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

import com.eriksencosta.money.Currency
import com.eriksencosta.money.money

internal object Fixtures {
    internal val usd = Currency.of("USD")
    internal val jpy = Currency.of("JPY")
    internal val exchangeUsd = usd.toCustomCurrency(4)
    internal val exchangeJpy = jpy.toCustomCurrency(4)

    internal val zeroCents = 0.00 money "USD"
    internal val oneCent = 0.01 money "USD"
    internal val twoCents = 0.02 money "USD"
    internal val threeCents = 0.03 money "USD"
    internal val fourCents = 0.04 money "USD"
    internal val fiveCents = 0.05 money "USD"
}
