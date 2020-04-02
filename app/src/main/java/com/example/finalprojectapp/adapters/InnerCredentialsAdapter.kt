package com.example.finalprojectapp.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.databinding.Credentials1Binding
import com.example.finalstudy.crypto.CredentialEncrypt
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InnerCredentialsAdapter(private val children : List<Credentials>):
    RecyclerView.Adapter<InnerCredentialsAdapter.ViewHolder>(){
    val visibility= MutableLiveData<Boolean>()

    class ViewHolder(private val binding: Credentials1Binding)
        : RecyclerView.ViewHolder(binding.root) {
        init {

            binding.setCopyCredentials {
                binding.username2.text.let { text ->
                    val myClipboard = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val myClip = ClipData.newPlainText("user name", text)
                    myClipboard.setPrimaryClip(myClip)
                    Toast.makeText(it.context, "copy user", Toast.LENGTH_SHORT).show()
                }
            }
            binding.setRevelCredentials {
                val result = MutableLiveData<String>().apply {
                    observeForever {
                        binding.username2.text=it
                        binding.revelCredentialsButton.visibility= View.INVISIBLE
                    }
                }
                GlobalScope.launch {
                    result.postValue(decrepitCredentials(binding.credentialsData!!).value)
                }
            }

            //end init fun
        }


        private fun decrepitCredentials(cre: Credentials): Credentials {
            val encrypted = CredentialEncrypt("password")
            return encrypted.decrypt(cre)
        }

        fun bind(data: Credentials) {
            binding.credentialsData = data
        }

        fun setVisbality(visibility: MutableLiveData<Boolean>) {
            visibility.observeForever(Observer {
                if (it){
                    binding.credentialLayout2.visibility= View.VISIBLE
                }
                else{
                    binding.credentialLayout2.visibility= View.INVISIBLE
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            Credentials1Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        )
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = children[position]
        holder.bind(data)
        holder.setVisbality(visibility)
    }


}