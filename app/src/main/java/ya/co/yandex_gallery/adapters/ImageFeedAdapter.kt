package ya.co.yandex_gallery.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import ya.co.yandex_gallery.model.Image
import ya.co.yandex_gallery.util.AppConstants
import android.view.LayoutInflater
import com.bumptech.glide.request.RequestOptions
import ya.co.yandex_gallery.R


class ImageFeedAdapter (private val mContext: Context) : BaseAdapter() {

    private var imagesList: ArrayList<Image> = ArrayList<Image>()

    fun addImages(newImages: List<Image> ) {
        imagesList.addAll(newImages)
        notifyDataSetChanged()
    }

    fun clearImages() {
        imagesList.clear()
        notifyDataSetChanged()
    }

    override fun getCount(): Int = imagesList.size

    override fun getItem(position: Int): Image = imagesList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        //cause parameter var (convertView) cannot be re-assigned - local var to hold returned view
        var returnedConvertView: View //replacement of a convertView - gridViewItem
        var holder: ViewHolder

        // LayoutInflater to call external grid_element.xml file
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            returnedConvertView= inflater.inflate(R.layout.grid_element, null)
            holder = ViewHolder()
            imageView = returnedConvertView.findViewById(R.id.grid_item_image) as ImageView
            imageView.scaleType = ImageView.ScaleType.FIT_XY

            holder.imageView = imageView
            returnedConvertView.tag = holder

        } else {
            returnedConvertView = convertView
            holder = convertView.tag as ViewHolder
        }

        val currentPicDownloadUrl = if(imagesList[position].preview != null) {
            imagesList[position].preview!!
        } else {
            imagesList[position].file
        }



        Glide.with(mContext)
                .load(AppConstants.getUrlWithHeaders(currentPicDownloadUrl))
                .apply(RequestOptions().centerCrop())
                .into(holder.imageView!!)

        return returnedConvertView
    }

    //use ViewHolder pattern because call findViewById() is quite expensive
    class ViewHolder {
        var imageView: ImageView? = null
    }
}