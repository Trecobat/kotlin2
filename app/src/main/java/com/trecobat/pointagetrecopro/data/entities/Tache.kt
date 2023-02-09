package com.trecobat.pointagetrecopro.data.entities

import androidx.room.*

@Entity( tableName = "taches" )
data class Tache(
    @PrimaryKey val id: Int,
    val text: String,
    val start_date: String,
    val end_date: String?,
    val duration: Int,
    val progress: Float,
    val parent: Int,
    val sortorder: Int,
    @ColumnInfo(name = "tache_ut_imaj_uid" ) val ut_imaj_uid: String,
    @ColumnInfo(name = "tache_aff_id" ) val aff_id: Int,
    @ColumnInfo(name = "tache_bdct_id" ) val bdct_id: String,
    val type: String,
    val planned_start: String?,
    val planned_end: String?,
    val is_modele: Int?,
    val type_modele: String?,
    val class_livraison: String?,
    val modif_ts: String?,
    val deleted: String?,
    val lock: Int,
    val prevalide_michabil: Int?,
    val valide_michabil: Int?,
    val valide_usine: Int?,
    val actif: Int?,
//    @ColumnInfo(name = "tache_protected" ) val protected: Boolean, Le mot protected est reservé donc impossible de build avec
    val modele_id: Int,
    val view_at: String?,
    val hidden: Int?,
    val start_date_forced: String?,
    val nb_pointage: Int = 0,
    val pt_tache_id: Int?,
    val nb_termine: Int = 0,
    @Embedded val equipe: Equipe,
    @Embedded val affaire: Affaire,
    @Embedded val bdc_type: BdcType
)
