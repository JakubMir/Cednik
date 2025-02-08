package com.example.cednik.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cednik.model.Location
import com.example.cednik.ui.screens.account.AccountScreen
import com.example.cednik.ui.screens.add_edit_journey.AddEditJourneyScreen
import com.example.cednik.ui.screens.list_of_journeys.JourneyListScreen
import com.example.cednik.ui.screens.journey_detail.JourneyScreen
import com.example.cednik.ui.screens.login.LoginScreen
import com.example.cednik.ui.screens.map.MapScreen
import com.example.cednik.ui.screens.password_recovery.PasswordRecoveryScreen
import com.example.cednik.ui.screens.register.SignUpScreen
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

@Composable
fun NavGraph(
    navHostController: NavHostController = rememberNavController(),
    navigationRouter: INavigationRouter = remember {
        NavigationRouterImpl(navHostController)
    },
    startDestination: String
){

    NavHost(navController = navHostController, startDestination = startDestination) {
        composable(Destination.LoginScreen.route){
            LoginScreen(navigationRouter = navigationRouter)
        }

        composable(Destination.PasswordRecoveryScreen.route){
            PasswordRecoveryScreen(navigationRouter = navigationRouter)
        }

        composable(Destination.SignUpScreen.route){
            SignUpScreen(navigationRouter = navigationRouter)
        }

        composable(Destination.JourneyListScreen.route){
            JourneyListScreen(navigationRouter = navigationRouter)
        }

        composable(
            Destination.AddEditJourneyScreen.route + "/{id}",
            arguments = listOf(
                navArgument(name = "id"){
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ){
            val id = it.arguments?.getLong("id")
            AddEditJourneyScreen(navigationRouter = navigationRouter, id = id)
        }

        composable(
            Destination.AddEditJourneyScreen.route
        ){
            AddEditJourneyScreen(navigationRouter = navigationRouter, id = null)
        }

        composable(
            Destination.JourneyScreen.route + "/{id}",
            arguments = listOf(
                navArgument(name = "id"){
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ){
            val id = it.arguments!!.getLong("id")
            JourneyScreen(navigationRouter = navigationRouter, id = id)
        }

        composable(Destination.AccountScreen.route){
            AccountScreen(navigationRouter = navigationRouter)
        }

        composable(Destination.MapScreen.route + "/{location}",
            arguments = listOf(
                navArgument("location") {
                    type = NavType.StringType
                    defaultValue = ""
                })
        ) {
            val locationString = it.arguments?.getString("location")
            if (!locationString.isNullOrEmpty()) {
                val moshi: Moshi = Moshi.Builder().build()
                val jsonAdapter: JsonAdapter<Location> = moshi.adapter(Location::class.java)
                val location = jsonAdapter.fromJson(locationString)
                MapScreen(navigationRouter = navigationRouter, latitude = location!!.latitude, longitude = location.longitude!!.toDouble())
            } else {
                // do Composable funkce už null poslat můžu.
                MapScreen(navigationRouter = navigationRouter, latitude = null, longitude = null)
            }
        }


        composable(Destination.MapScreen.route) {
            MapScreen(navigationRouter = navigationRouter, latitude = null, longitude = null)
        }

    }

}
