package ru.hihit.cobuy.currency

import android.icu.text.NumberFormat
import android.icu.util.ULocale
import java.util.Locale
import android.icu.util.Currency as IcuCurrency

object CurrencyConverter {
    fun convertFromRub(
        amountRub: Double?,
        toRate: Double
    ): Double = if (amountRub == null) {
        0.0
    } else {
        amountRub / toRate
    }

    fun convertToRub(
        amountForeign: Double?,
        fromRate: Double
    ): Double = if (amountForeign == null) {
        0.0
    } else {
        amountForeign * fromRate
    }


    fun format(
        amount: Double,
        currencyCode: String,
        locale: Locale = Locale.getDefault()
    ): String {
        val uLocale = ULocale.forLocale(locale)
        val cur = IcuCurrency.getInstance(currencyCode)
        val fmt = NumberFormat.getCurrencyInstance(uLocale)
        fmt.currency = cur
        return fmt.format(amount)
    }

    fun getSymbol(
        code: String
    ): String {
        val locale = Locale.getDefault()
        return IcuCurrency.getInstance(code).getSymbol(ULocale.forLocale(locale))
    }
}