package ru.hihit.cobuy.currency.data

import java.time.LocalDate

interface CbrXmlService {
    suspend fun fetchRates(date: LocalDate? = null): Map<String, Double>
}