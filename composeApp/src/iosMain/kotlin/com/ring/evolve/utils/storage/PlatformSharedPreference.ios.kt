package com.ring.evolve.utils.storage

import platform.Foundation.NSUserDefaults

actual class PlatformSharedPreference : SharedPreferenceStorageTypes {

    private val prefs = NSUserDefaults.standardUserDefaults

    actual override fun setString(key: String, value: String) = prefs.setObject(value, key)
    actual override fun getString(key: String): String? = prefs.stringForKey(key)

    actual override fun setBoolean(key: String, value: Boolean) = prefs.setBool(value, key)
    actual override fun getBoolean(key: String): Boolean? = prefs.objectForKey(key)?.let { prefs.boolForKey(key) }

    actual override fun setInt(key: String, value: Int) = prefs.setInteger(value.toLong(), key)
    actual override fun getInt(key: String): Int? = prefs.objectForKey(key)?.let { prefs.integerForKey(key).toInt() }

    actual override fun remove(key: String) = prefs.removeObjectForKey(key)
    actual override fun clear() {
        prefs.dictionaryRepresentation().keys.forEach {
            prefs.removeObjectForKey(it as String)
        }
    }
}