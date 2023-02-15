package com.trecobat.pointagetrecopro.helper

import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    companion object {
        // Formate une date en en date fr
        fun formatDate(date: String?): String? {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.FRANCE)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
            val textToDate = date?.let { inputFormat.parse(it) }

            return textToDate?.let { outputFormat.format(it) }
        }

        fun getDateTime(twoDigits: Boolean = false): String {
            return "${getDate(twoDigits)} ${getHour()}"
        }

        fun getDate(twoDigits: Boolean = false): String {
            return "${getYear(twoDigits)}-${getMonth()}-${getDay()}"
        }

        fun getDay(): String {
            val calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            return if ( day < 10 ) "0$day" else "$day"
        }

        fun getMonth(): String {
            val calendar = Calendar.getInstance()
            val month = calendar.get(Calendar.MONTH)

            return if ( month < 10 ) "0$month" else "$month"
        }

        fun getYear(twoDigits: Boolean = false): String {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)

            return if ( twoDigits ) year.toString().substring(2) else "$year"
        }

        fun getTime(): String {
            return "${getHour()}:${getMinute()}:${getSecond()}"
        }

        fun getHour(): Int {
            val calendar = Calendar.getInstance()
            return calendar.get(Calendar.HOUR_OF_DAY)
        }

        fun getMinute(): Int {
            val calendar = Calendar.getInstance()
            return calendar.get(Calendar.MINUTE)
        }

        fun getSecond(): Int {
            val calendar = Calendar.getInstance()
            return calendar.get(Calendar.SECOND)
        }
    }
}