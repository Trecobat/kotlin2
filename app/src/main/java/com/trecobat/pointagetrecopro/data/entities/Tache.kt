package com.trecobat.pointagetrecopro.data.entities

import androidx.room.*
import kotlin.String

@Entity( tableName = "taches" )
data class Tache(
    @PrimaryKey val id: Int = 0,
    var text: String? = "",
    var start_date: String? = "",
    val end_date: String? = "",
    val duration: Int? = 1,
    var hidden: Int? = 0,
    val nb_pointage: Int = 0,
    val nb_termine: Int = 0,
    @Embedded var equipe: Equipe? = null,
    @Embedded var affaire: Affaire,
    @Embedded var bdc_type: BdcType
)
