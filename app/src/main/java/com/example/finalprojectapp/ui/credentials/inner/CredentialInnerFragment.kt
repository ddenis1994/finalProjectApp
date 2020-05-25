package com.example.finalprojectapp.ui.credentials.inner

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.finalprojectapp.R
import com.example.finalprojectapp.adapters.CredentialsAdapter
import com.example.finalprojectapp.utils.InjectorUtils


class CredentialInnerFragment : Fragment() {

    private var dataSetId:Long=0
    private lateinit var credentialsAdapter: CredentialsAdapter

    companion object {
        fun newInstance() =
            CredentialInnerFragment()
    }

    private val args:CredentialInnerFragmentArgs by navArgs()
    private val viewModel:CredentialInnerViewModel by activityViewModels {
        InjectorUtils.provideCredentialInnerViewModelFactory(this)
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inner_credential, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dataSetId=args.dataSetId
        val localData= viewModel.getCrede(dataSetId)
        localData.observe(viewLifecycleOwner, Observer {
            holder.recyclerView.apply {
                credentialsAdapter=CredentialsAdapter(it,viewLifecycleOwner,mContext)
                adapter=credentialsAdapter
                layoutManager=
                    LinearLayoutManager(holder.recyclerView.context, RecyclerView.VERTICAL, false)
            }
        })

    }

}
