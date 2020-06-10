package com.example.finalprojectapp.data

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.finalprojectapp.data.model.LoggedInUser

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(private val dataSource: LoginDataSource, private val lifecycleOwner: LifecycleOwner) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }



    fun login(
        username: String,
        password: String
    ): MutableLiveData<Result<LoggedInUser>> {
        // handle login
        val result = dataSource.login(username, password)
        result.observe(lifecycleOwner, Observer {
            if(it is Result.Success)
                setLoggedInUser(it.data)
        })

        return result
    }

    private fun setLoggedInUser(loggedInUser: LoggedInUser) {
        this.user = loggedInUser

    }

    fun register(username: String, email: String, password: String): MutableLiveData<Result<LoggedInUser>> {
        return dataSource.createNewAccent(username,email,password)
    }
}
