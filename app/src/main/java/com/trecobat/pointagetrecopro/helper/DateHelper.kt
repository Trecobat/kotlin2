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
    }
}