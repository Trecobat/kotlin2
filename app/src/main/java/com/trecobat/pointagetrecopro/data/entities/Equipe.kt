package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity( tableName = "equipe_vp" )
data class Equipe(
    @PrimaryKey val eqvp_id: Int,
    val eqvp_parent: Int,
    val eqvp_nom: String,
    val eqvp_charpente_etancheite: Int?,
    val eqvp_carrelage: Int?,
    val eqvp_menuiseries: Int?,
    val eqvp_mob: Int?,
    val eqvp_isolation: Int?,
    val eqvp_electricite: Int?,
    val eqvp_plomberie: Int?,
    val eqvp_bandes: Int?,
    val eqvp_go: Int?,
    val eqvp_id_vp: Int,
    val eqvp_order: Int,
    val eqvp_login: String,
    val eqvp_admin: Int?,
    val eqvp_admin_dep: Int,
    @Embedded val departement: Departement
)
