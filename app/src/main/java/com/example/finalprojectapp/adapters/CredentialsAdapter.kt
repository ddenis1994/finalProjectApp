package com.example.finalprojectapp.adapters

import android.content.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.AppAuthActivity
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.databinding.LayoutCredentialBinding
import com.example.finalprojectapp.ui.credentials.inner.CredentialInnerFragment

class CredentialsAdapter(
    private val children: List<LayoutCredentialView>,
    private val credentialsFragment: CredentialInnerFragment,
    private val requestCode: Int
) :
    RecyclerView.Adapter<CredentialsAdapter.ViewHolder>() {


    class ViewHolder(
        private val binding: LayoutCredentialBinding,
        private val mContext: CredentialInnerFragment

    ) : RecyclerView.ViewHolder(binding.root) {
        private var bindingPotion: Int = 0
        private var requestCodeForDecrypt: Int = 0


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
                val intent = Intent(mContext.context, AppAuthActivity::class.java).apply {
                    putExtra("target", bindingPotion)
                }
                mContext.startActivityForResult(intent, requestCodeForDecrypt)


            }

        }


        fun bind(
            data: LayoutCredentialView,
            position: Int,
            requestCode: Int
        ) {
            bindingPotion = position
            requestCodeForDecrypt = requestCode
            binding.apply {
                if (data.iv.isNullOrEmpty()) {
                    binding.credentialRevel.visibility = View.GONE
                    binding.credentialCopy.visibility = View.VISIBLE
                    binding.credentialText.text=data.data
                }
                else {
                    credentialText.text = data.hintList?.get(0)
                    credentialsData = data
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutCredentialBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), credentialsFragment
        )
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = children[position]
        holder.bind(data, position, requestCode)
    }
}
