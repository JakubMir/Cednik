package com.example.cednik.ui.screens.list_of_journeys

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cednik.database.ILocalJourneysRepository
import com.example.cednik.datastore.IDataStoreRepository
import com.example.cednik.datastore.DataStoreConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JourneyListViewModel @Inject constructor(
    private val repository: ILocalJourneysRepository,
    private val dataStore: IDataStoreRepository
) : ViewModel() {

    val journeyListUIState: MutableState<JourneyListUIState> =
        mutableStateOf(JourneyListUIState.Loading())


    fun loadJourneys() {
        viewModelScope.launch {
            dataStore.getLong(DataStoreConstants.USER_KEY.name)?.let {
                repository.getAllJourneys(it).collect {
                    journeyListUIState.value = JourneyListUIState.Success(it)
                }
            }
        }
    }
}