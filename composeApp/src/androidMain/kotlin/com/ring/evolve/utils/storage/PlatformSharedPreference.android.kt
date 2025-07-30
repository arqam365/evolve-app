package com.ring.evolve.utils.storage

import android.content.Context
import android.content.SharedPreferences

actual class PlatformSharedPreference : SharedPreferenceStorageTypes {

    private lateinit var prefs: SharedPreferences

    fun initialize(context: Context): PlatformSharedPreference {
        prefs = context.getSharedPreferences("evolve_prefs", Context.MODE_PRIVATE)
        return this
    }

    actual override fun setString(key: String, value: String) = prefs.edit().putString(key, value).apply()
    actual override fun getString(key: String): String? = prefs.getString(key, null)

    actual override fun setBoolean(key: String, value: Boolean) = prefs.edit().putBoolean(key, value).apply()
    actual override fun getBoolean(key: String): Boolean? = if (prefs.contains(key)) prefs.getBoolean(key, false) else null

    actual override fun setInt(key: String, value: Int) = prefs.edit().putInt(key, value).apply()
    actual override fun getInt(key: String): Int? = if (prefs.contains(key)) prefs.getInt(key, 0) else null

    actual override fun remove(key: String) = prefs.edit().remove(key).apply()
    actual override fun clear() = prefs.edit().clear().apply()
}