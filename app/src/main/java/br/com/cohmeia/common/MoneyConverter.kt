package br.com.cohmeia.common

import java.lang.Exception
import java.text.NumberFormat
import java.util.*

object MoneyConverter {

    fun formatValueInCents(valueInCents: Int): String {
        val localeDefault = Locale("pt", "BR")

        val currency = NumberFormat.getCurrencyInstance(localeDefault)
            .currency
            .getSymbol(localeDefault)
            .trim()

        val number = NumberFormat.getCurrencyInstance(localeDefault)
            .format(valueInCents / 100.0)
            .replace(currency, "")
            .trim()

//        return "$currency $number"
        return "$ $number"
    }

    fun stringValueToInt(value: String): Int {
        val replace = value.replace(Regex("[$,. ]"), "")
        return try {
            replace.toInt()
        } catch (e: Exception) {
            0
        }
    }

}