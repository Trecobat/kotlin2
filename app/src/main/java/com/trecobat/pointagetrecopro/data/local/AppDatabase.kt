package com.trecobat.pointagetrecopro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.trecobat.pointagetrecopro.data.entities.Pointage

@Database(entities = [Pointage::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun pointageDao(): PointageDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "pointages")
                .fallbackToDestructiveMigration()
                .build()
    }

}