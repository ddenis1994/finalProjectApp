package com.example.finalprojectapp.ui.login

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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.Result
import com.example.finalprojectapp.data.model.LoggedInUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.fragment_login.view.*
import java.io.IOException


class LoginFragment : Fragment() {

    private val TAG ="loginFragment"

    private  val loginViewModel: LoginViewModel by activityViewModels{
        LoginViewModelFactory(requireActivity())
    }
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
        val username = root.username_inner
        val password = root.password_inner
        val login = root.login
        val loading = root.loading
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext().applicationContext, gso)
        auth = FirebaseAuth.getInstance()

        root.google_sigh_in.setOnClickListener{
            loading.visibility = View.VISIBLE
            singInGoogle()
        }

        loginViewModel.loginFormState.observe(viewLifecycleOwner, Observer {
            val loginState = it ?: return@Observer

            if (loginState.signIn) {
                login.text = getString(R.string.action_sign_in)
                login.isEnabled = loginState.isDataValid

            }
            else {
                login.text = getString(R.string.action_register)
                login.isEnabled = true
            }


            if (loginState.usernameError != null) {
                root.username.helperText=getString(loginState.usernameError)
            }
            else
                root.username.helperText=getString(R.string.action_sign_in)
            if (loginState.passwordError != null) {
                root.password.helperText=getString(loginState.passwordError)
            }
            else
                root.password.helperText=""
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
            activity?.setResult(AppCompatActivity.RESULT_OK)
        })
        username.afterTextChanged {
            if(it.isNotEmpty())
                root.password.isEnabled=true
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
                        loginViewModel.login(username.text.toString(),password.text.toString())
                }
                false
            }

            login.setOnClickListener {
                if (loginViewModel.loginFormState.value?.signIn!!) {
                    loading.visibility = View.VISIBLE
                    loginViewModel.login(username.text.toString(), password.text.toString())
                        .observe(viewLifecycleOwner,
                            Observer {
                                loginViewModel.updateResult(it)
                            })
                }
                else{
                    val userNamePassword=username.text.toString()
                    val action =LoginFragmentDirections.actionLoginFragmentToRegisterUserNameFragment(userNamePassword)
                    requireView().findNavController().navigate(action)

                }
            }
        }

        return root
    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome)
        val displayName = model.displayName
        //ToDO make one main navigation
        requireView().findNavController().navigate(R.id.startMainApplication)
        Toast.makeText(
            requireContext(),
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()
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
        else
            loginViewModel.updateResult(Result.Error(Exception("do not have user logged in")))
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
