package ya.co.yandex_gallery.model

//It could be better to write separate class for @YandexDiskPublicApi.getImages()
/**but I'm too lazy in here for creating another ImagesResponse-like data class just because of in /public/resources
 * there are only _embedded world difference in the response
 */
data class ImagesResponse(val items: List<Image>, val _embedded: ImagesResponse?) {

}