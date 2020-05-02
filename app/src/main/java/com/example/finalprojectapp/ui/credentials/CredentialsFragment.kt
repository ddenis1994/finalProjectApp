package com.example.finalprojectapp.ui.credentials

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.R
import com.example.finalprojectapp.adapters.ServiceAdapter
import com.example.finalprojectapp.utils.InjectorUtils
import kotlinx.android.synthetic.main.fragment_cre.*
import kotlinx.android.synthetic.main.fragment_cre.view.*


class CredentialsFragment : Fragment() {
    private val credentialsViewModel: CredentialsViewModel by activityViewModels(){
        InjectorUtils.provideCredentialsViewModelFactory(this)

    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_cre, container, false)
        credentialsViewModel.allPasswords.observe(viewLifecycleOwner, Observer {
            root.progressBarForLoading.visibility=View.GONE
            root.textViewForLoading.visibility=View.GONE
            viewAdapter = ServiceAdapter(it,credentialsViewModel,viewLifecycleOwner,this)
            recyclerView.apply {
                adapter=viewAdapter
            }
        })
        return root
    }

    override fun onStart() {
        super.onStart()
        viewManager = LinearLayoutManager(context)
        recyclerView = list_recycle_view_services.apply {
            setHasFixedSize(true)
            layoutManager = viewManager

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        (viewAdapter as ServiceAdapter).onActivityResult(requestCode, resultCode, data)
        Log.e("tet","TE")
    }
}
