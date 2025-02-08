package com.example.cednik.ui.screens.add_edit_journey

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.birjuvachhani.locus.Locus
import com.birjuvachhani.locus.extensions.isFatal
import com.example.cednik.R
import com.example.cednik.utils.StringResourcesProvider
import com.example.cednik.database.ILocalJourneysRepository
import com.example.cednik.datastore.DataStoreConstants
import com.example.cednik.datastore.IDataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditJourneyViewModel @Inject constructor(
    private val repository: ILocalJourneysRepository,
    private val dataStore: IDataStoreRepository,
    private val stringResourcesProvider: StringResourcesProvider
) : ViewModel(), AddEditJourneyActions {

    private var data: AddEditJourneyScreenData = AddEditJourneyScreenData()

    var locationLoaded: Boolean = false

    private val textError: String = stringResourcesProvider.getString(R.string.cannot_be_empty)

    private val _addEditJourneyUIState: MutableStateFlow<AddEditJourneyUIState> = MutableStateFlow(
        AddEditJourneyUIState.Loading()
    )

    val addEditJourneyUIState = _addEditJourneyUIState.asStateFlow()

    override fun saveJourney() {
        // check mandatory fields
        if (data.journey.name.isNotEmpty()){

            viewModelScope.launch {
                if(data.journey.id == null){
                    data.journey.userId = dataStore.getLong(DataStoreConstants.USER_KEY.name)
                    repository.insertJourney(data.journey)
                }else{
                    repository.updateJourney(data.journey)
                }

                _addEditJourneyUIState.update {
                    AddEditJourneyUIState.JourneySaved()
                }
            }
        } else {
            data.journeyNameError = textError

            _addEditJourneyUIState.update {
                AddEditJourneyUIState.ScreenDataChanged(data)
            }
        }
    }


    override fun journeyNameChanged(name: String?) {
        data.journey.name = name ?: ""
        if (data.journey.name.isNotEmpty())data.journeyNameError = null

        _addEditJourneyUIState.update {
            AddEditJourneyUIState.ScreenDataChanged(data)
        }
    }

    override fun journeyTextChanged(text: String?) {
        data.textString = text ?: ""
        data.journey.text = text
        _addEditJourneyUIState.update {
            AddEditJourneyUIState.ScreenDataChanged(data)
        }
    }

    override fun journeyLocationChanged(latitude: Double?, longitude: Double?) {
        data.journey.latitude = latitude
        data.journey.longitude = longitude
        _addEditJourneyUIState.update {
            AddEditJourneyUIState.ScreenDataChanged(data)
        }
    }

    override fun userLocationChanged(latitude: Double, longitude: Double) {
        data.userLatitude = latitude
        data.userLongitude = longitude
        _addEditJourneyUIState.update {
            AddEditJourneyUIState.ScreenDataChanged(data)
        }
    }

    override fun navigateToMap(latitude: Double?, longitude: Double?) {
        _addEditJourneyUIState.update {
            AddEditJourneyUIState.NavigateToMap(latitude, longitude)
        }
    }

    override fun onLocationChanged(latitude: Double, longitude: Double) {
        data.journey.latitude = latitude
        data.journey.longitude = longitude
        _addEditJourneyUIState.update {
            AddEditJourneyUIState.ScreenDataChanged(data)
        }
    }

    override fun deleteJourney() {
        viewModelScope.launch{
            repository.deleteJourney(data.journey)
            _addEditJourneyUIState.update {
                AddEditJourneyUIState.JourneyDeleted()
            }
        }
    }

    override fun fetchLocation(context: Context) {
        Locus.getCurrentLocation(context) { result ->
            result.location?.let {
                locationLoaded = true
                onLocationChanged(it.latitude, it.longitude) }
            result.error?.let { error ->
                when{
                    error.isFatal -> {
                        data.locationError = stringResourcesProvider.getString(R.string.could_not_load_a_location)
                    }
                }
            }
        }
    }

    fun loadJourney(id: Long?){
        if(id != null) {
            viewModelScope.launch {
                dataStore.getLong(DataStoreConstants.USER_KEY.name)?.let{
                    data.journey = repository.getJourney(id,it)
                }
                data.textString = if(data.journey.text == null) "" else data.journey.text.toString()
                locationLoaded = true
                _addEditJourneyUIState.update {
                    AddEditJourneyUIState.ScreenDataChanged(data)
                }
            }
        }
        else{
            _addEditJourneyUIState.update {
                AddEditJourneyUIState.LoadingLocation()
            }
        }
    }


    fun returnBack(){
        _addEditJourneyUIState.update {
            AddEditJourneyUIState.ReturnBack()
        }
    }

}