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
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson) : Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api-partenaires.trecobat.fr/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(
                OkHttpClient.Builder().apply {
                    // Ajout de l'intercepteur pour le header Authorization
                    addInterceptor { chain ->
                        val token = System.getProperty("token")
                        val original = chain.request()
                        val requestBuilder = original.newBuilder()
                        if (token != null) {
                            requestBuilder.header("Authorization", "Bearer $token") // Ajouter le header si le token n'est pas null
                        }
                        val request = requestBuilder.method(original.method, original.body).build()
                        chain.proceed(request)
                    }
                }.build()
            )
            .build()
    }

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