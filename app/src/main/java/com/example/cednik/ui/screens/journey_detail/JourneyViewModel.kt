package com.example.cednik.ui.screens.journey_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cednik.database.ILocalJourneysRepository
import com.example.cednik.datastore.IDataStoreRepository
import com.example.cednik.datastore.DataStoreConstants
import com.example.cednik.model.Journey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyViewModel @Inject constructor(
    private val repository: ILocalJourneysRepository,
    private val dataStore: IDataStoreRepository
) : ViewModel(), JourneyActions {

    private var data: JourneyScreenData = JourneyScreenData()

    var locationLoaded: Boolean = false

    private val _journeyUIState: MutableStateFlow<JourneyUIState> = MutableStateFlow(JourneyUIState.Loading())

    val journeyUIState = _journeyUIState.asStateFlow()


    override fun deleteJourney() {
        viewModelScope.launch{
            repository.deleteJourney(data.journey)
            _journeyUIState.update {
                JourneyUIState.JourneyDeleted()
            }
        }
    }

    override fun journeyDataChanged(journey: Journey) {
        data.journey = journey
        _journeyUIState.update {
            JourneyUIState.ScreenDataChanged(data)
        }
    }

    fun loadJourney(id: Long){
        viewModelScope.launch {
            dataStore.getLong(DataStoreConstants.USER_KEY.name)?.let{
                data.journey = repository.getJourney(id, it)
            }
            locationLoaded = true
            _journeyUIState.update {
                JourneyUIState.ScreenDataChanged(data)
            }
        }

    }

    fun returnBack(){
        _journeyUIState.update {
            JourneyUIState.ReturnBack()
        }
    }

}