package com.trecobat.pointagetrecopro.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.trecobat.pointagetrecopro.data.local.AppDatabase
import com.trecobat.pointagetrecopro.data.local.TacheDao
import com.trecobat.pointagetrecopro.data.local.PointageDao
import com.trecobat.pointagetrecopro.data.remote.chantier.TacheDataSource
import com.trecobat.pointagetrecopro.data.remote.chantier.TacheService
import com.trecobat.pointagetrecopro.data.remote.pointage.PointageDataSource
import com.trecobat.pointagetrecopro.data.remote.pointage.PointageService
import com.trecobat.pointagetrecopro.data.repository.TacheRepository
import com.trecobat.pointagetrecopro.data.repository.PointageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson) : Retrofit = Retrofit.Builder()
        .baseUrl("https://api-partenaires.trecobat.fr/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) = AppDatabase.getDatabase(appContext)

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    /*********************************************POINTAGE*********************************************************/
    @Provides
    fun providePointageService(retrofit: Retrofit): PointageService = retrofit.create(PointageService::class.java)
    @Singleton
    @Provides
    fun providePointageDataSource(pointageService: PointageService) = PointageDataSource(pointageService)
    @Singleton
    @Provides
    fun providePointageDao(db: AppDatabase) = db.pointageDao()
    @Singleton
    @Provides
    fun providePointageRepository(remoteDataSource: PointageDataSource,
                          localDataSource: PointageDao
    ) =
        PointageRepository(remoteDataSource, localDataSource)
    /*******************************************FIN POINTAGE********************************************************/

    /*********************************************CHANTIER**********************************************************/
    @Provides
    fun provideChantierService(retrofit: Retrofit): TacheService = retrofit.create(TacheService::class.java)
    @Singleton
    @Provides
    fun provideChantierataSource(chantierService: TacheService) = TacheDataSource(chantierService)
    @Singleton
    @Provides
    fun provideChantierDao(db: AppDatabase) = db.chantierDao()
    @Singleton
    @Provides
    fun provideChantierRepository(remoteDataSource: TacheDataSource,
                                  localDataSource: TacheDao
    ) =
        TacheRepository(remoteDataSource, localDataSource)
    /*******************************************FIN CHANTIER********************************************************/
}