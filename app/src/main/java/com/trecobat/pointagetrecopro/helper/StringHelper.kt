package com.trecobat.pointagetrecopro.helper

import java.text.Normalizer

class StringHelper {
    companion object {
        private fun removeSpecialChar(string: String): String {
            val regex =  Regex("[^a-zA-Z0-9]+")
            return regex.replace(string, "_")
        }

        private fun removeAccent(string: String): String {
            val regex = Regex("\\p{InCombiningDiacriticalMarks}+")
            val normalized = Normalizer.normalize(string, Normalizer.Form.NFD)
            return regex.replace(normalized, "")
        }

        fun nettoyerChaine(string: String): String {
            return removeSpecialChar ( removeAccent ( string ) )
        }
    }
}