package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity( tableName = "ged_files" )
data class GedFiles(
    @PrimaryKey val gdf_fo_id: String,
    val gdf_obj_id: Int,
    val gdf_type: String,
    val gdf_cat_label: String,
    val gdf_tache_id: Int,
    val local_storage: String?
)
