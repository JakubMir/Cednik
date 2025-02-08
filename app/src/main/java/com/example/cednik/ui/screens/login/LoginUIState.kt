package com.example.cednik.ui.screens.login

sealed class LoginUIState {
    class Loading : LoginUIState()
    class Success : LoginUIState()
    class Logged : LoginUIState()
    class ScreenDataChanged(val data: LoginScreenData) : LoginUIState()
    class NavigateToPasswordRecovery() : LoginUIState()
    class NavigateToSignUp() : LoginUIState()
}