package ya.co.yandex_gallery.util

import ya.co.yandex_gallery.util.Env.CLIENT_ID


class AppConstants {
    companion object {
        @JvmStatic
        val REDIRECT_URL = "yandex-gallery://token-callback"

        @JvmStatic //todo: add CALLBACK_URL as parameter here?
        val AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id=$CLIENT_ID&redirect_uri=$REDIRECT_URL"

        @JvmStatic
        val TOKEN_KEY = "ACCESS_TOKEN_KEY"
    }
}