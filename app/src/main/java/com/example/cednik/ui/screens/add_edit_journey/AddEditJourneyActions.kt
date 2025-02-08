package com.example.cednik.ui.screens.add_edit_journey

import android.content.Context

interface AddEditJourneyActions {
    fun journeyNameChanged(name: String?)
    fun journeyTextChanged(text: String?)
    fun journeyLocationChanged(latitude: Double?, longitude: Double?)
    fun userLocationChanged(latitude: Double, longitude: Double)
    fun navigateToMap(latitude: Double?, longitude: Double?)
    fun onLocationChanged(latitude: Double, longitude: Double)
    fun saveJourney()
    fun deleteJourney()
    fun fetchLocation(context: Context)
}