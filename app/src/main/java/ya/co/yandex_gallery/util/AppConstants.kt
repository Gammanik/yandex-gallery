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

        @JvmStatic
        val TOKEN_KEY = "ACCESS_TOKEN_KEY"

        @JvmStatic
        var ACCESS_TOKEN = ""

        @JvmStatic
        fun getAccessToken(): String {
            if(ACCESS_TOKEN != "") {
                return ACCESS_TOKEN
            } else {
                val prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.appContext)
                val token = prefs.getString(AppConstants.TOKEN_KEY, "")
                AppConstants.ACCESS_TOKEN = token
                return token
            }
        }

        @JvmStatic
        fun getUrlWithHeaders(url: String): GlideUrl {
            return GlideUrl(url, LazyHeaders.Builder()
                    .addHeader("Authorization", "OAuth ${getAccessToken()}")
                    .build())
        }
    }
}