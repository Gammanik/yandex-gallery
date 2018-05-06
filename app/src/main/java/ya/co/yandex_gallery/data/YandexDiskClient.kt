package ya.co.yandex_gallery.data

import io.reactivex.Single
import ya.co.yandex_gallery.model.ImagesResponse


class YandexDiskClient {

    companion object {

        private val publicApi: YandexDiskPublicApi = NetworkHelper.getRetrofit().create(YandexDiskPublicApi::class.java)
        private val guardedApi: YandexDiskApi = NetworkHelper.getRetrofit().create(YandexDiskApi::class.java)

        fun loadImages(isContinueAnonymous: Boolean, itemsPerPage: Int, offset: Int): Single<ImagesResponse> {
            return if(isContinueAnonymous) {
                publicApi.getImages(itemsPerPage, offset, "https%3A%2F%2Fyadi.sk%2Fd%2F2auBunz33VQqVP")
            } else {
                guardedApi.getImages(itemsPerPage, offset)
            }
        }
    }

}