package com.strongbulb.hsv_extraction.extension

import android.content.Context
import android.content.SharedPreferences

private fun Context.getSharedPreference(): SharedPreferences? =
    getSharedPreferences("sharedPreference", Context.MODE_PRIVATE)

fun Context.putSharedPreferenceInt(key: String, value: Int) {
    val sharedPreference = getSharedPreference()
    val editor = sharedPreference?.edit()
    editor?.putInt(key, value)
    editor?.apply()
}

fun Context.getSharedPreferenceInt(key: String, defaultValue: Int = 0): Int? {
    val sharedPreference = getSharedPreference()
    return sharedPreference?.getInt(key, defaultValue)
}