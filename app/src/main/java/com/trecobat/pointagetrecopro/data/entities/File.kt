package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "files")
data class File(
    @PrimaryKey val fo_id: String,
    val fi_name: String,
    val file_content: String
)
