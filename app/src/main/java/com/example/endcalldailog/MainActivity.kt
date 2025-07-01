package com.example.endcalldailog

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.endcalldailog.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        requestScreenOverlayPermission()
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finishAffinity()
        }
    }
    private val overlayPermissionHandler = Handler(Looper.getMainLooper())

    private fun requestScreenOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${packageName}")
        )
        requestScreenOverlayPermission.launch(intent)
        overlayPermissionHandler.post(checkOverlayPermissionRunnable)
    }


    private val checkOverlayPermissionRunnable = object : Runnable {
        override fun run() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this@MainActivity)) {

                } else {
                    overlayPermissionHandler.removeCallbacks(this)
                    val flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startIntentWithFlags(MainActivity::class.java, flags)
                }
            }

        }
    }

    fun startIntentWithFlags(destinationClass: Class<*>, flags: Int) = Intent().apply {
        setClass(this@MainActivity, destinationClass)
        this.flags = flags
        startActivity(this)
    }

    private val requestScreenOverlayPermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

        }

}