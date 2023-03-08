package com.trecobat.pointagetrecopro.helper

import android.view.View
import java.text.SimpleDateFormat
import java.util.*

class DateHelper {
    companion object {
        // Formate une date en en date fr
        fun formatDate(date: String?, inputPattern: String? = "yyyy-MM-dd HH:mm:ss", outputPattern: String? = "dd/MM/yyyy"): String? {
            val inputFormat = SimpleDateFormat(inputPattern, Locale.FRANCE)
            val outputFormat = SimpleDateFormat(outputPattern, Locale.FRANCE)
            val textToDate = date?.let { inputFormat.parse(it) }

            return textToDate?.let { outputFormat.format(it) }
        }

        fun getDateTime(twoDigits: Boolean = false): String {
            return "${getDate(twoDigits)} ${getTime()}"
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
            val month = calendar.get(Calendar.MONTH) + 1

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

        fun getHour(): String {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            return if ( hour < 10 ) "0$hour" else "$hour"
        }

        fun getMinute(): String {
            val calendar = Calendar.getInstance()
            val minute = calendar.get(Calendar.MINUTE)
            return if ( minute < 10 ) "0$minute" else "$minute"
        }

        private fun getSecond(): String {
            val calendar = Calendar.getInstance()
            val second = calendar.get(Calendar.SECOND)
            return if ( second < 10 ) "0$second" else "$second"
        }
    }
}