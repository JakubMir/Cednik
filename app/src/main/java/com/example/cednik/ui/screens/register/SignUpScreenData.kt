package com.example.cednik.ui.screens.register

import com.example.cednik.model.User

class SignUpScreenData {
    var user: User = User ("", "")
    var passwordAgain: String = ""
    var emailError: String? = null
    var passwordError: String? = null
    var passwordAgainError: String? = null
}