package com.trecobat.pointagetrecopro.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.trecobat.pointagetrecopro.data.entities.*

@Database(entities = [
    Affaire::class,
    BdcType::class,
    Client::class,
    Departement::class,
    Equipe::class,
    Pointage::class,
    Site::class,
    Tache::class,
    UsersTreco::class,
    GedFiles::class
], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pointageDao(): PointageDao
    abstract fun chantierDao(): TacheDao
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