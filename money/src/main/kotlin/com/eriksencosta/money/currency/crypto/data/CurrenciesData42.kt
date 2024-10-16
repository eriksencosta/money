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

import com.eriksencosta.money.currency.CurrencyData

// 50 currencies
@Suppress("MagicNumber", "MaxLineLength", "StringLiteralDuplication")
internal class CurrenciesData42 {
    val currencies: Map<String, CurrencyData> get() = mapOf(
        "WBQHCRVJ0" to CurrencyData("WBQHCRVJ0", "GUSD", "Gemini dollar", "GUSD", "AUXILIARY", 2),
        "WC147L5M3" to CurrencyData("WC147L5M3", "SOL", "Wrapped SOL", "◎", "AUXILIARY", 9),
        "WC2Q7RPJM" to CurrencyData("WC2Q7RPJM", "AAVE", "Heco-Peg AAVE", "AAVE", "AUXILIARY", 18),
        "WCFP5ZBK5" to CurrencyData("WCFP5ZBK5", "POPCAT", "POPCAT", "POPCAT", "AUXILIARY", 9),
        "WCRQ5K6JB" to CurrencyData("WCRQ5K6JB", "AMC", "DAAG Certificat/Tracker Perp AMC", "AMC", "AUXILIARY", 8),
        "WDW87KTD5" to CurrencyData("WDW87KTD5", "FRP", "FAME REWARD PLUS", "FRP", "AUXILIARY", 18),
        "WF9C3FK6M" to CurrencyData("WF9C3FK6M", "AXL", "Axelar", "AXL", "AUXILIARY", 6),
        "WFJ88ZR2T" to CurrencyData("WFJ88ZR2T", "WBNB", "Wrapped BNB", "WBNB", "AUXILIARY", 18),
        "WFQSW1L5L" to CurrencyData("WFQSW1L5L", "INJ", "Injective", "INJ", "NATIVE", 9),
        "WG4QW2C75" to CurrencyData("WG4QW2C75", "TLM", "Alien Worlds Trilium", "TLM", "AUXILIARY", 4),
        "WGHBLG826" to CurrencyData("WGHBLG826", "FR0014003521", "European Invest/Zero Cpn Bd", "FR0014003521", "AUXILIARY", 0),
        "WGJSRNZGM" to CurrencyData("WGJSRNZGM", "GRT.e", "Graph Token", "GRT.e", "AUXILIARY", 18),
        "WGK57QL5L" to CurrencyData("WGK57QL5L", "BNB-bsc", "Binance Coin", "BNB-bsc", "AUXILIARY", 18),
        "WHHCGRLK1" to CurrencyData("WHHCGRLK1", "IRIS", "IRISnet", "IRIS", "AUXILIARY", 6),
        "WHLCJ41PK" to CurrencyData("WHLCJ41PK", "MATIC", "Matic Token", "MATIC", "AUXILIARY", 18),
        "WJ855ZZMP" to CurrencyData("WJ855ZZMP", "SYN", "Synapse", "SYN", "AUXILIARY", 18),
        "WJG5XV296" to CurrencyData("WJG5XV296", "ECOx", "ECOx", "ECOx", "AUXILIARY", 18),
        "WJJ3P11HW" to CurrencyData("WJJ3P11HW", "SOL", "Wrapped SOL", "◎", "AUXILIARY", 9),
        "WJP8PMDMP" to CurrencyData("WJP8PMDMP", "ASHP", "AFREUM SHP", "ASHP", "AUXILIARY", 7),
        "WK0WPVLMW" to CurrencyData("WK0WPVLMW", "", "BID/2.205 BO 20240721", "", "PROVISIONAL", 0),
        "WK2NPGFKC" to CurrencyData("WK2NPGFKC", "AAUD", "AFREUM AUD", "AAUD", "AUXILIARY", 7),
        "WK55D5NB8" to CurrencyData("WK55D5NB8", "AMAD", "AFREUM MAD", "AMAD", "AUXILIARY", 7),
        "WKLHJRTP2" to CurrencyData("WKLHJRTP2", "AGIX", "SingularityNET Token", "AGIX", "AUXILIARY", 8),
        "WKQ2QXHT4" to CurrencyData("WKQ2QXHT4", "PFE", "DAAG Certificat/Tracker Perp PFE", "PFE", "AUXILIARY", 8),
        "WKZB29MBZ" to CurrencyData("WKZB29MBZ", "ABTN", "AFREUM BTN", "ABTN", "AUXILIARY", 7),
        "WL0JVNMPV" to CurrencyData("WL0JVNMPV", "SETB", "AFREUM STABLE ETB", "SETB", "AUXILIARY", 7),
        "WL8WN1N51" to CurrencyData("WL8WN1N51", "FCT", "[FCT] FirmaChain Token", "FCT", "AUXILIARY", 18),
        "WLGL2R5HW" to CurrencyData("WLGL2R5HW", "AHUF", "AFREUM HUF", "AHUF", "AUXILIARY", 7),
        "WLKHBH10R" to CurrencyData("WLKHBH10R", "LRC", "LoopringCoin V2", "LRC", "AUXILIARY", 18),
        "WMRRN1XWJ" to CurrencyData("WMRRN1XWJ", "GNO", "Gnosis Token on xDai", "GNO", "AUXILIARY", 18),
        "WNNDNL8TD" to CurrencyData("WNNDNL8TD", "UST", "UST", "UST", "AUXILIARY", 6),
        "WPR0Q24WT" to CurrencyData("WPR0Q24WT", "SMMK", "AFREUM STABLE MMK", "SMMK", "AUXILIARY", 7),
        "WQ41Z1VKD" to CurrencyData("WQ41Z1VKD", "WMATIC", "Wrapped Matic", "WMATIC", "AUXILIARY", 18),
        "WQBWTWRCJ" to CurrencyData("WQBWTWRCJ", "CHZ", "chiliZ", "CHZ", "AUXILIARY", 18),
        "WQFH3SWKW" to CurrencyData("WQFH3SWKW", "WBTC", "Wrapped Bitcoin", "WBTC", "AUXILIARY", 8),
        "WQPP9P8HK" to CurrencyData("WQPP9P8HK", "STZS", "AFREUM STABLE TZS", "STZS", "AUXILIARY", 7),
        "WQRD710WG" to CurrencyData("WQRD710WG", "AVAX", "Avalanche", "AVAX", "AUXILIARY", 18),
        "WR5J4ZCFX" to CurrencyData("WR5J4ZCFX", "MKR.e", "Maker", "MKR.e", "AUXILIARY", 18),
        "WR68PGJRQ" to CurrencyData("WR68PGJRQ", "ENJ", "Enjin", "ENJ", "AUXILIARY", 8),
        "WRDZ657V5" to CurrencyData("WRDZ657V5", "USDD", "Decentralized USD", "USDD", "AUXILIARY", 18),
        "WRF47PN5M" to CurrencyData("WRF47PN5M", "AGNF", "AFREUM GNF", "AGNF", "AUXILIARY", 7),
        "WRHGTZZ0H" to CurrencyData("WRHGTZZ0H", "COS", "Cats Of Sol", "COS", "AUXILIARY", 9),
        "WRHZ2RN0V" to CurrencyData("WRHZ2RN0V", "USDT", "Tether USD", "₮", "AUXILIARY", 6),
        "WRPR921TV" to CurrencyData("WRPR921TV", "NOTE", "Note", "NOTE", "AUXILIARY", 18),
        "WS1W9KPHZ" to CurrencyData("WS1W9KPHZ", "FRAX", "Frax", "FRAX", "AUXILIARY", 18),
        "WS6BZ8225" to CurrencyData("WS6BZ8225", "FTM", "Fantom", "FTM", "NATIVE", 18),
        "WS6SFQ5D1" to CurrencyData("WS6SFQ5D1", "ONT", "Poly-Peg ONT", "ONT", "AUXILIARY", 9),
        "WT8RT0Z9J" to CurrencyData("WT8RT0Z9J", "SKL", "SKALE", "SKL", "AUXILIARY", 18),
        "WTQM4HSVG" to CurrencyData("WTQM4HSVG", "UNI", "Uniswap", "UNI", "AUXILIARY", 8),
        "WVBTKS08V" to CurrencyData("WVBTKS08V", "USDC", "USD Coin", "USDC", "AUXILIARY", 6),
    )
}
