package com.example.finalprojectapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.example.finalprojectapp.data.LoginRepository
import com.example.finalprojectapp.data.Result

import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.model.LoggedInUser
import com.google.android.material.textfield.TextInputEditText

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData(
        LoginFormState()
    )
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private var _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    var loginInPageState=0

    fun login(username: String, password: String): LiveData<Result<LoggedInUser>> {
        return loginRepository.login(username, password)
    }

    fun updateResult(result:Result<LoggedInUser>){
        if (result is Result.Success) {
            _loginResult.value =
                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
        } else {
            _loginResult.value = LoginResult()
        }
    }


    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username,signIn = false)
        } else if (!isPasswordValid(password)) {
            if (password.isNotEmpty())
                _loginForm.value = LoginFormState(passwordError = R.string.invalid_password,signIn = true)
            else
                _loginForm.value = LoginFormState(passwordError = R.string.invalid_password,signIn = false)
        }
        else {
            _loginForm.value = LoginFormState(isDataValid = true,signIn = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun register(
        username: String,
        email: String,
        password: String
    ): MutableLiveData<Result<LoggedInUser>> {
        return loginRepository.register(username,email,password)
    }
}
