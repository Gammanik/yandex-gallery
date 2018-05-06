package ya.co.yandex_gallery.data

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ya.co.yandex_gallery.model.ImagesResponse


//in case user does not have photos on his di there is a "demo version"
/**
 * I had hardcoded here my public folder key
 * for not logged user to be able to try the so called application
 */
interface YandexDiskPublicApi{

    @GET("public/resources?")
    fun getImages(@Query("limit") limit: Int,
                  @Query("offset") offset: Int,
                  @Query("public_key") key: String = "https%3A%2F%2Fyadi.sk%2Fd%2F2auBunz33VQqVP"): Single<ImagesResponse>
}