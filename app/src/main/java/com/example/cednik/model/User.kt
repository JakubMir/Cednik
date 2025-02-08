package com.example.cednik.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class User(var email: String, var password: String){

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null
}
