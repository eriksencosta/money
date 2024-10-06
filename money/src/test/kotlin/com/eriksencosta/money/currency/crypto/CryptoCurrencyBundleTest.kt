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

package com.eriksencosta.money.currency.crypto

import com.eriksencosta.money.currency.CurrencyBundle
import com.eriksencosta.money.currency.CurrencyBundleTest

internal class CryptoCurrencyBundleTest : CurrencyBundleTest() {
    override val invalidCurrenciesCodes = setOf("000000000")
    override val invalidCurrenciesSecondaryCodes = setOf("\$")
    override val invalidSuffix = "_INVALID"

    override fun factory(): CurrencyBundle = CryptoCurrencyBundle
}
