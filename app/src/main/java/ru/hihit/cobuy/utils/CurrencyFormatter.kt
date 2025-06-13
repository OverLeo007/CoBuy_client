package ru.hihit.cobuy.utils

import android.icu.util.Currency
import android.icu.text.NumberFormat
import android.icu.util.ULocale
import java.util.Locale



fun formatCurrencyICU(amount: Double, currencyCode: String, locale: Locale = Locale.getDefault()): String {
    val uLocale = ULocale.forLocale(locale)
    val currency = Currency.getInstance(currencyCode)
    val formatter = NumberFormat.getCurrencyInstance(uLocale)
    formatter.currency = currency
    return formatter.format(amount)
}

fun getCurrencySymbol(currencyCode: String, locale: Locale = Locale.getDefault()): String {
    val currency = Currency.getInstance(currencyCode)
    return currency.getSymbol(ULocale.forLocale(locale))
}

