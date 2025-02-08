package com.example.cednik.ui.screens.journey_detail

import com.example.cednik.model.Journey

interface JourneyActions {
    fun deleteJourney()
    fun journeyDataChanged(journey: Journey)
}