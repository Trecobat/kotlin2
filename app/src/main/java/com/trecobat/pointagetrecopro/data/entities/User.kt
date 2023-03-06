package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.String

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val password: String
)
