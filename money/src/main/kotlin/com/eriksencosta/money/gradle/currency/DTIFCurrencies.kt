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

@file:OptIn(ExperimentalSerializationApi::class)

package com.eriksencosta.money.gradle.currency

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNames

internal object DTIFCurrencies : SourceCurrencies() {
    internal data class DTIFCurrency(
        override val code: String,
        override val name: String,
        override val symbol: String,
        override val type: String,
        override val minorUnits: Int,
        val shortCodes: Set<String> = emptySet()
    ) : RawCurrency {
        override val secondaryCode: String get() = shortCodes.elementAtOrElse(0) { "" }
    }

    @Serializable
    private data class DTIFCurrencies(val records: List<DTIFDigitalToken>) {
        private val excludeTypes = setOf("DISTRIBUTED", "FUNGIBLE")

        fun toCurrencies(symbols: Map<String, String>): Map<String, DTIFCurrency> = records
            .filterNot { excludeTypes.contains(it.header.type()) }
            .associate { token ->
                val shortCodes = token.informative.codes.map { it.name.trim() }.toSet()
                val type = when {
                    token.metadata.deleted -> "HISTORICAL"
                    token.metadata.provisional -> "PROVISIONAL"
                    else -> token.header.type()
                }

                token.header.code to DTIFCurrency(
                    token.header.code,
                    token.informative.name.trim(),
                    lookupForSymbol(shortCodes, symbols),
                    type,
                    token.informative.minorUnits(),
                    shortCodes
                )
            }

        private fun lookupForSymbol(shortCodes: Set<String>, symbols: Map<String, String>): String =
            when (val shortCode = shortCodes.find { symbols.containsKey(it) }) {
                is String -> symbols[shortCode]!!
                else -> shortCodes.elementAtOrElse(0) { "" }
            }

        @Serializable
        private data class DTIFDigitalToken(
            @JsonNames("Header")
            val header: DTIFDigitalTokenHeader,

            @JsonNames("Informative")
            val informative: DTIFDigitalTokenInformative,

            @JsonNames("Metadata")
            val metadata: DTIFDigitalTokenMetadata
        )

        @Serializable
        private data class DTIFDigitalTokenHeader(
            @JsonNames("DTI")
            val code: String,

            @JsonNames("DTIType")
            val type: Int,
        ) {
            @Suppress("MagicNumber")
            fun type(): String = when (type) {
                0 -> "AUXILIARY"
                1 -> "NATIVE"
                2 -> "DISTRIBUTED"
                3 -> "FUNGIBLE"
                else -> throw IllegalArgumentException("Invalid \"type\" for DTI record")
            }
        }

        @Serializable
        private data class DTIFDigitalTokenInformative(
            @JsonNames("LongName")
            val name: String,

            @JsonNames("UnitMultiplier")
            val units: String = "",

            @JsonNames("ShortNames")
            val codes: List<ShortCode> = emptyList()
        ) {
            fun minorUnits(): Int = if (units.isNotEmpty()) units.length - 1 else 0

            @Serializable
            data class ShortCode(@JsonNames("ShortName") val name: String)
        }

        @Serializable
        private data class DTIFDigitalTokenMetadata(
            @JsonNames("Provisional")
            @Suppress("BooleanPropertyNaming")
            val provisional: Boolean,

            @JsonNames("Deleted")
            @Suppress("BooleanPropertyNaming")
            val deleted: Boolean = false,
        )
    }

    @Serializable
    private data class Symbol(
        val name: String,

        @JsonNames("symbol")
        val shortCode: String = "",

        @JsonNames("usym")
        val unicodeSymbol: String = ""
    )

    private val format = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val client = HttpClient {
        expectSuccess = true
    }

    private const val TOKENS_URL = "https://download.dtif.org/data.json"
    private const val SYMBOLS_URL = "https://raw.githubusercontent.com/yonilevy/crypto-currency-symbols/master" +
        "/symbols.json"

    // Includes the 24 currencies with the biggest market cap
    // See: https://coinmarketcap.com/
    @Suppress("Indentation")
    override val prioritizedCurrenciesCodes = setOf(
        "4H95J0R2X", // Bitcoin (BTC, XBT)
        "X9J9K872S", // Ethereum (ETH)
                     // Tether (USDT) -- only fungible group and auxiliary DTIs
        "HWRGLMT9T", // Binance Smart Chain (BNB)
        "20J63Z4N3", // Solana (SOL)
                     // USDC -- fungible group and auxiliary
        "L6GTZC9G4", // Ripple (XRP)
        "820B7G1NL", // Dogecoin (DOGE)
        "QBZLT5MT1", // Toncoin (TON)
        "HWGL1C2CK", // Cardano (ADA)
        "993D8X1FB", // TRON (TRX)
        "M3Z631TN4", // Avalanche (AVAX)
                     // Shiba Inu (SHIB) -- only fungible group and auxiliary DTIs
        "P5B46MFPP", // Polkadot (DOT)
                     // Chainlink (LINK) -- only fungible group and auxiliary DTIs
                     // Bitcoin Cash (BCH) -- two native DTIs but no match for token reference URL
        "DC83NXWML", // NEAR Protocol (NEAR)
                     // Leo Token (LEO) -- only fungible group and auxiliary DTIs
                     // Dai (DAI) -- only fungible group and auxiliary DTIs
        "WTX0G7K46", // Litecoin (LTC)
        "RQWW6J6K0", // Polygon Matic (MATIC)
                     // Pepe (PEPE) -- only fungible group and auxiliary DTIs
        "7KL6LCVWQ", // Internet Computer Token (ICP)
                     // Uniswap (UNI) -- only fungible group and auxiliary DTIs
        "ZWK6RM36C", // Kaspa (KAS)
        "GWQWXVV7J", // Ethereum Classic (ETC)
        "SSFZXXTDD", // Aptos Token (APT)
                     // Artificial Superintelligence Alliance (FET)
        "24VV95T9F", // Monero (XMR)
        "C4SRNZD8K", // Stellar Lumen (XLM)
        "C0D7M4H0R", // Mantle (MNT)
        "W7BGSQ91D", // Stacks (STX)
                     // Render (RNDR) -- only fungible group and auxiliary DTIs
                     // dofwifhat (WIF) -- only auxiliary DTI
                     // Maker (MKR) -- only fungible group and auxiliary DTIs
        "K8B662X5Z", // Filecoin (FIL)
                     // OKB (OKB) -- only fungible group and auxiliary DTIs
        "DHQPD433B", // Hedera Hashgraph (HBAR)
    )

    override val currencies: Map<String, DTIFCurrency> by lazy {
        runBlocking { loadTokens().toCurrencies(loadSymbols()) }
    }

    private suspend fun loadSymbols(): Map<String, String> = run {
        val symbols = format.decodeFromString<List<Symbol>>(client.get(SYMBOLS_URL).bodyAsText())
        symbols.associate { it.shortCode to it.unicodeSymbol }.toSortedMap()
    }

    private suspend fun loadTokens(): DTIFCurrencies = run {
        format.decodeFromString<DTIFCurrencies>(client.get(TOKENS_URL).bodyAsText())
    }
}
