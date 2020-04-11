package com.example.finalprojectapp.ui.credentials

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.R
import com.example.finalprojectapp.adapters.MyAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_cre.*
import kotlinx.android.synthetic.main.fragment_cre.view.*

class CredentialsFragment : Fragment() {
    private val credentialsViewModel: CredentialsViewModel by activityViewModels()
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
            viewAdapter = MyAdapter(it)
            recyclerView.apply {
                adapter=viewAdapter
            }
        })
        credentialsViewModel.getCredentialsData()

        return root
    }

    override fun onStart() {
        super.onStart()
        viewManager = LinearLayoutManager(context)
        recyclerView = my_manu_top.apply {
            setHasFixedSize(true)
            layoutManager = viewManager

        }
    }
}
