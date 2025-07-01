package com.example.endcalldailog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.endcalldailog.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private val _binding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    private val phoneNumber by lazy { intent.getStringExtra("phoneNumber") }
    private val time by lazy { intent.getStringExtra("time") }
    private val duration by lazy { intent.getStringExtra("duration") }
    private val callType by lazy { intent.getStringExtra("callType") }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(_binding.root)
        _binding.tvCallComingTime.text = time
        _binding.tvCallDuration.text = duration
        _binding.phoneNumber.text = phoneNumber
        _binding.tvCallType.text = callType

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    }



    private fun makePhoneCall(context: Context) {
        val intent = Intent(Intent.ACTION_DIAL)
        context.startActivity(intent)
        finishAndRemoveTask()
    }


    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            Log.e(TAG, "callscreen_onbackpress")

            finishAndRemoveTask()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "callscreen_onpause")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "callscreen_onresume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "callscreen_ondestroy")
    }

    companion object {
        const val TAG = "DetailAct"
    }

}