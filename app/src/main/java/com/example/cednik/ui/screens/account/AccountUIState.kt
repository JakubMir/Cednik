package com.example.cednik.ui.screens.account

sealed class AccountUIState {
    class Loading : AccountUIState()
    class ChangedCredentials : AccountUIState()
    class LoggedOut : AccountUIState()
    class ScreenDataChanged(val data: AccountScreenData) : AccountUIState()
    class ReturnBack : AccountUIState()
}