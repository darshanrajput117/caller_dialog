package com.example.myapplication.gallery

import SliderAdapter
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityImageSliderBinding
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd

class ImageSliderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageSliderBinding
    private lateinit var imageUris: List<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageSliderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUris = intent.getParcelableArrayListExtra("image_uris") ?: emptyList()
        val startPosition = intent.getIntExtra("start_position", 0)

//        binding.viewPager.adapter = SliderAdapter(this , imageUris)
        buildSliderItems(imageUris) { sliderItems ->
            binding.viewPager.adapter = SliderAdapter(this, sliderItems)
            binding.viewPager.setCurrentItem(startPosition, false)
        }

//        binding.viewPager.setCurrentItem(startPosition, false)

    }

    private fun buildSliderItems(images: List<Uri>, onComplete: (List<SliderItem>) -> Unit) {
        val imageItems = images.map { SliderItem.ImageItem(it) }.toMutableList()
        val adCount = imageItems.size / 3
        val nativeAds = mutableListOf<NativeAd>()

        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forNativeAd { nativeAd ->
                nativeAds.add(nativeAd)
                if (nativeAds.size == adCount) {
                    // Inject ads into image list
                    val finalList = mutableListOf<SliderItem>()
                    var imageIndex = 0
                    for (i in 0 until imageItems.size + nativeAds.size) {
                        if ((i + 1) % 4 == 0 && nativeAds.isNotEmpty()) {
                            finalList.add(SliderItem.AdItem(nativeAds.removeAt(0)))
                        } else if (imageIndex < imageItems.size) {
                            finalList.add(imageItems[imageIndex++])
                        }
                    }
                    onComplete(finalList)
                }
            }
            .build()

        repeat(adCount) {
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }


}