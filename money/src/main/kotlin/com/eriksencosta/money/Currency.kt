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

package com.eriksencosta.money

import com.eriksencosta.money.Currency.Factory.of
import com.eriksencosta.money.CustomCurrency.CustomCurrencyConfig
import com.eriksencosta.money.caching.Cache
import com.eriksencosta.money.caching.CacheConfig
import com.eriksencosta.money.caching.DefaultCache
import com.eriksencosta.money.caching.NoCache
import com.eriksencosta.money.caching.createCache
import com.eriksencosta.money.currency.CurrencyResolution
import java.util.Objects.hash

/**
 * Represents a currency, a system of money in common use within a specific environment over time.
 */
public sealed class Currency protected constructor(
    /**
     * The code of the currency.
     */
    public val code: String,

    /**
     * The secondary code of the currency
     */
    public val secondaryCode: String,

    /**
     * The name of the currency.
     */
    public val name: String,

    /**
     * The graphical symbol of the currency.
     */
    public val symbol: String,

    /**
     * The type of the currency.
     */
    public open val type: CurrencyType,

    /**
     * The minor units (i.e., the decimal places) of the currency.
     */
    public val minorUnits: Int
) : Comparable<Currency> {
    override fun compareTo(other: Currency): Int = when (val comparison = code.compareTo(other.code)) {
        0 -> minorUnits.compareTo(other.minorUnits)
        else -> comparison
    }

    override fun equals(other: Any?): Boolean = (other as Currency).let {
        code == it.code &&
            secondaryCode == it.secondaryCode &&
            name == it.name &&
            symbol == it.symbol &&
            type == it.type &&
            minorUnits == it.minorUnits
    }

    override fun hashCode(): Int = hash(code, secondaryCode, name, symbol, type.toString(), minorUnits)

    override fun toString(): String = code

    /**
     * A [Currency] factory.
     */
    public companion object Factory {
        private var cache: Cache<Currency> = createDefaultCache()
            set(custom) = synchronized(this) {
                field = when {
                    custom is DefaultCache -> custom
                    field is DefaultCache && !field.isInitialized() -> custom
                    else -> error("The factory cache can't be replaced once it is configured or initialized")
                }
            }

        /**
         * Creates a [StandardizedCurrency] for a standardized circulating currency or cryptocurrency.
         *
         * @param[code] The code or secondary code of the currency.
         * @throws[IllegalArgumentException] When no currency is found for [code].
         */
        public infix fun of(code: String): StandardizedCurrency = cachedCurrency(code) {
            CurrencyResolution.thorough.resolve(code)
        }

        /**
         * Creates a [CirculatingCurrency] for a standardized circulating currency.
         *
         * @param[code] The code or secondary code of the currency.
         * @throws[IllegalArgumentException] When no currency is found for [code].
         */
        public infix fun circulating(code: String): CirculatingCurrency = cachedCurrency(code) {
            CurrencyResolution.circulating.resolve(code) as CirculatingCurrency
        }

        /**
         * Creates a [CryptoCurrency] for a standardized cryptocurrency.
         *
         * @param[code] The code or secondary code of the currency.
         * @throws[IllegalArgumentException] When no currency is found for [code].
         */
        public infix fun crypto(code: String): CryptoCurrency = cachedCurrency(code) {
            CurrencyResolution.crypto.resolve(code) as CryptoCurrency
        }

        /**
         * Creates a [CustomCurrency].
         *
         * @param[code] The code of the currency.
         * @param[minorUnits] The minor units (i.e., the decimal places) of the currency.
         * @throws[IllegalArgumentException] When [minorUnits] is lower than a zero.
         */
        public fun custom(code: String, minorUnits: Int): CustomCurrency = custom(code, minorUnits) {}

        /**
         * Creates a [CustomCurrency].
         *
         * @param[code] The code of the currency.
         * @param[minorUnits] The minor units (i.e., the decimal places) of the currency.
         * @param[config] An optional block to configure the [CustomCurrency] object.
         * @throws[IllegalArgumentException] When [minorUnits] is lower than a zero.
         */
        public fun custom(code: String, minorUnits: Int, config: CustomCurrencyConfig.() -> Unit): CustomCurrency =
            CustomCurrencyConfig(code, minorUnits).apply(config).let {
                cachedCurrency("$code/${it.type}/$minorUnits/${it.hash()}") {
                    CustomCurrency(it.code, it.secondaryCode, it.name, it.symbol, it.type, it.minorUnits)
                }
            }

        /**
         * Configures the [Factory] cache. The method must be called before the cache is initialized (i.e., before any
         * call to the [of] method) and should be called once.
         *
         * @param[config] A configuration block to set up the cache.
         * @throws[IllegalStateException] When the cache was previously initialized or configured.
         */
        internal fun configureCache(config: CacheConfig.() -> Unit) { cache = createCache(config) }

        /**
         * Disables the [Factory] cache. The method must be called before the cache is initialized (i.e., before any
         * call to the [of] method) and should be called once.
         *
         * @throws[IllegalStateException] When the cache was previously initialized or configured.
         */
        internal fun disableCache() { cache = NoCache() }

        internal fun resetCache() = cache.clean().also {
            cache = createDefaultCache()
        }

        private fun createDefaultCache(): Cache<Currency> = DefaultCache(createCache())

        @Suppress("UNCHECKED_CAST")
        private fun <T : Currency> cachedCurrency(key: String, block: () -> T): T = cache.get(key, block) as T
    }
}

/**
 * Represents the type of [Currency].
 */
public sealed interface CurrencyType

/**
 * Represents a standardized currency. A [StandardizedCurrency] can be created with an ISO 4217 code (e.g., `USD` for
 * the United States Dollar, `EUR` for the Euro, and so on) or with an ISO 24165 (Digital Token Identifier, e.g.,
 * `4H95J0R2X` for Bitcoin and `X9J9K872S` for Ethereum) through the [Currency.of] method.
 */
public sealed class StandardizedCurrency protected constructor(
    code: String,
    secondaryCode: String,
    name: String,
    symbol: String,
    type: CurrencyType,
    minorUnits: Int
) : Currency(code, secondaryCode, name, symbol, type, minorUnits) {
    /**
     * Creates a [CustomCurrency] based on this instance data.
     *
     * @param[minorUnits] The minor units (i.e., the decimal places) for the custom currency.
     * @throws[IllegalArgumentException] When [minorUnits] is lower than zero.
     */
    public fun toCustomCurrency(minorUnits: Int): CustomCurrency = let { source ->
        custom(code, minorUnits) {
            secondaryCode = source.secondaryCode
            name = source.name
            symbol = source.symbol
            type = source.type
        }
    }
}

/**
 * Represents a circulating currency. De facto currencies, albeit not ISO-standardized, are also available (e.g., `CNH`
 * for Hong Kong's Renminbi).
 *
 * Besides the currently standardized ISO 4217 currencies, historical currencies can also be created (e.g., `FRF` for
 * the French Franc, `ITL` for the Italian Lira, `SUR` the for Soviet Union Rouble, and so on).
 */
public class CirculatingCurrency internal constructor(
    code: String,
    secondaryCode: String,
    name: String,
    symbol: String,
    type: Type,
    minorUnits: Int
) : StandardizedCurrency(code, secondaryCode, name, symbol, type, minorUnits) {
    /**
     * Represents the type of [CirculatingCurrency].
     */
    public enum class Type : CurrencyType {
        /**
         * Represents a currency that courts of law are required to recognize as satisfactory payment for any monetary
         * debt.
         */
        TENDER,

        /**
         * Represents a supranational currency and resources similar to currencies like precious metals (e.g., gold,
         * silver, palladium, and platinum).
         */
        OTHER,

        /**
         * Represents an inactive currency.
         */
        HISTORICAL
    }

    override fun equals(other: Any?): Boolean = this === other || (other is CirculatingCurrency && super.equals(other))

    override fun hashCode(): Int = super.hashCode()
}

/**
 * Represents a cryptocurrency (also known as Digital Token).
 */
public class CryptoCurrency internal constructor(
    code: String,
    secondaryCode: String,
    name: String,
    symbol: String,
    type: Type,
    minorUnits: Int
) : StandardizedCurrency(code, secondaryCode, name, symbol, type, minorUnits) {
    /**
     * Represents the type of [CryptoCurrency].
     */
    public enum class Type : CurrencyType {
        /**
         * Represents a cryptocurrency with a privileged position in a distributed ledger technology protocol (e.g.,
         * Bitcoin or Ethereum).
         */
        NATIVE,

        /**
         * Represents a cryptocurrency created as an application on a distributed ledger (e.g., ERC-20 tokens).
         */
        AUXILIARY,

        /**
         * Represents a cryptocurrency being registered in the Digital Token Identifier Foundation database. The
         * cryptocurrency may or may not be registered.
         */
        PROVISIONAL,

        /**
         * Represents a cryptocurrency that is inactive.
         */
        HISTORICAL
    }

    override fun equals(other: Any?): Boolean = this === other || (other is CryptoCurrency && super.equals(other))

    override fun hashCode(): Int = super.hashCode()
}

/**
 * Represents a custom currency identified by a code. This object can be used to represent:
 *
 * * A currency that is not standardized (e.g., new cryptocurrencies or currencies from unrecognized or partially
 *   recognized countries)
 * * A standardized currency with enough minor units (i.e., decimal places) to model amounts like exchange rates and
 *   fuel prices
 */
public class CustomCurrency internal constructor(
    code: String,
    secondaryCode: String,
    name: String,
    symbol: String,
    type: CurrencyType,
    minorUnits: Int
) : Currency(code, secondaryCode, name, symbol, type, minorUnits) {
    /**
     * Represents the type of [CustomCurrency].
     */
    public enum class Type : CurrencyType {
        /**
         * Represents the default type for a [CustomCurrency].
         */
        CUSTOM
    }

    /**
     * Configuration options for a [CustomCurrency] used by the [Currency.custom] method.
     */
    public class CustomCurrencyConfig internal constructor(
        /**
         * The code of the currency.
         */
        public val code: String,

        /**
         * The minor units (i.e., the decimal places) of the currency.
         */
        public val minorUnits: Int
    ) {
        /**
         * The secondary code of the currency.
         */
        public var secondaryCode: String = ""

        /**
         * The name of the currency.
         */
        public var name: String = ""

        /**
         * The graphical symbol of the currency.
         */
        public var symbol: String = code

        /**
         * The type of the currency.
         */
        public var type: CurrencyType = Type.CUSTOM

        internal fun hash(): Int = hash(code, secondaryCode, name, symbol, type.toString(), minorUnits)
    }

    init {
        require(minorUnits >= 0) {
            "The currency minor units must be greater than or equal 0"
        }
    }

    override fun equals(other: Any?): Boolean = this === other || (other is CustomCurrency && super.equals(other))

    override fun hashCode(): Int = super.hashCode()

    override fun toString(): String = "$code[$minorUnits]"
}
