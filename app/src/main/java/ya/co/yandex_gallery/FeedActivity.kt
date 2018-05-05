package ya.co.yandex_gallery

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
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
import ya.co.yandex_gallery.model.Image
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

        //if user is lon logged in - he cannot see this page
        if(AppConstants.getAccessToken() == "")
            redirectToLoginPage()

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

        //this code is better to be here than inside the adapter I guess
        imagesGrid.onItemClickListener = AdapterView.OnItemClickListener { _, _, itemPosition, _ ->
            val image: Image = adapter.getItem(itemPosition)

            val intent = Intent(MyApplication.appContext, PhotoDetailsActivity::class.java)
            intent.putExtra(AppConstants.KEY_IMAGE_URL, image.file)
            intent.putExtra(AppConstants.KEY_IMAGE_NAME, image.name)
            MyApplication.appContext?.startActivity(intent)
        }
    }

    private fun redirectToLoginPage() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    private fun showProgressBar() {
        swipeRefreshLayout.isRefreshing = true
    }

    private fun hideProgressBar() {
        swipeRefreshLayout.isRefreshing = false
    }


    fun loadImages(offset: Int) { //todo: put in ImagesRepository class
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
                    Toast.makeText(this, "Error loading photos ${throwable.toString()}", Toast.LENGTH_LONG)
                            .show()
                })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_logout) {
            logoutUser()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun logoutUser() {
        //pretend we've never been logged in
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.remove(AppConstants.KEY_TOKEN)
        editor.apply()
        redirectToLoginPage()

        AppConstants.clearAccessToken()
        //todo: it would be good to clear the token cookie in the webView also
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
