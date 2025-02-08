package com.example.cednik.ui.screens.map

import com.google.android.libraries.places.api.model.Place

class MapScreenData {
    var latitude: Double? = null
    var longitude: Double? = null
    var locationChanged: Boolean = false

    var place: Place? = null
    var placeName: String = ""

}