package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.String

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey val cli_id: Int,
    val cli_nom: String,
    val cli_prenom: String,
    val cli_adresse1_chantier: String,
    val cli_adresse2_chantier: String?,
    val cli_cp_chantier: String,
    val cli_ville_chantier: String
)
