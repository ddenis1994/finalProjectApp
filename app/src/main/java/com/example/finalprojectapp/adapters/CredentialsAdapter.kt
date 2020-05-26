package com.example.finalprojectapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.AppAuthActivity
import com.example.finalprojectapp.data.model.adpters.LayoutCredentialView
import com.example.finalprojectapp.databinding.LayoutCredentialBinding
import com.example.finalprojectapp.ui.credentials.inner.CredentialInnerFragment

class CredentialsAdapter(
    private val children: List<LayoutCredentialView>,
    private val credentialsFragment: CredentialInnerFragment,
    private val requestCodeForDecrypt: Int,
    private val requestCodeForDelete:Int,
    private val requestCodeForCopy:Int
) :
    RecyclerView.Adapter<CredentialsAdapter.ViewHolder>() {


    class ViewHolder(
        private val binding: LayoutCredentialBinding,
        private val mContext: CredentialInnerFragment

    ) : RecyclerView.ViewHolder(binding.root) {
        private var bindingPotion: Int = 0
        private var requestCodeForDecrypt: Int = 0
        private var requestCodeForDelete: Int = 0
        private var requestCodeForCopy:Int=0


        init {
            binding.setCopyCredentials {
                val intent = Intent(mContext.context, AppAuthActivity::class.java).apply {
                    putExtra("target", bindingPotion)
                }
                mContext.startActivityForResult(intent, requestCodeForCopy)
            }

            binding.setRevelCredentials {
                val intent = Intent(mContext.context, AppAuthActivity::class.java).apply {
                    putExtra("target", bindingPotion)
                }
                mContext.startActivityForResult(intent, requestCodeForDecrypt)
            }
            binding.setDeleteCredentials {
                val intent = Intent(mContext.context, AppAuthActivity::class.java).apply {
                    putExtra("target", bindingPotion)
                }
                mContext.startActivityForResult(intent, requestCodeForDelete)
            }

        }

        fun bind(
            data: LayoutCredentialView,
            position: Int,
            requestCode: Int,
            requestCodeForDelete: Int,
            requestCodeForCopy: Int
        ) {
            this.bindingPotion = position
            this.requestCodeForDecrypt = requestCode
            this.requestCodeForDelete = requestCodeForDelete
            this.requestCodeForCopy=requestCodeForCopy
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
        holder.bind(data, position, requestCodeForDecrypt,requestCodeForDelete,requestCodeForCopy)
    }
}
