package com.example.cednik.ui.screens.map

import com.google.android.libraries.places.api.model.Place

interface MapScreenActions {
    fun onLocationChanged(latitude: Double, longitude: Double)
    fun placeChanged(place: Place?)
    fun placeNameChanged(placeName: String?)
}