package com.trecobat.pointagetrecopro.data.entities

import kotlin.String

data class PostTache(
    var text: String,
    var start_date: String,
    var duration: Int = 1,
    var bdct_label: String,
    var aff_id: Int
)
