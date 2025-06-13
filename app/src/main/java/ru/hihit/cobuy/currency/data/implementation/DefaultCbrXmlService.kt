package ru.hihit.cobuy.currency.data.implementation

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import org.xml.sax.InputSource
import ru.hihit.cobuy.currency.data.CbrXmlService
import java.io.StringReader
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilderFactory

class DefaultCbrXmlService(
    private val baseUrl: String = "https://www.cbr.ru/scripts/XML_daily.asp"
) : CbrXmlService {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override suspend fun fetchRates(date: LocalDate?): Map<String, Double> =
        withContext(Dispatchers.IO) {
            val url = buildString {
                append(baseUrl)
                if (date != null) append("?date_req=${date.format(dateFormatter)}")
            }
            val xml = URL(url).openStream().bufferedReader(Charsets.UTF_8).use { it.readText() }
            val doc = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(InputSource(StringReader(xml)))
            val nodes = doc.getElementsByTagName("Valute")
            val rates = mutableMapOf<String, Double>()

            for (i in 0 until nodes.length) {
                val el = nodes.item(i) as Element
                val code = el.getElementsByTagName("CharCode").item(0).textContent
                val nominal = el.getElementsByTagName("Nominal").item(0).textContent.toInt()
                val value = el.getElementsByTagName("Value").item(0).textContent
                    .replace(',', '.').toDouble()
                rates[code] = value / nominal
            }
            rates["RUB"] = 1.0
            rates.toMap()
        }
}