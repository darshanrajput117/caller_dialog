package com.example.endcalldailog

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

object SharedPref {

    private const val PREF_NAME = "NG_CAD_FIRST_RESPONDER"
    const val ACCESS_TOKEN = "ACCESS_TOKEN"
    const val IS_READ_UNREAD_REQUIRED = "isReadUnreadRequired"
    const val ACCESS_STATE_DEVICE = "accessStateDevice"

    private var mSharedPref: SharedPreferences? = null

    fun init(context: Context) {
        if (mSharedPref == null) {
            mSharedPref = context.getSharedPreferences(context.packageName, Activity.MODE_PRIVATE)
        }
    }

    fun getString(key: String, defValue: String): String {
        return mSharedPref?.getString(key, defValue) ?: defValue
    }

    fun putString(key: String, value: String) {
        mSharedPref?.edit()?.putString(key, value)?.apply()
    }

    fun getInteger(key: String, defValue: Int): Int {
        return mSharedPref?.getInt(key, defValue) ?: defValue
    }

    fun putInteger(key: String, value: Int) {
        mSharedPref?.edit()?.putInt(key, value)?.apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mSharedPref?.getBoolean(key, defValue) ?: defValue
    }

    fun putBoolean(key: String, value: Boolean) {
        mSharedPref?.edit()?.putBoolean(key, value)?.apply()
    }

    fun getLong(key: String, defValue: Long): Long {
        return mSharedPref?.getLong(key, defValue) ?: defValue
    }

    fun putLong(key: String, value: Long) {
        mSharedPref?.edit()?.putLong(key, value)?.apply()
    }

    fun getFloat(key: String, defValue: Float): Float {
        return mSharedPref?.getFloat(key, defValue) ?: defValue
    }

    fun putFloat(key: String, value: Float) {
        mSharedPref?.edit()?.putFloat(key, value)?.apply()
    }

    fun clearPreference() {
        mSharedPref?.edit()?.clear()?.apply()
    }

    fun removePreference(key: String) {
        mSharedPref?.edit()?.remove(key)?.apply()
    }
}
