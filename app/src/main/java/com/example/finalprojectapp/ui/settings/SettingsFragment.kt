package com.example.finalprojectapp.ui.settings

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.autofill.AutofillManager
import android.widget.Button
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.example.finalprojectapp.MainApplication
import com.example.finalprojectapp.R
import com.example.finalprojectapp.credentialsDB.NotificationRepository
import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.coroutines.launch
import javax.inject.Inject


class SettingsFragment : Fragment() {

    private val REQUEST_CODE_SET_DEFAULT = 1
    @Inject lateinit var mAutoFillManager:AutofillManager
    @Inject lateinit var setting:SharedPreferences
    @Inject lateinit var serviceRepository: ServiceRepository
    @Inject lateinit var notificationRepository: NotificationRepository

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MainApplication).appComponent.uiComponent().create().settingsComponent().create().inject(this)
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
            mAutoFillManager.hasEnabledAutofillServices(),
            CompoundButton.OnCheckedChangeListener { _: CompoundButton?, serviceSet: Boolean ->
                setService(serviceSet)
            }
        )


        setupSettingsSwitch(
            root.settingsSetCheckRepeatedPasswords,
            R.id.settingsSetRepeatedPasswordsLabel,
            R.id.settingsSetRepeatedPasswordsSwitch,
            mAutoFillManager.hasEnabledAutofillServices(),
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
            root.disconnectButton,
            ::disconnectButton
        )

        // Inflate the layout for this fragment
        return root
    }

    private fun setUpSettingsButton(
        button: Button?,
        func: ()->Unit
    ) {
        button?.setOnClickListener {
            func()
        }
    }


    private fun disconnectButton(){
        this.lifecycleScope.launch {
            serviceRepository.nukeALl()
            notificationRepository.nukeAllNotification()
            FirebaseAuth.getInstance().signOut()
            setting.edit().clear().apply()
            requireActivity().findViewById<BottomNavigationView>(R.id.my_nav_view).visibility=View.GONE
            this@SettingsFragment.parentFragmentManager.popBackStack(null,FragmentManager.POP_BACK_STACK_INCLUSIVE)

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
        if (!mAutoFillManager.hasEnabledAutofillServices()) {
            val intent = Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE)
            intent.data = Uri.parse("package:com.example.android.autofill.service")
            startActivityForResult(
                intent,
                REQUEST_CODE_SET_DEFAULT
            )
        }
    }

    private fun disableService() {
        if (mAutoFillManager.hasEnabledAutofillServices())
            mAutoFillManager.disableAutofillServices()
    }

}
