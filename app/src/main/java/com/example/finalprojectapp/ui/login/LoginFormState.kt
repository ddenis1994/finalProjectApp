package com.example.finalprojectapp.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val signIn:Boolean=false,
    val isDataValid: Boolean = false
)
