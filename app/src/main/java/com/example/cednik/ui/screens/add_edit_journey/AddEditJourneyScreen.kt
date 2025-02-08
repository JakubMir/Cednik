package com.example.cednik.ui.screens.add_edit_journey

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cednik.R
import com.example.cednik.extensions.getValue
import com.example.cednik.extensions.removeValue
import com.example.cednik.model.Location
import com.example.cednik.navigation.INavigationRouter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditJourneyScreen(navigationRouter: INavigationRouter, id: Long?) {

    val viewModel = hiltViewModel<AddEditJourneyViewModel>()

    val state = viewModel.addEditJourneyUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(AddEditJourneyScreenData())
    }
    val context = LocalContext.current

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        val mapScreenResult = navigationRouter.getNavController().getValue<String>("location")
        mapScreenResult?.value?.let { it ->
            val moshi: Moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<Location> = moshi.adapter(Location::class.java)
            val location = jsonAdapter.fromJson(it)
            navigationRouter.getNavController().removeValue<String>("location")
            location?.let {
                viewModel.journeyLocationChanged(it.latitude, it.longitude)
            }
        }
    }


    state.value.let {
        when(it){
            is AddEditJourneyUIState.Loading -> {
                viewModel.loadJourney(id)
            }
            is AddEditJourneyUIState.ScreenDataChanged -> {
                data = it.data
            }
            is AddEditJourneyUIState.JourneyDeleted -> {
                LaunchedEffect(it) {
                    navigationRouter.returnBack()
                }
            }
            is AddEditJourneyUIState.JourneySaved -> {
                LaunchedEffect(it) {
                    if (id != null) {
                        navigationRouter.returnFromAddEditJourney(data.journey)
                    } else {
                        navigationRouter.returnBack()
                    }
                }
            }

            is AddEditJourneyUIState.ReturnBack -> {
                LaunchedEffect(it){
                    navigationRouter.returnBack()
                }
            }

            is AddEditJourneyUIState.LoadingLocation -> {
                LaunchedEffect(it){
                    viewModel.fetchLocation(context)
                }
            }

            is AddEditJourneyUIState.NavigateToMap -> {
                LaunchedEffect(it){
                    navigationRouter.navigateToMap(it.latitude, it.longitude)
                    viewModel.journeyLocationChanged(it.latitude, it.longitude)
                }
            }
        }
    }
    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if(id != null)data.journey.name else stringResource(R.string.add_journey))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.returnBack() // preventing jumping out of navigation stack
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                }
            )
        }
    ){
        AddEditJourneyScreenContent(
            paddingValues = it,
            viewModel = viewModel,
            data = data
        )
    }
}

@Composable
fun AddEditJourneyScreenContent(
    paddingValues: PaddingValues,
    viewModel: AddEditJourneyViewModel,
    data: AddEditJourneyScreenData,
){
    // update location on change
    val latitude by rememberUpdatedState(newValue = data.journey.latitude ?: data.userLatitude)
    val longitude by rememberUpdatedState(newValue = data.journey.longitude ?: data.userLongitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude),14.0f)
    }

    val markerState = rememberMarkerState(position = LatLng(latitude, longitude))

    // update map location on location change
    LaunchedEffect(latitude, longitude) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 14.0f)
        markerState.position = LatLng(latitude, longitude)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp) // M3 padding
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ){
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp),
            label = {
                Text(text = stringResource(R.string.name))
            },
            value = data.journey.name,
            onValueChange = {
                viewModel.journeyNameChanged(it)
            },
            supportingText = {
                if (data.journeyNameError != null){
                    Text(text = data.journeyNameError!!)
                }
            },
            isError = data.journeyNameError != null,
            singleLine = true,
        )

    Box(modifier = Modifier
        .aspectRatio(ratio = 1f)
        .padding(bottom = 8.dp)
        ){

        if (!viewModel.locationLoaded){
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                Text(text = stringResource(R.string.loading_your_location))
            }
        }
        else if (data.locationError != null){
            Text(text = data.locationError!!,Modifier.align(
                Alignment.Center))
        }
        else{
            GoogleMap(
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(scrollGesturesEnabled = false, scrollGesturesEnabledDuringRotateOrZoom = false, zoomGesturesEnabled = false, zoomControlsEnabled = true, rotationGesturesEnabled = false, tiltGesturesEnabled = false),
                onMapClick = {
                    viewModel.navigateToMap(latitude, longitude)
                }
            ) {
                Marker(state = markerState, draggable = false)
            }
        }

    }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            label = {
                Text(text = stringResource(R.string.note))
            },
            value = data.textString,
            onValueChange = {
                viewModel.journeyTextChanged(it)
            },
            supportingText = {

            },
            singleLine = false,
        )

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                if (viewModel.locationLoaded) viewModel.saveJourney() else null
            }) {
            Text(text = stringResource(id = R.string.submit))
        }

    }
}