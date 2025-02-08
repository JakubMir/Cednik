package com.example.cednik.ui.screens.map

sealed class MapUIState {
    class Loading : MapUIState()
    class ScreenDataChanged(val data: MapScreenData) : MapUIState()

    class SearchError(val error: String) : MapUIState()
}