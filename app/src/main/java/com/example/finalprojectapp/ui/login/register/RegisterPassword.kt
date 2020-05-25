package com.example.finalprojectapp.ui.login.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.Result
import com.example.finalprojectapp.data.model.LoggedInUser
import com.example.finalprojectapp.ui.login.LoginViewModel
import com.example.finalprojectapp.ui.login.LoginViewModelFactory
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_register_password.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterPassword.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterPassword : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val registerPasswordArgs:RegisterPasswordArgs by navArgs()
    private val registerPasswordViewModel:RegisterPasswordViewModel by activityViewModels()
    private  val loginViewModel: LoginViewModel by activityViewModels{
        LoginViewModelFactory(requireActivity())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
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


        companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegisterPassword.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterPassword().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
