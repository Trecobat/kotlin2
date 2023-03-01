package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.String

@Entity( tableName = "bdc_type" )
data class BdcType(
    @PrimaryKey val bdct_num_id: Int,
    val bdct_id: String,
    val bdct_label: String,
    val bdct_type: String,
    val bdct_ch_choix: String,
    val bdct_cor_id: Int,
    val bdct_ordre: Int,
    val bdct_category: String,
    val bdct_u_id: String,
    val bdct_ts: String,
    val bdct_actif: Int,
    val bdct_action: String,
    val bdct_default: Int,
    val bdct_moma: String,
    val bdct_hello_hidden: Int,
    val bdct_no_bilan: Int,
    val bdct_couleur: String,
    val bdct_label_short: String,
    val bdct_bdct_linked: String,
    val bdct_delais_bdct_linked: Int,
    val bdct_id_categorie_vp: Int,
    val bdct_metier: String,
    val bdct_materiaux_associe: String,
    val bdct_factures_cat_id: Int?,
    val bdct_nb_jours_theorique_chantier: String?
)
