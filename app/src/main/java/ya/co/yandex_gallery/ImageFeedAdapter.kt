package ya.co.yandex_gallery

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import ya.co.yandex_gallery.model.Image
import ya.co.yandex_gallery.util.AppConstants


class ImageFeedAdapter (private val mContext: Context) : BaseAdapter() {

    private var imagesList: ArrayList<Image> = ArrayList<Image>()

    fun addImages(newImages: List<Image> ) {
        imagesList.addAll(newImages)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = imagesList.size

    override fun getItem(position: Int): Image = imagesList[position]

    override fun getItemId(position: Int): Long = 0L

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = ImageView(mContext)
            imageView.layoutParams = ViewGroup.LayoutParams(120, 120)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            imageView = convertView as ImageView
        }

        Glide.with(mContext)
                .load(AppConstants.getUrlWithHeaders(imagesList[position].preview))
//               todo:  .placeholder("")
                .into(imageView)

        return imageView
    }
}