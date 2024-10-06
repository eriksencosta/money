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

package com.eriksencosta.money.currency

internal interface CurrencyBundle {
    // fun currencies(): List<CurrencyData> = codes().map { ofCode(it) }
    // fun numberOfCurrencies(): Int
    fun codes(): Set<String>
    fun secondaryCodes(): Set<String>
    fun patternForCode(): Regex
    fun patternForSecondaryCode(): Regex
    fun ofCode(code: String): CurrencyData
    fun ofSecondaryCode(code: String): CurrencyData
}
