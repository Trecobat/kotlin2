package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.String

@Entity( tableName = "equipe_vp" )
data class Equipe(
    @PrimaryKey val eqvp_id: Int,
    val eqvp_parent: Int? = 0,
    val eqvp_nom: String? = "",
    val eqvp_charpente_etancheite: Int? = 0,
    val eqvp_carrelage: Int? = 0,
    val eqvp_menuiseries: Int? = 0,
    val eqvp_mob: Int? = 0,
    val eqvp_isolation: Int? = 0,
    val eqvp_electricite: Int? = 0,
    val eqvp_plomberie: Int? = 0,
    val eqvp_bandes: Int? = 0,
    val eqvp_go: Int? = 0,
    val eqvp_id_vp: Int? = 0,
    val eqvp_order: Int? = 0,
    val eqvp_login: String? = "",
    val eqvp_admin: Int? = 0,
    val eqvp_admin_dep: Int? = 0,
    val eqvp_email: String? = "",
    @Embedded val departement: Departement? = null
)
