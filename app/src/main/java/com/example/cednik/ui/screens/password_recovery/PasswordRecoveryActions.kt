package com.example.cednik.ui.screens.password_recovery

interface PasswordRecoveryActions {
    fun emailChanged(email: String?)
    fun recoverPassword()
}