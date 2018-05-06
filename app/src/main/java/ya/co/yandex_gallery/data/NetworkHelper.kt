package ya.co.yandex_gallery.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ya.co.yandex_gallery.util.AppConstants
import java.util.concurrent.TimeUnit


class NetworkHelper {
    companion object {
        fun getRetrofit(): Retrofit {
            return Retrofit.Builder()
                    .baseUrl(AppConstants.API_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(getOkHttpClient())
                    .build()
        }

        fun getOkHttpClient(): OkHttpClient {
            //todo: add offlineCacheControlInterceptor() &&  .cache(httpCache)
            return OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .addInterceptor(YandexApiInterceptor())
                    .build()
        }

        private class YandexApiInterceptor: Interceptor {
            override fun intercept(chain: Interceptor.Chain?): Response {
                val originalRequest = chain!!.request()
                val request = originalRequest
                        .newBuilder()
                        .addHeader("Authorization", "OAuth " + AppConstants.getAccessToken())
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                        .build()
                return chain.proceed(request)
            }

        }
    }
}