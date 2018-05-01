package ya.co.yandex_gallery.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ya.co.yandex_gallery.model.ImagesResponse


public interface YandexDiskApi {

    @GET("resources/last-uploaded")
    fun getImages(@Query("limit") limit: Int,
                  @Query("media_type") mediaType: String): Single<ImagesResponse>
}