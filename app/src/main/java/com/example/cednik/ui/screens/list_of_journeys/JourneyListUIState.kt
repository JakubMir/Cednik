package com.example.cednik.ui.screens.list_of_journeys

import com.example.cednik.model.Journey


sealed class JourneyListUIState {
    class Loading : JourneyListUIState()
    class Success(val journeys: List<Journey>) : JourneyListUIState()
}