package com.example.cednik.navigation

import androidx.navigation.NavController
import com.example.cednik.model.Journey

interface INavigationRouter {
    fun navigateToLogin()
    fun navigateToSignUp()
    fun navigateToPasswordRecovery()
    fun navigateToAddEditJourney(id: Long?)
    fun navigateToJourneyListScreen()
    fun navigateToJourney(id: Long?)
    fun navigateToAccount()
    fun returnBack()
    fun navigateToMap(latitude: Double?, longitude: Double?)
    fun returnFromMap(latitude: Double, longitude: Double)
    fun returnFromAddEditJourney(journey: Journey)
    fun getNavController(): NavController
}