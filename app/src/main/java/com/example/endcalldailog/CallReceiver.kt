package com.example.endcalldailog

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CallReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            Log.e(TAG, "onReceive: context: null or intent: null")
            return
        }

        Log.e(TAG, "callreceiver_receiver_call")

        val preferences = context.getSharedPreferences("CALL_FEATURE", Context.MODE_PRIVATE)
        val isMissedCallFeatureEnable =
            preferences.getBoolean("IS_MISSED_CALL_FEATURE_ENABLE", true)
        val isCompleteCallFeatureEnable =
            preferences.getBoolean("IS_COMPLETE_CALL_FEATURE_ENABLE", true)
        val isNoAnswerFeatureEnable = preferences.getBoolean("IS_NO_ANSWER_FEATURE_ENABLE", true)
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val extraState = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            val phoneNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
            Log.e(TAG, "onReceive: phoneNumber::: $phoneNumber")
            when (extraState) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    if (!isRinging) {
                        isRinging = true
                        isOutgoingRinging = false
                        isIncomingRinging = true

                        Log.e(TAG, "callreceiver_EXTRASTATERINGING")

                        handleIncomingCall(context, phoneNumber)
                    }
                }

                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    if (!isRingingTwo) {
                        isRingingTwo = true
                        isIncomingRinging = false
                        isOutgoingRinging = true
                        Log.e(TAG, "callreceiver_EXTRASTATEOFFHOOK")
                        handleAnsweredCall(context, phoneNumber)
                    }
                }

                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Log.e(TAG, "callreceiver_EXTRASTATEIDLE")

                    if (isRinging || isRingingTwo) {
                        if (isIncomingRinging) {
                            Log.e(TAG, "onReceive: isIncomingRinging::: true")

                            Log.e(TAG, "callreceiver_InncomingCallTrue")

                            if (isMissedCallFeatureEnable && isNoAnswerFeatureEnable && isCompleteCallFeatureEnable) {
                                Log.e(TAG, "onReceive: isIncomingRinging::: All Preference::: true")

                                Log.e(TAG, "callreceiver_InAllPrefTrue")

                                handleEndedCall(context, phoneNumber, intent)
                            } else {
                                if (isMissedCallFeatureEnable) {
                                    Log.e(TAG, "onReceive: isIncomingRinging::: isMissedCallFeatureEnable::: true")

                                    Log.e(TAG, "callreceiver_InMissCallFunTrue")

                                    handleEndedCall(context, phoneNumber, intent)
                                } else if (isNoAnswerFeatureEnable) {
                                    Log.e(TAG, "onReceive: isIncomingRinging::: isNoAnswerFeatureEnable::: true")

                                    Log.e(TAG, "callreceiver_InNoAnsFunTrue")

                                    handleEndedCall(context, phoneNumber, intent)
                                }
                            }
                        } else if (isOutgoingRinging) {
                            Log.e(TAG, "onReceive: isOutgoingRinging::: true")

                            Log.e(TAG, "callreceiver_OutgoingCallTrue")

                            if (isMissedCallFeatureEnable && isNoAnswerFeatureEnable && isCompleteCallFeatureEnable) {
                                Log.e(TAG, "onReceive: isOutgoingRinging::: All Preference::: true")

                                Log.e(TAG, "callreceiver_OutAllPrefTrue")

                                handleEndedCall(context, phoneNumber, intent)
                            } else {
                                if (isCompleteCallFeatureEnable) {
                                        Log.e(TAG, "onReceive: isOutgoingRinging::: isCompleteCallFeatureEnable::: true")

                                    Log.e(TAG, "callreceiver_OutCallEndTrue")

                                    handleEndedCall(context, phoneNumber, intent)
                                }
                            }
                        }
                        isRinging = false
                        isRingingTwo = false
                        isIncomingRinging = false
                        isOutgoingRinging = false
                    }
                }
            }
        }
    }

    private fun handleIncomingCall(context: Context, phoneNumber: String?) {
        time = Date().time
        callType = "Incoming call"
    }

    private fun handleAnsweredCall(context: Context, phoneNumber: String?) {
        time = Date().time
        callType = "Outgoing call"
    }

    private fun handleEndedCall(context: Context, phoneNumber: String?, intent: Intent) {
        Handler(Looper.getMainLooper()).postDelayed({
            try {


                startDetailActivity(context, phoneNumber, time, callType)
            } catch (e: Exception) {
                Log.e(TAG, "callreceiver_NoIntentDetailAct")
            }
        }, 50)
    }

    private fun startDetailActivity(
        context: Context,
        phoneNumber: String?,
        time: Long,
        callType: String
    ) {
        val intent = Intent(context.applicationContext, DetailActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

//        intent.addFlags(
//            Intent.FLAG_ACTIVITY_NEW_TASK or
//                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
//        )

//        intent.addFlags(
//            Intent.FLAG_ACTIVITY_NEW_TASK or
//                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
//                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
//                    Intent.FLAG_ACTIVITY_SINGLE_TOP
//        )

        intent.putExtra("phoneNumber", phoneNumber)
        intent.putExtra("time", formatTimeToString(time))
        intent.putExtra("duration", calculateDuration(time, Date().time))
        intent.putExtra("callType", callType)
        Log.e(TAG, "startDetailActivity: intent::: $intent")
        context.applicationContext.startActivity(intent)
    }

    companion object {
        private const val TAG = "CallReceiver"
        private var time: Long = 0
        private var callType = ""
        private var isRinging: Boolean = false
        private var isRingingTwo: Boolean = false
        private var isIncomingRinging: Boolean = false
        private var isOutgoingRinging: Boolean = false
    }

    fun formatTimeToString(millies: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(millies)
    }

    fun calculateDuration(startTime: Long, endTime: Long): String {
        val durationMillis = endTime - startTime

        // Calculate hours, minutes, and seconds
        val hours = durationMillis / (1000 * 60 * 60)
        val minutes = durationMillis % (1000 * 60 * 60) / (1000 * 60)
        val seconds = durationMillis % (1000 * 60) / 1000

        // Format the duration
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

}