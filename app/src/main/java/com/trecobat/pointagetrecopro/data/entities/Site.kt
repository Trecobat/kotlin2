package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.String

@Entity(tableName = "sites")
data class Site(
    @PrimaryKey val sit_id: Int,
    val sit_nom: String
)
