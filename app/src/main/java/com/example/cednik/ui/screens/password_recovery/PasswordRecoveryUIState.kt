package com.example.cednik.ui.screens.password_recovery

sealed class PasswordRecoveryUIState {
    class Loading : PasswordRecoveryUIState()
    class Recovered() : PasswordRecoveryUIState()
    class ScreenDataChanged(val data: PasswordRecoveryScreenData) : PasswordRecoveryUIState()
    class ReturnBack() : PasswordRecoveryUIState()
}