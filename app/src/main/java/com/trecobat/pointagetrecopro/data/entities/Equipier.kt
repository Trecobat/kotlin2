package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipiers")
data class Equipier(
    @PrimaryKey val eevp_id: Int,
    val eevp_prenom: String?,
    val eevp_nom: String?,
    @Embedded val equipe: Equipe?
)
