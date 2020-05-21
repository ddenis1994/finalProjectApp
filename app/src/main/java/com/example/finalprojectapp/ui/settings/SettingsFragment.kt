package com.example.finalprojectapp.ui.settings

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.autofill.AutofillManager
import android.widget.*
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.finalprojectapp.R
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlin.reflect.KFunction0

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var mAutofillManager:AutofillManager
    private val REQUEST_CODE_SET_DEFAULT = 1
    private lateinit var setting:SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        mAutofillManager = getSystemService(requireContext(), AutofillManager::class.java)!!
        setting=SingleEncryptedSharedPreferences().getSharedPreference(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root=inflater.inflate(R.layout.fragment_settings, container, false)

        setupSettingsSwitch(
            root.settingsSetServiceContainer,
            R.id.settingsSetServiceLabel,
            R.id.settingsSetServiceSwitch,
            mAutofillManager.hasEnabledAutofillServices(),
            CompoundButton.OnCheckedChangeListener { compoundButton: CompoundButton?, serviceSet: Boolean ->
                setService(serviceSet)
            }
        )


        setupSettingsSwitch(
            root.settingsSetCheckRepeatedPasswords,
            R.id.settingsSetRepeatedPasswordsLabel,
            R.id.settingsSetRepeatedPasswordsSwitch,
            mAutofillManager.hasEnabledAutofillServices(),
            CompoundButton.OnCheckedChangeListener { _: CompoundButton?, serviceSet: Boolean ->
                setting.edit().putBoolean("RepeatedPasswords",serviceSet).apply()
            }
        )

        setupSettingsSwitch(
            root.settingsSetSecondFactorAuthenticationContainer,
            R.id.settingsSetSecondFactorAuthentication,
            R.id.settingsSetSecondFactorAuthenticationSwitch,
            setting.getBoolean("SecondFactorAuthentication",false),
            CompoundButton.OnCheckedChangeListener { _: CompoundButton?, serviceSet: Boolean ->
                setting.edit().putBoolean("SecondFactorAuthentication",serviceSet).apply()
            }
        )

        setUpSettingsButton(
            root.settings_disconnect_layout,
            root.disconnectButton,
            ::disconnectButton
        )

        // Inflate the layout for this fragment
        return root
    }

    private fun setUpSettingsButton(
        settingsLayout: LinearLayout?,
        button: Button?,
        func: ()->Unit
    ) {
        button?.setOnClickListener {
            func()
        }
    }


    private fun disconnectButton(){
        Log.i("sett","clicked disc")
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setupSettingsSwitch(
        containerId: ViewGroup, labelId: Int, switchId: Int, checked: Boolean,
        checkedChangeListener: CompoundButton.OnCheckedChangeListener) {
        val container: ViewGroup = containerId
        val switchLabel =
            (container.findViewById<View>(labelId) as TextView).text.toString()
        val switchView =
            container.findViewById<Switch>(switchId)
        switchView.contentDescription = switchLabel
        switchView.isChecked = checked
        container.setOnClickListener { switchView.performClick() }
        switchView.setOnCheckedChangeListener(checkedChangeListener)
    }

    private fun setService(enableService: Boolean) {
        if (enableService) {
            startEnableService()
        } else {
            disableService()
        }
    }

    private fun startEnableService() {
        if (!mAutofillManager.hasEnabledAutofillServices()) {
            val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
            intent.data = Uri.parse("package:com.example.android.autofill.service")
            startActivityForResult(
                intent,
                REQUEST_CODE_SET_DEFAULT
            )
        }
    }

    private fun disableService() {
        if (mAutofillManager.hasEnabledAutofillServices())
            mAutofillManager.disableAutofillServices()
    }

}
