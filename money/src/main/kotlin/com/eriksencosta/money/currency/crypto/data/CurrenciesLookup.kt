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

/*
 * This file was auto-generated by running the "generateCryptoCurrenciesDataClasses" Gradle task. Always run the
 * task after a DTI database update.
 */

package com.eriksencosta.money.currency.crypto.data

import com.eriksencosta.money.caching.Cache
import com.eriksencosta.money.currency.createSizedCache
import com.eriksencosta.money.currency.findCodeBySecondaryCode

@Suppress("CyclomaticComplexMethod")
internal object CurrenciesLookup {
    private const val NUMBER_OF_LOOKUP_CLASSES = 19

    private val cache: Cache<Map<String, String>> by lazy {
        createSizedCache(NUMBER_OF_LOOKUP_CLASSES)
    }

    private val prioritizedCurrencies = CurrenciesLookup0().currencies

    fun of(code: String): String = prioritizedCurrencies.getOrElse(code) {
        when (code) {
            in "\$HIB".."ACNY" -> findCodeBySecondaryCode("Lookup1", code) { CurrenciesLookup1().currencies }
            in "ACOP".."AKN" -> findCodeBySecondaryCode("Lookup2", code) { CurrenciesLookup2().currencies }
            in "AKPW".."AQAR" -> findCodeBySecondaryCode("Lookup3", code) { CurrenciesLookup3().currencies }
            in "AR".."AZAR" -> findCodeBySecondaryCode("Lookup4", code) { CurrenciesLookup4().currencies }
            in "AZERO".."CH1199021903" -> findCodeBySecondaryCode("Lookup5", code) { CurrenciesLookup5().currencies }
            in "CH1228837865".."ETN" -> findCodeBySecondaryCode("Lookup6", code) { CurrenciesLookup6().currencies }
            in "EUL".."HBD" -> findCodeBySecondaryCode("Lookup7", code) { CurrenciesLookup7().currencies }
            in "HBSV".."LOKA" -> findCodeBySecondaryCode("Lookup8", code) { CurrenciesLookup8().currencies }
            in "LOOKS".."NPXS" -> findCodeBySecondaryCode("Lookup9", code) { CurrenciesLookup9().currencies }
            in "NU".."QSP" -> findCodeBySecondaryCode("Lookup10", code) { CurrenciesLookup10().currencies }
            in "QTF".."SBZD" -> findCodeBySecondaryCode("Lookup11", code) { CurrenciesLookup11().currencies }
            in "SCAD".."SJOD" -> findCodeBySecondaryCode("Lookup12", code) { CurrenciesLookup12().currencies }
            in "SJPY".."SPKR" -> findCodeBySecondaryCode("Lookup13", code) { CurrenciesLookup13().currencies }
            in "SPLN".."SWRV" -> findCodeBySecondaryCode("Lookup14", code) { CurrenciesLookup14().currencies }
            in "SWST".."USDCLEGACY" -> findCodeBySecondaryCode("Lookup15", code) { CurrenciesLookup15().currencies }
            in "USDE".."XDB" -> findCodeBySecondaryCode("Lookup16", code) { CurrenciesLookup16().currencies }
            in "XDC".."pWING" -> findCodeBySecondaryCode("Lookup17", code) { CurrenciesLookup17().currencies }
            in "pogai".."xDAI" -> findCodeBySecondaryCode("Lookup18", code) { CurrenciesLookup18().currencies }
            else -> ""
        }
    }

    private fun findCodeBySecondaryCode(key: String, code: String, block: () -> Map<String, String>): String =
        cache.get(key) { block() }.findCodeBySecondaryCode(code)
}
