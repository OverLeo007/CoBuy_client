package ru.hihit.cobuy.currency.data

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate

class CurrencyRepository(
    private val service: CbrXmlService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val _rates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val rates: StateFlow<Map<String, Double>> = _rates.asStateFlow()

    suspend fun refresh(date: LocalDate? = null) {
        Log.d("CurrencyRepository", "refreshed currencies")
        val map = service.fetchRates(date)
        withContext(ioDispatcher) {
            _rates.value = map
        }
    }
}