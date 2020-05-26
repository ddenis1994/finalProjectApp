package com.example.finalprojectapp.ui.credentials.inner

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.R
import com.example.finalprojectapp.adapters.CredentialsAdapter
import com.example.finalprojectapp.utils.InjectorUtils
import kotlinx.android.synthetic.main.fragment_inner_credential.*
import kotlinx.android.synthetic.main.fragment_inner_credential.view.*
import java.util.*


class CredentialInnerFragment : Fragment() {
    private val requestCodeDecryptCredential = 3000
    private val requestCodeDeleteCredential = 3100
    private val requestCodeCopyCredential = 3010

    private var dataSetId: Long = 0
    private lateinit var dataSetName: String
    private lateinit var serviceName: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    companion object {
        fun newInstance() =
            CredentialInnerFragment()
    }

    private val args: CredentialInnerFragmentArgs by navArgs()
    private val viewModel: CredentialInnerViewModel by activityViewModels {
        InjectorUtils.provideCredentialInnerViewModelFactory(this)
    }


    @ExperimentalStdlibApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val root = inflater.inflate(R.layout.fragment_inner_credential, container, false)
        serviceName = args.serviceName
        dataSetName = args.dataSetName

        val temp = serviceName
            .replace(".", " ", false)
            .split(" ")
        root.credential_service_name.text = temp[temp.size - 1].capitalize(Locale.ROOT)
        root.credential_data_set_name.text = dataSetName.toUpperCase(Locale.ROOT)

        dataSetId = args.dataSetId

        val firstTimeCredentials = viewModel.firstTimeCredentials(dataSetId)

        firstTimeCredentials.observe(viewLifecycleOwner, Observer {
            viewModel.setData(it)
        })

        viewModel.data.observe(viewLifecycleOwner, Observer {
            viewAdapter = CredentialsAdapter(
                it,
                this,
                requestCodeDecryptCredential,
                requestCodeDeleteCredential,
                requestCodeCopyCredential
            )
            recyclerView.apply {
                adapter = viewAdapter
            }
        })
        return root
    }

    override fun onStart() {
        super.onStart()
        viewManager = LinearLayoutManager(context)
        recyclerView = credentials_recycler_View.apply {
            setHasFixedSize(true)
            layoutManager = viewManager

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val target = data?.getIntExtra("target", -1)!!
            when (requestCode) {
                requestCodeDecryptCredential -> {
                    if (target != -1)
                        viewModel.updateData(target)

                }
                requestCodeCopyCredential -> {
                    val myClipboard =requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val myClip = ClipData.newPlainText("user name", viewModel.data.value?.get(target)?.data)
                    myClipboard.setPrimaryClip(myClip)
                    Toast.makeText(requireContext(), "copy user", Toast.LENGTH_SHORT).show()
                }
                requestCodeDeleteCredential ->{
                    viewModel.deleteCredential(viewModel.data.value?.get(target)?.id,dataSetId)
                }
            }

        }


    }


}
