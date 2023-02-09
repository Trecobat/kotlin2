package com.trecobat.pointagetrecopro.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity( tableName = "users_treco" )
data class UsersTreco(
    @PrimaryKey val ut_imaj_uid: String
)
