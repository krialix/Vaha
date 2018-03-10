package com.vaha.android.di.module

import com.squareup.moshi.Moshi
import com.vaha.android.BuildConfig
import com.vaha.android.data.api.VahaService
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object NetModule {

    @Singleton
    @Provides
    @JvmStatic
    fun proideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().build()
    }

    @Singleton
    @Provides
    @JvmStatic
    fun provideYolooService(okHttpClient: OkHttpClient, moshi: Moshi): VahaService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .client(okHttpClient)
            .build()
            .create(VahaService::class.java)
    }

    /*@Provides
    @JvmStatic
    fun provideAuthorization(): HttpRequestInitializer {
        return HttpRequestInitializer {
            var headers: HttpHeaders? = it.headers
            if (headers == null) {
                headers = HttpHeaders()
                it.headers = headers
            }

            val tokenResult = FirebaseAuth.getInstance().currentUser?.getIdToken(false)
            tokenResult?.let {
                val getTokenResult = Tasks.await(it)
                val idToken = getTokenResult.token
                //Timber.d("Id token: %s", idToken)
                headers.put("Authorization", listOf("Bearer " + idToken))
            }
        }
    }

    @Provides
    @JvmStatic
    @Singleton
    fun provideVahaApi(httpRequestInitializer: HttpRequestInitializer): VahaApi {
        return VahaApi.Builder(
            AndroidHttp.newCompatibleTransport(),
            JacksonFactory.getDefaultInstance(),
            httpRequestInitializer
        )
            .setRootUrl(BuildConfig.SERVER_URL)
            .setGoogleClientRequestInitializer {
                val enableGZip = BuildConfig.SERVER_URL.startsWith("https:")
                it.disableGZipContent = !enableGZip
            }
            .setApplicationName("vahaApi")
            .build()
    }*/
}