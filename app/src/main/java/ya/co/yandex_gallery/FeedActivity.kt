package ya.co.yandex_gallery

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.GridView
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
import ya.co.yandex_gallery.model.ImagesResponse


class FeedActivity : AppCompatActivity() {

    private val TAG = "FeedActivity"
    @BindView(R.id.button_load) lateinit var loginButton: Button
    @BindView(R.id.grid_images) lateinit var imagesGrid: GridView

    private lateinit var adapter: ImageFeedAdapter

    @OnClick(R.id.button_load)
    fun OnLoad() {
        Log.d(TAG, "click!")

        val retrofit: YandexDiskApi? = getRetrofit().create(YandexDiskApi::class.java)
        retrofit!!.getImages(20, "image")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( {
                    imagesResponse: ImagesResponse -> Log.d(TAG, "got images!!: " +
                        "${imagesResponse.items.map { image -> image.name }}")
                    val images = imagesResponse.items
                    adapter.addImages(images)
                }, {
                    throwable: Throwable? ->
                    throwable?.printStackTrace()
                })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        ButterKnife.bind(this)

        adapter = ImageFeedAdapter(this)
        imagesGrid.adapter = adapter

//        imagesGrid.setOnClickListener()
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

private fun GridView.setOnClickListener() {
    Log.d("CridView", "grid clicked")
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
