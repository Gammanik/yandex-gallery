package ya.co.yandex_gallery.util

import android.preference.PreferenceManager
import ya.co.yandex_gallery.MyApplication
import ya.co.yandex_gallery.util.Env.CLIENT_ID
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.model.GlideUrl




class AppConstants {
    companion object {

        @JvmStatic
        val API_BASE_URL = "https://cloud-api.yandex.net/v1/disk/"

        @JvmStatic
        val REDIRECT_URL = "yandex-gallery://token-callback"

        @JvmStatic
        val AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id=$CLIENT_ID&redirect_uri=$REDIRECT_URL"

        @JvmStatic //for preference manager
        val KEY_TOKEN = "ACCESS_TOKEN_KEY"

        @JvmStatic //intent.putExtra
        val KEY_IMAGE_URL = "IMAGE_DOWNLOAD_URL_KEY"

        @JvmStatic //intent.putExtra
        val KEY_IMAGE_NAME = "IMAGE_DOWNLOAD_NAME_KEY"

        @JvmStatic
        val KEY_IS_CONTINUE_ANON = "USER_WITHOUT_REGISTRATION_KEY"

        @JvmStatic
        private var ACCESS_TOKEN = ""

        @JvmStatic
        fun getAccessToken(): String {
            if(ACCESS_TOKEN != "") {
                return ACCESS_TOKEN
            } else {
                val prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.appContext)
                val token = prefs.getString(AppConstants.KEY_TOKEN, "")
                AppConstants.ACCESS_TOKEN = token
                return token
            }
        }

        @JvmStatic
        fun clearAccessToken() {
            ACCESS_TOKEN = ""
        }

        @JvmStatic
        fun getUrlWithHeaders(url: String): GlideUrl {
            return GlideUrl(url, LazyHeaders.Builder()
                    .addHeader("Authorization", "OAuth ${getAccessToken()}")
                    .build())
        }
    }
}