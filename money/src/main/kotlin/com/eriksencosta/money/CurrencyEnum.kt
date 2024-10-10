package com.eriksencosta.money

import com.eriksencosta.math.percentage.Percentage

public interface CurrencyCode {
    public val currency: Currency
    public fun zero(): Money = currency.zero()
    public infix fun money(amount: Number): Money = Money.of(amount, currency)
}

public enum class CirculatingCurrencyCode : CurrencyCode {
    BRL {
        override val currency: CirculatingCurrency get() = of("BRL")
    },
    EUR {
        override val currency: CirculatingCurrency get() = of("EUR")
    },
    USD {
        override val currency: CirculatingCurrency get() = of("USD")
    },
    TRY {
        override val currency: CirculatingCurrency get() = of("TRY")
    },
    JPY {
        override val currency: CirculatingCurrency get() = of("JPY")
    };

    public val zero: Money get() = currency.zero()
    protected fun of(code: String): CirculatingCurrency = Currency.circulating(code)
}

public enum class CryptoCurrencyCode : CurrencyCode {
    BTC {
        override val currency: CryptoCurrency get() = of("BTC")
    },
    ETH {
        override val currency: CryptoCurrency get() = of("ETH")
    };

    protected fun of(code: String): CryptoCurrency = Currency.crypto(code)
}

public infix fun Number.money(currency: CurrencyCode): Money = currency money this

public val Number.percent: Percentage get() = Percentage.of(this)