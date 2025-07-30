package com.ring.evolve.utils.storage

interface SharedPreferenceStorageTypes {
    fun setString(key: String, value: String)
    fun getString(key: String): String?

    fun setBoolean(key: String, value: Boolean)
    fun getBoolean(key: String): Boolean?

    fun setInt(key: String, value: Int)
    fun getInt(key: String): Int?

    fun remove(key: String)
    fun clear()
}