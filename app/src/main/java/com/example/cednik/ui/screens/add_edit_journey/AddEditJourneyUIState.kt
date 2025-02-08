package com.example.cednik.ui.screens.add_edit_journey

sealed class AddEditJourneyUIState {
    class Loading : AddEditJourneyUIState()
    class LoadingLocation : AddEditJourneyUIState()
    class JourneySaved : AddEditJourneyUIState()
    class JourneyDeleted : AddEditJourneyUIState()
    class ScreenDataChanged(val data: AddEditJourneyScreenData) : AddEditJourneyUIState()
    class NavigateToMap(val latitude: Double?, val longitude: Double?) : AddEditJourneyUIState()
    class ReturnBack() : AddEditJourneyUIState()
}