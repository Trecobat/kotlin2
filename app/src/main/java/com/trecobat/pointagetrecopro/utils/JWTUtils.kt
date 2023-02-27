package com.trecobat.pointagetrecopro.utils

import android.util.Base64
import android.util.Log
import timber.log.Timber
import java.io.UnsupportedEncodingException

class JWTUtils {
    companion object {
        @Throws(Exception::class)
        fun decoded(JWTEncoded: String): String? {
            return try {
                val split = JWTEncoded.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                Timber.e("JWT_DECODED : Header => ${getJson(split[0])}")
                Timber.e("JWT_DECODED : Body => ${getJson(split[1])}")
                getJson(split[1])
            } catch (e: UnsupportedEncodingException) {
                Timber.e("Error decoding token : ${e.message}")
                null
            }
        }

        @Throws(UnsupportedEncodingException::class)
        fun getJson(strEncoded: String): String {
            val decodedBytes: ByteArray = Base64.decode(strEncoded, Base64.URL_SAFE)
            return String(decodedBytes, Charsets.UTF_8)
        }
    }
}