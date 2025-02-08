package com.example.cednik.navigation

sealed class Destination(val route: String) {
    object LoginScreen : Destination("login")
    object SignUpScreen : Destination("sign_up")
    object PasswordRecoveryScreen : Destination("forgot_password")
    object JourneyListScreen : Destination("journey_list")
    object JourneyScreen : Destination("journey")
    object AddEditJourneyScreen : Destination("add_edit_journey")
    object AccountScreen : Destination("account")
    object MapScreen : Destination("map")
}