package com.example.finalprojectapp.adapters

import android.content.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.AppAuthActivity
import com.example.finalprojectapp.crypto.Cryptography
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.databinding.LayoutCredentialBinding
import com.example.finalprojectapp.ui.credentials.inner.CredentialInnerFragment
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

class CredentialsAdapter(
    private val children: List<LayoutCredentialView>,
    private val viewLifecycleOwner: LifecycleOwner,
    private val credentialsFragment: CredentialInnerFragment
):
    RecyclerView.Adapter<CredentialsAdapter.ViewHolder>() {


    class ViewHolder(
        private val binding: LayoutCredentialBinding,
        private val viewLifecycleOwner: LifecycleOwner,
        private val mContext: CredentialInnerFragment

    ) : RecyclerView.ViewHolder(binding.root) {

        private var bindingPotion: Int=0

        private lateinit var setting: SharedPreferences

        private val executor: Executor = ContextCompat.getMainExecutor(mContext.requireContext())
        private val biometricPrompt: BiometricPrompt = BiometricPrompt(mContext, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        mContext.requireContext(),
                        "Authentication error: $errString", Toast.LENGTH_SHORT
                    )
                        .show()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    startDecryption()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    Toast.makeText(
                        mContext.requireContext(), "Authentication failed",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        private var promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        init {

            binding.setCopyCredentials {
                binding.credentialText.text.let { text ->
                    val myClipboard =
                        it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val myClip = ClipData.newPlainText("user name", text)
                    myClipboard.setPrimaryClip(myClip)
                    Toast.makeText(it.context, "copy user", Toast.LENGTH_SHORT).show()
                }
            }
            binding.setRevelCredentials {
                val intent=Intent(mContext.context, AppAuthActivity::class.java).apply {
                    putExtra("target", bindingPotion)
                }
                mContext.startActivityForResult(intent,9002)

                setting =
                    SingleEncryptedSharedPreferences().getSharedPreference(mContext.requireContext())
                when (setting.getBoolean("SecondFactorAuthentication", false)) {
                    true -> {
                        biometricPrompt.authenticate(promptInfo)
                    }
                    false -> {
                        startDecryption()
                    }
                }
            }

        }

        private fun startDecryption() {
            val result2 = MutableLiveData<String>().apply {
                observe(viewLifecycleOwner, Observer {
                    binding.credentialText.text = it
                    binding.credentialRevel.visibility = View.GONE
                    binding.credentialCopy.visibility = View.VISIBLE
                })
            }
            viewLifecycleOwner.lifecycleScope.launch {
                result2.postValue(binding.credentialsData?.data?.let { it1 ->
                    Credentials().copy(
                        data = it1,
                        iv = binding.credentialsData?.iv
                    )
                }
                    ?.let { it2 -> decrepitCredentials(it2).data })
            }
        }

        private fun decrepitCredentials(cre: Credentials): Credentials {
            val cryptography = Cryptography(null)
            return cryptography.decryptLocalSingleCredentials(cre)!!
        }

        fun bind(
            data: LayoutCredentialView,
            position: Int
        ) {
            bindingPotion=position
            binding.apply {
                credentialsData=data
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutCredentialBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ),viewLifecycleOwner,credentialsFragment)
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = children[position]
        holder.bind(data,position)
    }
}
