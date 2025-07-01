package com.example.endcalldailog

import android.app.Application
import android.content.IntentFilter

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val callReceiver = CallReceiver()
        registerReceiver(
            callReceiver,
            IntentFilter(
                "android.intent.action.PHONE_STATE"
            )
        )

    }

}