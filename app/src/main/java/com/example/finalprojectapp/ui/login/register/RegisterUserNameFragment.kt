package com.example.finalprojectapp.ui.login.register

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.finalprojectapp.R
import com.example.finalprojectapp.ui.login.afterTextChanged
import kotlinx.android.synthetic.main.fragment_register_user_name.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterUserNameFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterUserNameFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val registerUserNameFragmentArgs: RegisterUserNameFragmentArgs by navArgs()
    private val registerUserNameFragmentViewModel: RegisterUserNameFragmentViewModel by activityViewModels()

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
        val root = inflater.inflate(R.layout.fragment_register_user_name, container, false)
        checkTypeInput(root, registerUserNameFragmentArgs.userNamePassword)

        root.register_user_name.afterTextChanged {
            if (registerUserNameFragmentViewModel.loginFormState.value!=null)
                registerUserNameFragmentViewModel.loginDataChanged(
                    root.register_user_name.text.toString(),
                    root.register_email.text.toString()
                )
            else
                registerUserNameFragmentViewModel.startCheck()
        }

        root.register_email.afterTextChanged {
            if (registerUserNameFragmentViewModel.loginFormState.value!=null)
                registerUserNameFragmentViewModel.loginDataChanged(
                    root.register_user_name.text.toString(),
                    root.register_email.text.toString()
                )
            else
                registerUserNameFragmentViewModel.startCheck()
        }

        root.register_button_1.setOnClickListener {
            val username = root.register_user_name.text
            val email = root.register_email.text
            val action =
                RegisterUserNameFragmentDirections.actionRegisterUserNameFragementToRegisterPassword(
                    username.toString(),
                    email.toString()
                )
            requireView().findNavController().navigate(action)
        }
        registerUserNameFragmentViewModel.loginFormState.observe(viewLifecycleOwner, Observer {
            val loginState = it ?: return@Observer

            root.register_button_1.isEnabled = loginState.isDataValid


            if (loginState.usernameError != null) {
                root.username_register_layout.helperText = getString(loginState.usernameError)
            } else
                root.username_register_layout.helperText = ""
            if (loginState.passwordError != null) {
                root.email_register_layout.helperText = getString(loginState.passwordError)
            } else
                root.email_register_layout.helperText = ""

        })
        return root
    }


    private fun checkTypeInput(root: View?, userNamePassword: String) {
        if (userNamePassword.contains('@') && Patterns.EMAIL_ADDRESS.matcher(userNamePassword)
                .matches()
        ) {
            root?.register_email?.setText(userNamePassword)

        } else if (userNamePassword.isNotBlank()) {
            root?.register_user_name?.setText(userNamePassword)
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
         * @return A new instance of fragment RegisterUserNameFragement.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegisterUserNameFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}
