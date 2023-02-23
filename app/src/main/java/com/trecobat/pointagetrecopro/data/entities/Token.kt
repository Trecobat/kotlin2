package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "token")
data class Token(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val token: String? = null,
    val message: String? = null
)
