package com.example.cednik.ui.screens.list_of_journeys

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cednik.R
import com.example.cednik.model.Journey
import com.example.cednik.navigation.INavigationRouter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JourneyListScreen(navigationRouter: INavigationRouter){
    val viewModel = hiltViewModel<JourneyListViewModel>()

    val journeys: MutableList<Journey> = mutableListOf()

    viewModel.journeyListUIState.value.let {
        when(it){
            is JourneyListUIState.Loading -> {
                viewModel.loadJourneys()
            }
            is JourneyListUIState.Success -> {
                journeys.addAll(it.journeys)
            }
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(title = {
                Text(text = stringResource(R.string.your_journeys))
            },
                actions = {
                    IconButton(onClick = { navigationRouter.navigateToAccount() }) {
                        Icon(imageVector = Icons.Outlined.AccountCircle , contentDescription = "")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navigationRouter.navigateToAddEditJourney(null)
            }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        }
    ){
        JourneyListScreenContent(
            paddingValues = it,
            journeys = journeys,
            navigationRouter = navigationRouter
        )
    }
}
@Composable
fun JourneyListScreenContent(
    paddingValues: PaddingValues,
    journeys: List<Journey>,
    navigationRouter: INavigationRouter
) {

    if (journeys.isEmpty()){
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxWidth()) {
            Text(text = stringResource(R.string.you_have_no_journeys), fontSize = MaterialTheme.typography.headlineLarge.fontSize)
        }
    } else {
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            journeys.forEachIndexed { index, journey ->
                item {
                    JourneyListRow(
                        journey = journey, onClick = {
                            navigationRouter.navigateToJourney(journey.id)
                        }
                    )
                    if (index<journeys.lastIndex){
                        Divider()
                    }

                }
            }

        }
    }

}

@Composable
fun JourneyListRow(journey: Journey, onClick: ()->Unit){
    ListItem(
        modifier = Modifier.clickable(onClick=onClick),
        headlineContent = { Text(text = journey.name) }
    )
}