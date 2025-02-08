package com.example.cednik.navigation

import androidx.navigation.NavController
import com.example.cednik.model.Journey
import com.example.cednik.model.Location
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi

class NavigationRouterImpl(private val navController: NavController) : INavigationRouter {

    override fun navigateToLogin() {
        navController.navigate(Destination.LoginScreen.route)
    }

    override fun navigateToSignUp() {
        navController.navigate(Destination.SignUpScreen.route)
    }

    override fun navigateToPasswordRecovery() {
        navController.navigate(Destination.PasswordRecoveryScreen.route)
    }
    override fun navigateToJourneyListScreen() {
        navController.navigate(Destination.JourneyListScreen.route)
    }

    override fun navigateToAddEditJourney(id: Long?) {
        if (id != null) {
            navController.navigate(Destination.AddEditJourneyScreen.route + "/" + id)
        } else {
            navController.navigate(Destination.AddEditJourneyScreen.route)
        }
    }



    override fun navigateToJourney(id: Long?) {
        if (id != null) {
            navController.navigate(Destination.JourneyScreen.route + "/" + id)
        }
    }

    override fun navigateToAccount() {
        navController.navigate(Destination.AccountScreen.route)
    }

    override fun returnBack() {
        navController.popBackStack()
    }

    override fun navigateToMap(latitude: Double?, longitude: Double?) {
        if (latitude != null && longitude != null) {
            val moshi: Moshi = Moshi.Builder().build()
            val jsonAdapter: JsonAdapter<Location> = moshi.adapter(Location::class.java)
            navController.navigate(Destination.MapScreen.route + "/" + jsonAdapter.toJson(Location(latitude, longitude)))
        } else {
            navController.navigate(Destination.MapScreen.route)
        }
    }

    override fun returnFromMap(latitude: Double, longitude: Double) {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Location> = moshi.adapter(Location::class.java)

        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("location", jsonAdapter.toJson(Location(latitude, longitude)))
        returnBack()
    }

    override fun returnFromAddEditJourney(journey: Journey) {
        val moshi: Moshi = Moshi.Builder().build()
        val jsonAdapter: JsonAdapter<Journey> = moshi.adapter(Journey::class.java)

        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set("journey", jsonAdapter.toJson(journey))
        returnBack()
    }

    override fun getNavController(): NavController {
        return navController
    }
}