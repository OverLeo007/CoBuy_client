package ru.hihit.cobuy.currency

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.hihit.cobuy.currency.data.CurrencyRepository
import java.util.Locale


class CurrencyViewModel(
    private val repository: CurrencyRepository,
    private val prefManager: CurrencyPreferenceManager,
) : ViewModel() {
    private val _rates: StateFlow<Map<String, Double>> = repository.rates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
    val rates: StateFlow<Map<String, Double>> get() = _rates

    private val _selectedCurrency: StateFlow<String> = prefManager.selectedCurrencyFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "RUB")
    val selectedCurrency: StateFlow<String> get() = _selectedCurrency

    val currentSymbol: String
        get() {
            return CurrencyConverter.getSymbol(_selectedCurrency.value)
        }

    val currentRateToRub: Double
        get() {
            return _rates.value[selectedCurrency.value] ?: 1.0
        }

    init {
        viewModelScope.launch { repository.refresh() }
    }

    fun onCurrencySelected(code: String) = viewModelScope.launch {
        prefManager.setSelected(code)
        val rate = _rates.value[code] ?: 1.0
        val one = toRub(1.0)
        val thousand = toRub(1000.0)
        Log.d(
            "CurrencyViewModel",
            "Выбрана валюта: $code, курс к рублю: $rate\n" +
                    "1 $currentSymbol = $rate RUB, " +
                    "1000 $currentSymbol = ${toRub(1000.0)} RUB"
        )

    }

    fun toRub(amount: Double?): Double {
        return CurrencyConverter.convertToRub(amount, currentRateToRub)
    }

    fun toRub(amount: Double?, code: String): Double {
        return CurrencyConverter.convertToRub(amount, getRateFromCode(code))
    }

    fun fromRub(amount: Double?): Double {
        return CurrencyConverter.convertFromRub(amount, currentRateToRub)
    }

    fun fromRub(amount: Double?, code: String): Double {
        return CurrencyConverter.convertFromRub(amount, getRateFromCode(code))
    }


    fun format(amount: Double, code: String): String  {
        return "${String.format(Locale.getDefault(), "%.2f",amount)}${CurrencyConverter.getSymbol(code)}"
    }

    private fun getRateFromCode(code: String): Double {
        return _rates.value[code] ?: 1.0
    }
}

