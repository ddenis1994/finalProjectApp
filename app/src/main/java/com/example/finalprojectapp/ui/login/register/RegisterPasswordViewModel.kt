package com.example.finalprojectapp.ui.login.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.finalprojectapp.R
import com.example.finalprojectapp.ui.login.LoginFormState
import java.util.regex.Pattern

class RegisterPasswordViewModel : ViewModel() {

    private val _passwordLoginData = MutableLiveData<LoginFormState>()
    val passwordLoginData: LiveData<LoginFormState> = _passwordLoginData

    private val minLength=6
    private val maxLength=20
    private val stringPattern="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{$minLength,$maxLength}$"
    private val pattern=Pattern.compile(
            stringPattern)


    fun registerPasswordDataChanged(password: String, reapedPassword: String) {
        if(!checkCountPassword(password)){
            _passwordLoginData.value = LoginFormState(usernameError = R.string.invalid_password_length)
        }
        else if (!isPasswordValid(password)) {
            _passwordLoginData.value = LoginFormState(usernameError = R.string.invalid_password)
        }else if (!checkCountPassword(reapedPassword)){
            _passwordLoginData.value = LoginFormState(passwordError = R.string.invalid_password_length)
        }
        else if (!isRepentedPasswordValid(password, reapedPassword)) {
            _passwordLoginData.value = LoginFormState(passwordError = R.string.invalid_password)
        }else if(password != reapedPassword) {
            _passwordLoginData.value = LoginFormState(passwordError = R.string.invalid_reaped_password_not_same)
        }
        else {
            _passwordLoginData.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isRepentedPasswordValid(password: String, reapedPassword: String): Boolean {
        return pattern.matcher(reapedPassword).matches() && isPasswordValid(password)
    }

    private fun checkCountPassword(password:String):Boolean{
        if (password.length in minLength..maxLength)
            return true
        return false
    }

    private fun isPasswordValid(password: String): Boolean {
        return pattern.matcher(password).matches()
    }
}