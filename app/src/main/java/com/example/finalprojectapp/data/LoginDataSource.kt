package com.example.finalprojectapp.data

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.MutableLiveData
import com.example.finalprojectapp.data.model.LoggedInUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest


/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource(private val activity: FragmentActivity) {

    private val auth = FirebaseAuth.getInstance()


    fun login(username: String, password: String): MutableLiveData<Result<LoggedInUser>> {
        val resultData = MutableLiveData<Result<LoggedInUser>>()
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    resultData.postValue(auth.currentUser!!.displayName?.let {
                        LoggedInUser(auth.currentUser!!.uid, it)
                    }?.let { Result.Success(it) })
                } else {
                    resultData.postValue(Result.Error(Exception("cannot log in")))
                }
            }
        return resultData
    }


    fun logout() {
        auth.signOut()
    }

    fun createNewAccent(
        username: String,
        email: String,
        password: String
    ): MutableLiveData<Result<LoggedInUser>> {
        val registrationData = MutableLiveData<Result<LoggedInUser>>()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { registration ->
            if (registration.isSuccessful) {
                val profileProfileUpdates: UserProfileChangeRequest =
                    UserProfileChangeRequest.Builder().apply {
                        displayName = username
                    }.build()
                auth.currentUser!!.updateProfile(profileProfileUpdates).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val result =
                            auth.currentUser?.uid?.let { it1 -> LoggedInUser(it1, username) }
                        if (result != null)
                            registrationData.postValue(Result.Success(result))
                    }
                }
            }

        }

        return registrationData
    }
}

