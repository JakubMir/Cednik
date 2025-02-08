package com.example.cednik.ui.screens.journey_detail

sealed class JourneyUIState {
    class Loading : JourneyUIState()
    class JourneyDeleted : JourneyUIState()
    class ScreenDataChanged(val data: JourneyScreenData) : JourneyUIState()
    class ReturnBack() : JourneyUIState()
}