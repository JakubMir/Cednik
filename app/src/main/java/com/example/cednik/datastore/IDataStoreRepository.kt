package com.example.cednik.datastore

interface IDataStoreRepository {
    suspend fun putString(key: String, value: String)
    suspend fun putLong(key: String, value: Long)
    suspend fun getString(key: String): String?
    suspend fun getLong(key: String): Long?
    suspend fun clearAll()
}