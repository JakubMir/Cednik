package com.example.cednik.ui.screens.account

interface AccountActions {
    fun emailChanged(email: String?)
    fun passwordChanged(password: String?)
    fun confirmChanges()
    fun logOut()
}