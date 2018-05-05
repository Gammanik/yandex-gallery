package ya.co.yandex_gallery.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ya.co.yandex_gallery.model.ImagesResponse


interface YandexDiskApi {

    //todo: add @Header("Authorization") String token -- to make code more explicit?
    @GET("resources/files")
    fun getImages(@Query("limit") limit: Int,
                  @Query("offset") offset: Int,
                  @Query("media_type") mediaType: String = "image",
                  @Query("preview_size") previewSize: String = "L"): Single<ImagesResponse>
}