package org.aerovek.chartr.util

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.*

val MONTH_NAME_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM")
val DAY_OF_MONTH_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd")
val HOUR_MINUTE_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm")

/** Syntax sugar to allow safe call operator `?.` chaining formatting on [LocalDateTime]/[LocalDate]/[LocalTime]/[ZonedDateTime]/etc */
fun TemporalAccessor?.formattedWith(formatter: DateTimeFormatter): String? {
    if (this == null) return null
    return formatter.format(this)
}

fun LocalTime.formattedAmPm(): String {
    return when (getAmPm()) {
        AmPm.Am -> "AM"
        AmPm.Pm -> "PM"
    }
}

fun LocalTime.getAmPm(): AmPm {
    // 0 based - 11 is noon and 23 is midnight
    return when {
        hour <= 11 -> AmPm.Am
        else -> AmPm.Pm
    }
}

enum class AmPm {
    Am,
    Pm
}