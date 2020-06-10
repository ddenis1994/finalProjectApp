package com.example.finalprojectapp.ui.settings

import android.content.Context
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.finalprojectapp.MainApplication
import com.example.finalprojectapp.R
import com.example.finalprojectapp.credentialsDB.NotificationRepository
import com.example.finalprojectapp.credentialsDB.ServiceRepository
import com.example.finalprojectapp.workers.ChangeEncryptionWorker
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.android.synthetic.main.fragment_settings.view.encryption_spinner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject


@Suppress("PrivatePropertyName")
class SettingsFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val TAG: String = "settings_fragment"
    private val REQUEST_CODE_SET_DEFAULT = 1

    @Inject
    lateinit var mAutoFillManager: AutofillManager

    @Inject
    lateinit var setting: SharedPreferences

    @Inject
    lateinit var serviceRepository: ServiceRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var scope: CoroutineScope

    lateinit var root: View

    private var secondFactorMethod = ""

    private var encryptionMethod = ""

    private var tempSecondFactorMethod:String = ""

    private var tempEncryptionMethod:String = ""

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MainApplication).appComponent.uiComponent().create()
            .settingsComponent().create().inject(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_settings, container, false)

        setupSettingsSwitch(
            root.settingsSetServiceContainer,
            R.id.settingsSetServiceLabel,
            R.id.settingsSetServiceSwitch,
            mAutoFillManager.hasEnabledAutofillServices(),
            CompoundButton.OnCheckedChangeListener { _: CompoundButton?, serviceSet: Boolean ->
                setService(serviceSet)
            }
        )
        //get values
        secondFactorMethod = setting.getString("SecondFactorAuthentication", "None") ?: ""
        encryptionMethod = setting.getString("encryptionMethod", "AES 128") ?: ""


        setupSettingsSwitch(
            root.settingsSetCheckRepeatedPasswords,
            R.id.settingsSetRepeatedPasswordsLabel,
            R.id.settingsSetRepeatedPasswordsSwitch,
            mAutoFillManager.hasEnabledAutofillServices(),
            CompoundButton.OnCheckedChangeListener { _: CompoundButton?, serviceSet: Boolean ->
                setting.edit().putBoolean("RepeatedPasswords", serviceSet).apply()
            }
        )

//        setupSettingsSwitch(
//            root.settingsSetSecondFactorAuthenticationContainer,
//            R.id.settingsSetSecondFactorAuthentication,
//            R.id.settingsSetSecondFactorAuthenticationSwitch,
//            setting.getBoolean("SecondFactorAuthentication", false),
//            CompoundButton.OnCheckedChangeListener { _: CompoundButton?, serviceSet: Boolean ->
//                setting.edit().putBoolean("SecondFactorAuthentication", serviceSet).apply()
//            }
//        )
//set spinner for encryption
        val encryptionSpinner: Spinner = root.encryption_spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.encryption_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            encryptionSpinner.adapter = adapter
        }
        encryptionSpinner.onItemSelectedListener = this

        //set spinner for second factor
        val secondFactorSpinner: Spinner = root.second_factor_spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.second_factor,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            secondFactorSpinner.adapter = adapter
        }
        secondFactorSpinner.onItemSelectedListener = this


        setUpSettingsButton(
            root.disconnectButton,
            ::disconnectButton
        )

        setUpSettingsButton(
            root.second_factor_confirm_button,
            ::change2FactorMethod
        )
        setUpSettingsButton(
            root.change_encryption_confirm_button,
            ::changeEncryptionType
        )


        return root
    }

    private fun setUpSettingsButton(
        button: Button?,
        func: () -> Unit
    ) {
        button?.setOnClickListener {
            func()
        }
    }


    private fun disconnectButton() {
        this.lifecycleScope.launch {
            serviceRepository.nukeALl()
            notificationRepository.nukeAllNotification()
            FirebaseAuth.getInstance().signOut()
            setting.edit().clear().apply()
            requireActivity().findViewById<BottomNavigationView>(R.id.my_nav_view).visibility =
                View.GONE
            this@SettingsFragment.parentFragmentManager.popBackStack(
                null,
                FragmentManager.POP_BACK_STACK_INCLUSIVE
            )

        }


    }

    private fun changeEncryptionType() {
        val newEncryptionType=tempEncryptionMethod
        setting.edit().putBoolean("changeEncryptionType",true).apply()
        val newEncryption= workDataOf("encryptionType" to newEncryptionType)
        val changeEncryptionWorker= OneTimeWorkRequestBuilder<ChangeEncryptionWorker>()
            .setInputData(newEncryption)
            .addTag("ChangeEncryptionWorker")
            .build()
        WorkManager.getInstance(requireContext()).enqueue(changeEncryptionWorker)
    }

    private fun change2FactorMethod() {
        setting.edit().putString("SecondFactorAuthentication",tempSecondFactorMethod).apply()
        secondFactorMethod=tempSecondFactorMethod
        root.second_factor_confirm_button.visibility=View.GONE
        tempSecondFactorMethod=""

    }

    private fun setupSettingsSwitch(
        containerId: ViewGroup, labelId: Int, switchId: Int, checked: Boolean,
        checkedChangeListener: CompoundButton.OnCheckedChangeListener
    ) {
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

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        val selected = parent?.getItemAtPosition(position)
        when (parent?.id) {
            R.id.encryption_spinner -> {
                val confirm = root.change_encryption_confirm_button
                if (selected != encryptionMethod) {
                    confirm.visibility = View.VISIBLE
                    tempEncryptionMethod=selected.toString()
                }
                else {
                    confirm.visibility = View.GONE
                    tempEncryptionMethod=""
                }

                Log.e(TAG, "onItemSelected: $selected")

            }
            R.id.second_factor_spinner -> {
                val confirm = root.second_factor_confirm_button
                if (selected != secondFactorMethod) {
                    confirm.visibility = View.VISIBLE
                    tempSecondFactorMethod=selected.toString()
                }
                else {
                    confirm.visibility = View.GONE
                    tempSecondFactorMethod=""
                }
            }
            else -> Log.e(TAG, "onItemSelected: cannot detrninate")

        }

    }

}
