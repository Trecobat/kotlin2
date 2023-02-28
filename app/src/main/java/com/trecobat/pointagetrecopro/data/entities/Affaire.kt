package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "affaires")
data class Affaire(
    @PrimaryKey val aff_id: Int,
    @Embedded val conducteur: UsersTreco?,
    @Embedded val site: Site?,
    @Embedded val client: Client?
)
