package com.example.cednik.ui.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cednik.R
import com.example.cednik.navigation.INavigationRouter
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navigationRouter: INavigationRouter,
    latitude: Double?,
    longitude: Double?
){

    val context = LocalContext.current

    val viewModel = hiltViewModel<MapViewModel>()

    val state = viewModel.mapUIState.collectAsStateWithLifecycle()

    var data by remember {
        mutableStateOf(MapScreenData())
    }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    state.value.let {
        when(it){
            is MapUIState.Loading -> {
                viewModel.initPlacesClient(LocalContext.current)
            }
            is MapUIState.ScreenDataChanged -> {
                data = it.data
            }

            is MapUIState.SearchError -> {
                scope.launch {
                    snackbarHostState.showSnackbar(it.error, withDismissAction = true)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            SearchBar(
                query = data.placeName,
                onQueryChange = { viewModel.placeNameChanged(it) },
                onSearch = {
                    viewModel.isSearching = true
                    viewModel.searchPlace(data.placeName, context)
                },
                active = false,
                onActiveChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                placeholder = { Text(text = stringResource(R.string.enter_your_location)) },
                leadingIcon = {
                    IconButton(onClick = {
                        navigationRouter.returnBack()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "")
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {
                        if (data.locationChanged){
                            navigationRouter.returnFromMap(data.latitude!!, data.longitude!!)
                        } else {
                            navigationRouter.returnBack()
                        }
                    }) {
                        Icon(imageVector = Icons.Outlined.Done, contentDescription = null)
                    }
                }
            ) {

            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState){
                Snackbar(snackbarData = it, containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.error, dismissActionContentColor = MaterialTheme.colorScheme.error)
            }
        }
    ) {
        MapScreenContent(
            paddingValues = it,
            actions = viewModel,
            data = data,
            latitude = latitude ?: 0.0,
            longitude = longitude ?: 0.0
        )

    }

}

@Composable
fun MapScreenContent(
    paddingValues: PaddingValues,
    actions: MapViewModel,
    data: MapScreenData,
    latitude: Double,
    longitude: Double
){
    // Place location for camera
    val placeLatitude by rememberUpdatedState(newValue = data.place?.latLng?.latitude ?: latitude)
    val placeLongitude by rememberUpdatedState(newValue = data.place?.latLng?.longitude ?: longitude)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(placeLatitude, placeLongitude),10.0f)
    }

    // Change camera on search finish
    LaunchedEffect(placeLatitude, placeLongitude) {
        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(placeLatitude, placeLongitude),13.0f)
    }
    val markerState = rememberMarkerState(position = LatLng(latitude, longitude))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        Box{
            GoogleMap(
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                onMapLongClick = {
                    markerState.position = it
                    actions.onLocationChanged(it.latitude, it.longitude)
                }
            ) {

                MapEffect {
                    it.setOnMarkerDragListener(object : OnMarkerDragListener{
                        override fun onMarkerDrag(p0: Marker) {}

                        override fun onMarkerDragEnd(p0: Marker) {
                            actions.onLocationChanged(p0.position.latitude, p0.position.longitude)
                        }

                        override fun onMarkerDragStart(p0: Marker) {}
                    })
                }

                Marker(state = markerState, draggable = true)
            }
        }
    }
}