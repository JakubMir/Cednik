package com.example.cednik.ui.screens.login

interface LoginActions {
    fun emailChanged(email: String?)
    fun passwordChanged(password: String?)
    fun login()
    fun forgotPassword()
    fun signUp()
}