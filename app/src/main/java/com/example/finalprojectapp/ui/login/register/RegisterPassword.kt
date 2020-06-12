package com.example.finalprojectapp.ui.login.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.Result
import com.example.finalprojectapp.data.model.LoggedInUser
import com.example.finalprojectapp.ui.login.LoginViewModel
import com.example.finalprojectapp.ui.login.LoginViewModelFactory
import kotlinx.android.synthetic.main.fragment_register_password.view.*



class RegisterPassword : Fragment() {

    private val registerPasswordArgs:RegisterPasswordArgs by navArgs()
    private val registerPasswordViewModel:RegisterPasswordViewModel by activityViewModels()
    private  val loginViewModel: LoginViewModel by activityViewModels{
        LoginViewModelFactory(requireActivity())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root=inflater.inflate(R.layout.fragment_register_password, container, false)
        val userName=registerPasswordArgs.username
        val email=registerPasswordArgs.email
        val password=root.register_password
        val repeatedPassword=root.register_password_repeat



        registerPasswordViewModel.passwordLoginData.observe(viewLifecycleOwner, Observer {

            root.register_button_2.isEnabled=it.isDataValid

            if (it.usernameError!=null) {
                root.register_password_layout.isErrorEnabled = true
                root.register_password_layout.error = getString(it.usernameError)
            }
            else {
                root.register_password_layout.isErrorEnabled = false
                root.register_password_layout.error = ""
            }
            if(it.passwordError!=null) {
                root.register_password_repeat_layout.isErrorEnabled = true
                root.register_password_repeat_layout.error = getString(it.passwordError)
            }
            else {
                root.register_password_repeat_layout.isErrorEnabled = false
                root.register_password_repeat_layout.error = ""
            }

        })

        password.afterTextChanged {
            registerPasswordViewModel.registerPasswordDataChanged(
                password.text.toString(),
                repeatedPassword.text.toString()
            )
        }

        repeatedPassword.afterTextChanged {
            registerPasswordViewModel.registerPasswordDataChanged(
                password.text.toString(),
                repeatedPassword.text.toString()
            )
        }

        root.register_button_2.setOnClickListener {
            root.register_loading.visibility=View.VISIBLE
            root.register_button_2.visibility=View.GONE
            loginViewModel.register(userName,email,password.text.toString()).observe(
                viewLifecycleOwner, Observer {
                        updateUI(it,root)
                }
            )
        }
        return root
    }

    private fun updateUI(
        result: Result<LoggedInUser>?,
        root: View
    ) {
        root.register_loading.visibility=View.GONE
        root.register_button_2.visibility=View.VISIBLE
        if (result is Result.Success){
            val welcome = getString(R.string.welcome)
            val displayName = result.data.displayName
            requireView().findNavController().navigate(R.id.startMainApplication)
            Toast.makeText(
                requireContext(),
                "$welcome $displayName",
                Toast.LENGTH_LONG
            ).show()
        }
        else{
            root.register_text_error.visibility=View.VISIBLE
        }
    }

    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })
    }



}
