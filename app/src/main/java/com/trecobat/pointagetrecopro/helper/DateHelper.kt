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

        fun getTime(): String {
            val calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)

            return "$year-$month-$day $hour:$minute:$second"
        }

        fun getDate(): String {
            val calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            return "$year-$month-$day"
        }

        fun getHour(): String {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            val second = calendar.get(Calendar.SECOND)

            return "$hour:$minute:$second"
        }
    }
}