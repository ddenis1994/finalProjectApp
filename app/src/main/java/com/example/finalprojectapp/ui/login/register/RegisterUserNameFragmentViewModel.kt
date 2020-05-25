package com.example.finalprojectapp.ui.login.register

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.R
import com.example.finalprojectapp.ui.login.LoginFormState

class RegisterUserNameFragmentViewModel : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    fun startCheck() {
        _loginForm.postValue(LoginFormState())
    }


    fun loginDataChanged(username: String, email: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isEmailValid(email)) {
            _loginForm.value = LoginFormState(passwordError = R.string.fui_invalid_email_address)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean =
        username.isNotEmpty()


    private fun isEmailValid(email: String): Boolean =
        email.contains('@') && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}