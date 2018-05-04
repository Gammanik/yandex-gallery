package ya.co.yandex_gallery

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.widget.AbsListView
import android.widget.GridView
import butterknife.BindView
import butterknife.ButterKnife
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
import java.util.concurrent.TimeUnit


class FeedActivity : AppCompatActivity() {

    companion object {
        private val TAG = "FeedActivity"
        private val ITEMS_PER_PAGE = 20
        //cause there is no such field as "total" in the response!!!!
        private var MAX_ITEM_COUNT = 25*ITEMS_PER_PAGE
    }

    private var currentOffset: Int = 0


    @BindView(R.id.grid_images) lateinit var imagesGrid: GridView
    @BindView(R.id.swipeRefreshFeed) lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var adapter: ImageFeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        ButterKnife.bind(this)

        adapter = ImageFeedAdapter(this)
        imagesGrid.adapter = adapter

        showProgressBar()
        loadImages(currentOffset)

        swipeRefreshLayout.setOnRefreshListener {
            adapter.clearImages()
            currentOffset = 0
            loadImages(currentOffset)
        }

        imagesGrid.setOnScrollListener(object: AbsListView.OnScrollListener {
            override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                if (totalItemCount - visibleItemCount <= firstVisibleItem && adapter.count + ITEMS_PER_PAGE <= MAX_ITEM_COUNT) {
                    showProgressBar()
                    currentOffset += ITEMS_PER_PAGE
                    loadImages(currentOffset)
                    Log.d(TAG, firstVisibleItem.toString())
                }
            }
            override fun onScrollStateChanged(view: AbsListView?, state: Int) {}
        })

    }

    private fun showProgressBar() {
        swipeRefreshLayout.isRefreshing = true
    }

    private fun hideProgressBar() {
        swipeRefreshLayout.isRefreshing = false
    }


    fun loadImages(offset: Int) {
        val retrofit: YandexDiskApi = getRetrofit().create(YandexDiskApi::class.java)
        retrofit.getImages(ITEMS_PER_PAGE, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( {
                    imagesResponse: ImagesResponse -> Log.d(TAG, "got images!!: " +
                        "${imagesResponse.items.map { image -> image.name }}")
                    //because there is no "total" in the images response I have to do such a @hack
                    if(imagesResponse.items.size < ITEMS_PER_PAGE) {
                        //to prevent sending useless requests when the end of the list is reached
                        MAX_ITEM_COUNT = adapter.count

                    }
                    val images = imagesResponse.items
                    hideProgressBar()
                    adapter.addImages(images)
                }, {
                    throwable: Throwable? ->
                    throwable?.printStackTrace()

                    //todo: go to authorization activity in case we're not authorized
//                    rxjava throwable get code
//                    if(throwable.code() as HttpException == 401) {
//                    val intent = Intent(this, LoginActivity::class.java)
//                    startActivity(intent)
//                }
                })
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

private fun GridView.setOnClickListener() {
    Log.d("CridView", "grid clicked")
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
