package com.example.cednik.ui.screens.register

sealed class SignUpUIState {
    class Loading : SignUpUIState()
    class SignedUp : SignUpUIState()
    class ScreenDataChanged(val data: SignUpScreenData) : SignUpUIState()
    class ReturnBack() : SignUpUIState()
}