package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity( tableName = "tokenData" )
data class TokenData(
    @PrimaryKey val id: String,
    val email: String,
    val entite: String
)
