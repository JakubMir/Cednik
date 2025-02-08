package com.example.cednik.ui.screens.journey_detail

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cednik.R
import com.example.cednik.extensions.getValue
import com.example.cednik.extensions.removeValue
import com.example.cednik.model.Journey
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
fun JourneyScreen(navigationRouter: INavigationRouter, id: Long){

    val viewModel = hiltViewModel<JourneyViewModel>()

    val state = viewModel.journeyUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(JourneyScreenData())
    }

    LifecycleEventEffect(event = Lifecycle.Event.ON_RESUME) {
        val journeyScreenResult = navigationRouter.getNavController().getValue<String>("journey")
        journeyScreenResult?.value?.let { it ->
            val moshi: Moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<Journey> = moshi.adapter(Journey::class.java)
            val journey = jsonAdapter.fromJson(it)
            navigationRouter.getNavController().removeValue<String>("journey")
            journey?.let {
                viewModel.journeyDataChanged(it)
            }
        }
    }

    state.value.let {
        when(it){
            is JourneyUIState.JourneyDeleted -> {
                LaunchedEffect(it) {
                    navigationRouter.returnBack()
                }
            }
            is JourneyUIState.Loading -> {
                viewModel.loadJourney(id)
            }
            is JourneyUIState.ReturnBack -> {
                LaunchedEffect(it){
                    navigationRouter.returnBack()
                }
            }
            is JourneyUIState.ScreenDataChanged -> {
                data = it.data
            }
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = {
                    Text(text = data.journey.name)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.returnBack() // Preventing jumping out of navigation stack
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.deleteJourney()
                    }) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigationRouter.navigateToAddEditJourney(id)
            },
            ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "")
            }
        }
    ){
        JourneyScreenContent(
            paddingValues = it,
            viewModel = viewModel,
            data = data
        )
    }
}

@Composable
fun JourneyScreenContent(
    paddingValues: PaddingValues,
    viewModel: JourneyViewModel,
    data: JourneyScreenData
){
    val latitude by rememberUpdatedState(newValue = data.journey.latitude ?: 0.0)
    val longitude by rememberUpdatedState(newValue = data.journey.longitude ?: 0.0)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude),14.0f)
    }

    val markerState = rememberMarkerState(position = LatLng(latitude, longitude))

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
                    Text(text = stringResource(R.string.loading_map))
                }
            }
            else if (data.locationError != null){
                Text(text = data.locationError!!, Modifier.align(
                    Alignment.Center))
            }
            else{
                GoogleMap(
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(scrollGesturesEnabled = false, scrollGesturesEnabledDuringRotateOrZoom = false, zoomGesturesEnabled = false, zoomControlsEnabled = true, rotationGesturesEnabled = false, tiltGesturesEnabled = false),
                ) {
                    Marker(state = markerState, draggable = false)
                }
            }

        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            text = data.journey.text?:""
        )

    }
}