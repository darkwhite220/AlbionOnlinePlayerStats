package earth.darkwhite.albiononlineplayerstats.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import earth.darkwhite.albiononlineplayerstats.PreferencesManager
import earth.darkwhite.albiononlineplayerstats.addplayer.AddPlayerRepo
import earth.darkwhite.albiononlineplayerstats.data.api.ApiService
import earth.darkwhite.albiononlineplayerstats.data.api.BASE_URL
import earth.darkwhite.albiononlineplayerstats.database.MyDao
import earth.darkwhite.albiononlineplayerstats.database.MyDatabase
import earth.darkwhite.albiononlineplayerstats.mainscreen.MainScreenRepo
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun providesRetrofits(): Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(providesMoshi()))
        .baseUrl(BASE_URL)
        .client(
            OkHttpClient.Builder()
                .connectTimeout(7, TimeUnit.SECONDS)
                .callTimeout(7, TimeUnit.SECONDS)
                .readTimeout(7, TimeUnit.SECONDS)
                .build()
        )
        .build()

    @Provides
    @Singleton
    fun providesAlbionApi(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun providesMyDatabase(@ApplicationContext context: Context): MyDatabase =
        MyDatabase.getInstance(context)

    @Provides
    @Singleton
    fun providesMyDao(database: MyDatabase): MyDao =
        database.databaseMyDao

    @Provides
    @Singleton
    fun providesMainScreenRepository(dataSource: MyDao, api: ApiService): MainScreenRepo =
        MainScreenRepo(dataSource, api)

    @Provides
    @Singleton
    fun providesAddPLayerRepository(dataSource: MyDao, api: ApiService): AddPlayerRepo =
        AddPlayerRepo(dataSource, api)

    @Provides
    @Singleton
    fun providesPreferencesManager(@ApplicationContext context: Context): PreferencesManager =
        PreferencesManager(context)
}