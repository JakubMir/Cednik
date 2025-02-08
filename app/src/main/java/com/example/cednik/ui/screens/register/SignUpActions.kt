package com.example.cednik.ui.screens.register

interface SignUpActions {
    fun emailChanged(email: String?)
    fun passwordChanged(password: String?)
    fun passwordAgainChanged(password: String?)
    fun signUp()
}