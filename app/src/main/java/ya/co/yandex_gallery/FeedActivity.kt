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
import ya.co.yandex_gallery.util.AppConstants
import ya.co.yandex_gallery.adapters.ImageFeedAdapter
import ya.co.yandex_gallery.data.YandexDiskClient
import ya.co.yandex_gallery.model.Image
import ya.co.yandex_gallery.model.ImagesResponse


class FeedActivity : AppCompatActivity() {

    companion object {
        private val TAG = "FeedActivity"
        private val ITEMS_PER_PAGE = 25
        //cause there is no such field as "total" in the response!!!!
        private var MAX_ITEM_COUNT = 25*ITEMS_PER_PAGE
    }

    private var currentOffset: Int = 0
    private var isLoading = false
    private var isContinueAnonimous = false

    @BindView(R.id.grid_images) lateinit var imagesGrid: GridView
    @BindView(R.id.swipeRefreshFeed) lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private lateinit var adapter: ImageFeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        ButterKnife.bind(this)

        adapter = ImageFeedAdapter(this)
        imagesGrid.adapter = adapter


        isContinueAnonimous = intent.getBooleanExtra(AppConstants.KEY_IS_CONTINUE_ANON, false)


        //if user is lon logged in - he cannot see this page
        if(AppConstants.getAccessToken() == "" && !isContinueAnonimous) {
            redirectToLoginPage()
        } else {
            showProgressBar()
            loadImages(currentOffset)

            swipeRefreshLayout.setOnRefreshListener {
                adapter.clearImages()
                currentOffset = 0
                loadImages(currentOffset)
            }

            imagesGrid.setOnScrollListener(object: AbsListView.OnScrollListener {
                override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                    if (totalItemCount - visibleItemCount <= firstVisibleItem
                            && adapter.count + ITEMS_PER_PAGE <= MAX_ITEM_COUNT && !isLoading) {
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
        //but to Implement it in repository class I have to avoid all side effects somehow
        isLoading = true //exclude: side effect ((

        YandexDiskClient
                .loadImages(isContinueAnonimous, ITEMS_PER_PAGE, offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( {
                    imagesResponse: ImagesResponse -> Log.e(TAG, "got images!!: " +
                        "${imagesResponse}")

                    val images = if(isContinueAnonimous) {
                        imagesResponse._embedded!!.items
                    } else {
                        imagesResponse.items
                    }

                    hideProgressBar()
                    adapter.addImages(images)
                    isLoading = false

                    //because there is no "total" in the images response I have to do such a @hack
                    if(images.size < ITEMS_PER_PAGE) {
                        //to prevent sending useless requests when the end of the list is reached
                        MAX_ITEM_COUNT = adapter.count
                    }


                }, {
                    throwable: Throwable? ->
                    throwable?.printStackTrace()
                    Toast.makeText(this, "Error loading photos ${throwable.toString()}", Toast.LENGTH_LONG)
                            .show()
                })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(!isContinueAnonimous) { //able to logout only if logged in
            menuInflater.inflate(R.menu.menu_main, menu)
            return super.onCreateOptionsMenu(menu)
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_logout) {
            logoutUser()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun logoutUser() { //td: create UserManager class?
        val editor = PreferenceManager.getDefaultSharedPreferences(this).edit()
        editor.remove(AppConstants.KEY_TOKEN)
        editor.apply()
        redirectToLoginPage()

        AppConstants.clearAccessToken()
        //todo: it would be good to clear the token cookie in the webView also
    }



}
