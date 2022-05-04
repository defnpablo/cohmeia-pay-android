package br.com.cohmeia.common

import org.junit.Assert.*
import org.junit.Test

class MoneyConverterTest {

    private val currency = "$"

    @Test
    fun `value zero`() {
        val formatted = MoneyConverter.formatValueInCents(0)
        assertEquals("$currency 0,00", formatted)
    }

    @Test
    fun `value one cent`() {
        val formatted = MoneyConverter.formatValueInCents(1)
        assertEquals("$currency 0,01", formatted)
    }

    @Test
    fun `value ten cents`() {
        val formatted = MoneyConverter.formatValueInCents(10)
        assertEquals("$currency 0,10", formatted)
    }

    @Test
    fun `value with decimal and cents`() {
        val formatted = MoneyConverter.formatValueInCents(1234)
        assertEquals("$currency 12,34", formatted)
    }

    @Test
    fun `value above one thousand`() {
        val formatted = MoneyConverter.formatValueInCents(123456)
        assertEquals("$currency 1.234,56", formatted)
    }

}