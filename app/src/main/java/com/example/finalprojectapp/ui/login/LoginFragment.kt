package com.example.finalprojectapp.ui.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.Result
import com.example.finalprojectapp.data.model.*
import com.example.finalprojectapp.localDB.PasswordRoomDatabase
import com.example.finalprojectapp.workers.DBWorkerDecryption
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException


class LoginFragment : Fragment() {

    private val TAG ="loginFragment"
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN:Int = 9001
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val root = inflater.inflate(R.layout.fragment_login, container, false)
        val username = root.username
        val password = root.password
        val login = root.login
        val loading = root.loading
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        Log.i(TAG,getString(R.string.default_web_client_id))
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext().applicationContext, gso)
        auth = FirebaseAuth.getInstance()

        root.google_sigh_in.setOnClickListener{
            loading.visibility = View.VISIBLE
            singInGoogle()
        }





        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(viewLifecycleOwner, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

        loginViewModel.loginResult.observe(viewLifecycleOwner, Observer {
            val loginResult = it ?: return@Observer
            loading.visibility = View.GONE
            if (loginResult.error != null) {
                showLoginFailed(loginResult.error)
            }
            if (loginResult.success != null) {
                updateUiWithUser(loginResult.success)
            }
            activity?.setResult(Activity.RESULT_OK)
        })
        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }
        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }

            login.setOnClickListener {
                loading.visibility = View.VISIBLE
                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }

        return root
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        getDataFromServer()
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        requireView().findNavController().navigate(R.id.startMainApplication)
        Toast.makeText(
            requireContext(),
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
    }
    private fun getDataFromServer() {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!
        db.collection("users").document(user.uid)
            .collection("services").get()
            .addOnSuccessListener {
                val result = it.toObjects<Service>()
                val localDB = PasswordRoomDatabase.getDatabase(requireContext())
                //TODO change the got data from hare (cannot get all data every time)
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            localDB.localCredentialsDAO().insertServiceCredentials(result)
                            with (requireContext().getSharedPreferences("mainPreferences", Context.MODE_PRIVATE).edit()){
                                putBoolean("encrypted", true)
                                commit()
                            }
                            val updateWorkRequest = OneTimeWorkRequestBuilder<DBWorkerDecryption>()
                                .build()
                            WorkManager.getInstance(requireContext()).enqueue(updateWorkRequest)
                        }
                }
            }

    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(requireContext(), errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if(currentUser!= null) {
            val user = LoggedInUser(java.util.UUID.randomUUID().toString(), currentUser.displayName.toString())
            val res= Result.Success(user)
            loginViewModel.updateResult(res)
        }
    }

    private fun singInGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            loginViewModel.loginResult.value?.success
            firebaseAuthWithGoogle(account!!)
        } catch (e: ApiException) {
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            loginViewModel.loginResult.value?.error
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this.requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = LoggedInUser(java.util.UUID.randomUUID().toString(), acct.displayName.toString())
                    val res= Result.Success(user)
                    loginViewModel.updateResult(res)
                }
                else {
                    auth.createUserWithEmailAndPassword(acct.email.toString(), "test5656")
                        .addOnCompleteListener(this.requireActivity()) { LoginTask ->
                            if (LoginTask.isSuccessful) {
                                val user = LoggedInUser(java.util.UUID.randomUUID().toString(), acct.displayName.toString())
                                val res= Result.Success(user)
                                loginViewModel.updateResult(res)
                            } else {
                                val res= Result.Error(IOException("Error logging in", LoginTask.exception))
                                loginViewModel.updateResult(res)
                                Log.w(TAG, "createUserWithEmail:failure",LoginTask.exception)
                            }

                        }
                }
            }
    }


}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })

}
