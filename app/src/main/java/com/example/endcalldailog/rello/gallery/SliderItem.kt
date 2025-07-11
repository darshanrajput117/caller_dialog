package com.example.myapplication.gallery

import android.net.Uri
import com.google.android.gms.ads.nativead.NativeAd

sealed class SliderItem {
    data class ImageItem(val uri: Uri) : SliderItem()
    data class AdItem(val nativeAd: NativeAd) : SliderItem()
}
