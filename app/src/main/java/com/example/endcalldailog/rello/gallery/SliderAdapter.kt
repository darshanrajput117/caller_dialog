import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.gallery.SliderItem
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class SliderAdapter(
    private val context: Context,
    private val items: List<SliderItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_IMAGE = 0
        private const val VIEW_TYPE_AD = 1
    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fullImage: ImageView = view.findViewById(R.id.fullImageView)
    }

    inner class AdViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val adView: NativeAdView = view.findViewById(R.id.nativeAdView)
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SliderItem.ImageItem -> VIEW_TYPE_IMAGE
            is SliderItem.AdItem -> VIEW_TYPE_AD
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_IMAGE) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_slider_image, parent, false)
            ImageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_native_ad, parent, false)
            AdViewHolder(view)
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is SliderItem.ImageItem -> {
                val imageHolder = holder as ImageViewHolder
                Glide.with(context)
                    .load(item.uri)
                    .into(imageHolder.fullImage)
            }
            is SliderItem.AdItem -> {
                val adHolder = holder as AdViewHolder
                populateNativeAdView(item.nativeAd, adHolder.adView)
            }
        }
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
        val imageView = adView.findViewById<ImageView>(R.id.ad_image)

        adView.headlineView = headlineView
        adView.imageView = imageView

        headlineView.text = nativeAd.headline
        nativeAd.images.firstOrNull()?.drawable?.let {
            imageView.setImageDrawable(it)
        }

        adView.setNativeAd(nativeAd)
    }
}
