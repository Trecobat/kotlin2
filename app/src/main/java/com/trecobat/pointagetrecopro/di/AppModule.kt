package com.trecobat.pointagetrecopro.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.trecobat.pointagetrecopro.data.local.AppDatabase
import com.trecobat.pointagetrecopro.data.local.MyDao
import com.trecobat.pointagetrecopro.data.remote.BaseDataSource
import com.trecobat.pointagetrecopro.data.remote.MyService
import com.trecobat.pointagetrecopro.data.repository.MyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ActivityContext
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

    @Provides
    fun provideService(retrofit: Retrofit): MyService = retrofit.create(MyService::class.java)
    @Singleton
    @Provides
    fun provideDataSource(myService: MyService) = BaseDataSource(myService)
    @Singleton
    @Provides
    fun provideDao(db: AppDatabase) = db.myDao()
    @Singleton
    @Provides
    fun provideRepository(remoteDataSource: BaseDataSource,
                          localDataSource: MyDao
    ) = MyRepository(remoteDataSource, localDataSource)
}