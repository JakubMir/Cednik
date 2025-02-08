package com.example.cednik.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "journeys")
@JsonClass(generateAdapter = true)
data class Journey(var name: String){

    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    var text: String? = null

    var latitude: Double? = null

    var longitude: Double? = null

    var userId: Long? = null
}
