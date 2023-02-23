package com.trecobat.pointagetrecopro.data.entities

import androidx.room.*

@Entity( tableName = "taches" )
data class Tache(
    @PrimaryKey val id: Int,
    val text: String,
    val start_date: String,
    val end_date: String?,
    val duration: Int,
    var hidden: Int?,
    val nb_pointage: Int = 0,
    val nb_termine: Int = 0,
    @Embedded val equipe: Equipe,
    @Embedded val affaire: Affaire,
    @Embedded val bdc_type: BdcType
)
