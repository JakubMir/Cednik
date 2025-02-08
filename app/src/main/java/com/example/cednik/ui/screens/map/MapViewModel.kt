package com.example.cednik.ui.screens.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cednik.R
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchByTextRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(

) : ViewModel(), MapScreenActions {

    private var data: MapScreenData = MapScreenData()

    private val _mapUIState: MutableStateFlow<MapUIState> = MutableStateFlow(MapUIState.Loading())

    val mapUIState = _mapUIState.asStateFlow()

    private var placesClient: PlacesClient? = null

    var isSearching: Boolean = false
    fun initPlacesClient(context: Context) {
        viewModelScope.launch {
            try {
                if (!Places.isInitialized()) {
                    Places.initializeWithNewPlacesApiEnabled(context, context.getString(R.string.apiKey))
                }
                if (placesClient == null){
                    placesClient = Places.createClient(context)
                }

            } catch (e: Exception) {
                println("Error: ${e}")
            }

        }
    }

    fun searchPlace(placeName: String?, context: Context){
        initPlacesClient(context)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val placeFields = listOf(
                    Place.Field.NAME,
                    Place.Field.LAT_LNG,
                    Place.Field.ADDRESS
                )

                val request = SearchByTextRequest.builder(placeName, placeFields).setMaxResultCount(1)
                val response = placesClient?.searchByText(request.build())?.await()
                response?.let {
                    placeChanged(response.places.first())
                }

            } catch (e: Exception) {
                println("Error: ${e}")

                _mapUIState.update {
                    if (e.localizedMessage.contains("empty string")){
                        MapUIState.SearchError(context.getString(R.string.text_query_must_not_be_an_empty_string))
                    }
                    else{
                        MapUIState.SearchError(
                            if (e.localizedMessage.contains(':')) e.localizedMessage.substringAfter(
                                ':'
                            ) else context.getString(
                                R.string.no_places_found
                            )
                        )
                    }

                }
            }
            finally {
                isSearching = false
                this.cancel()
            }
        }
    }
    override fun onLocationChanged(latitude: Double, longitude: Double) {
        data.latitude = latitude
        data.longitude = longitude
        data.locationChanged = true
        _mapUIState.update {
            MapUIState.ScreenDataChanged(data)
        }
    }

    override fun placeChanged(place: Place?) {
        data.place = place
        if (place != null){
            _mapUIState.update {
                MapUIState.ScreenDataChanged(data)
            }
        }
    }

    override fun placeNameChanged(placeName: String?) {
        data.placeName = placeName ?: ""
        _mapUIState.update {
            MapUIState.ScreenDataChanged(data)
        }
    }


}