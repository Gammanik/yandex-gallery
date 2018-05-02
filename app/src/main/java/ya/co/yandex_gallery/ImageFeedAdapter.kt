package ya.co.yandex_gallery

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import ya.co.yandex_gallery.model.Image
import ya.co.yandex_gallery.util.AppConstants
import android.view.LayoutInflater


class ImageFeedAdapter (private val mContext: Context) : BaseAdapter() {

    private var imagesList: ArrayList<Image> = ArrayList<Image>()

    fun addImages(newImages: List<Image> ) {
        imagesList.addAll(newImages)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = imagesList.size

    override fun getItem(position: Int): Image = imagesList[position]

    override fun getItemId(position: Int): Long = position.toLong()

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        val gridViewItem: View
        //cause parameter var (convertView) cannot be re-assigned - local var to hold returned view
        //todo: var holder: ViewHolder

        // LayoutInflater to call external grid_element.xml file
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            //todo: convertView =

            gridViewItem = inflater.inflate(R.layout.grid_element, null)
            imageView = gridViewItem.findViewById(R.id.grid_item_image) as ImageView

            imageView.scaleType = ImageView.ScaleType.FIT_XY

            Glide.with(mContext)
                    .load(AppConstants.getUrlWithHeaders(imagesList[position].preview))
                    //todo:  .placeholder(imagesList[position].name)
                    .into(imageView)
        } else {
            //todo: why is this happening?
            Log.e("TAG", "position $position -- ${imagesList[position].name}")
            gridViewItem = convertView
        }

        //todo: return convertView
        return gridViewItem
    }
}