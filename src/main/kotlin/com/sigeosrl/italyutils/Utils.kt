@file:JvmName("UtilsKt")
@file:Since("2026-02")
@file:Suppress("unused")

package com.sigeosrl.italyutils

import dev.tommasop1804.kutils.LocalDate
import dev.tommasop1804.kutils.MonthDay
import dev.tommasop1804.kutils.String2
import dev.tommasop1804.kutils.annotations.Since
import dev.tommasop1804.kutils.isNotNull
import java.time.MonthDay
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor

/**
 * Determines the Italian holiday corresponding to the date represented by this `TemporalAccessor`.
 *
 * The function checks for holidays based on predefined fixed dates and computes the dates
 * for Easter and Easter Monday for a given year. If the date matches a holiday, it returns
 * the name of the holiday in both Italian and English.
 *
 * It's possible that there's more than one Italian name. In that case, you can separate them with [com.sigeosrl.utils.splitAndTrim]
 * using the '/' character.
 *
 * @return A `String2` representing the holiday name in Italian (first) and English (second), or `null` if
 *         the date does not correspond to any Italian holiday.
 * @throws java.time.temporal.UnsupportedTemporalTypeException if cannot define year, month and day from the given date.
 * @since 2026-02
 */
val TemporalAccessor.italianHoliday: String2?
    get() {
        val year = get(ChronoField.YEAR)
        val date = MonthDay(get(ChronoField.MONTH_OF_YEAR), get(ChronoField.DAY_OF_MONTH))
        val constants = when (date) {
            MonthDay(1, 1) -> "Capodanno" to "New year"
            MonthDay(1, 6) -> "Epifania" to "Epiphany"
            MonthDay(4, 25) -> "Festa della Liberazione" to "Liberation Day"
            MonthDay(5, 1) -> "Festa dei Lavoratori" to "Labor Day"
            MonthDay(6, 2) -> "Festa della Repubblica" to "Republic Day"
            MonthDay(8, 15) -> "Ferragosto / Assuzione" to "Assumption Day"
            MonthDay(11, 1) -> "Ognissanti" to "All saints"
            MonthDay(12, 8) -> "Immacolata Concezione" to "Immaculate Conception"
            MonthDay(12, 25) -> "Natale" to "Christmas"
            MonthDay(12, 26) -> "Santo Stefano" to "St Stephen"
            else -> null
        }
        if (constants.isNotNull()) return constants
        if (date == MonthDay(10, 4) && year >= 2026) return "San Francesco d'Assisi" to "St Francis of Assisi"

        fun getEasterDate(year: Int): MonthDay {
            val a = year % 19
            val b = year / 100
            val c = year % 100
            val d = b / 4
            val e = b % 4
            val f = (b + 8) / 25
            val g = (b - f + 1) / 3
            val h = (19 * a + b - d - g + 15) % 30
            val i = c / 4
            val k = c % 4
            val l = (32 + 2 * e + 2 * i - h - k) % 7
            val m = (a + 11 * h + 22 * l) / 451
            val month = (h + l - 7 * m + 114) / 31
            val day = ((h + l - 7 * m + 114) % 31) + 1
            return MonthDay(month, day)
        }
        val easterDate = getEasterDate(year)
        if (date == easterDate) return "Pasqua" to "Easter"
        if (date == LocalDate(year, easterDate.month, easterDate.dayOfMonth).plusDays(1))
            return "Lunedì dell'Angelo / Pasquetta" to "Easter Monday"
        return null
    }
/**
 * Checks if the temporal object represents an Italian public holiday.
 *
 * The method uses the `getItalianHoliday` function to determine if the date corresponds to a public holiday in Italy.
 * These include fixed-date holidays such as "Capodanno" (New Year's Day) and "Natale" (Christmas),
 * as well as variable holidays like "Pasqua" (Easter) and "Lunedì dell'Angelo" (Easter Monday).
 * Italian holidays such as "San Francesco d'Assisi" are considered when applicable by year-based conditions.
 *
 * @receiver The temporal object, such as a `LocalDate`, to be checked.
 * @return `true` if the date corresponds to an Italian public holiday, `false` otherwise.
 * @throws java.time.temporal.UnsupportedTemporalTypeException if cannot define year, month and day from the given date.
 * @since 2026-02
 */
val TemporalAccessor.isItalianHoliday: Boolean
    get() = italianHoliday.isNotNull()