package com.ring.evolve.utils.storage

expect class PlatformSharedPreference() : SharedPreferenceStorageTypes {
    override fun setString(key: String, value: String)
    override fun getString(key: String): String?
    override fun setBoolean(key: String, value: Boolean)
    override fun getBoolean(key: String): Boolean?
    override fun setInt(key: String, value: Int)
    override fun getInt(key: String): Int?
    override fun remove(key: String)
    override fun clear()
}