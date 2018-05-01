package ya.co.yandex_gallery

import android.content.Context
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Button
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import ya.co.yandex_gallery.util.AppConstants
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import ya.co.yandex_gallery.data.YandexDiskApi


class FeedActivity : AppCompatActivity() {

    private val TAG = "FeedActivity"
    @BindView(R.id.button_load) lateinit var loginButton: Button

    @OnClick(R.id.button_load)
    fun OnLoad() {
        Log.d(TAG, "click!")
        val retrofit: YandexDiskApi? = getRetrofit().create(YandexDiskApi::class.java)
        retrofit!!.getImages(10, "image")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( {
                    images -> Log.d(TAG, "got images!!: $images")
                }, {
                    throwable: Throwable? ->
                    throwable?.printStackTrace()
                })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        ButterKnife.bind(this)

        
//        Log.d(TAG, "token: ${getToken()}")
    }


    //////////////////////todo: make it in Network class? provide as a dependency?
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
