package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Entity

@Entity( tableName = "equipe_departement_vp", primaryKeys = [ "eqdvp_eqvp_id", "eqdvp_dep_id" ] )
data class Departement(
    val eqdvp_eqvp_id: Int,
    val eqdvp_dep_id: Int
)
