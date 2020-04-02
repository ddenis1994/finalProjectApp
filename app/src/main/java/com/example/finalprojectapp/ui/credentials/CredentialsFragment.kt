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
import kotlinx.android.synthetic.main.fragment_cre.*

class CredentialsFragment : Fragment() {
    private val credentialsViewModel: CredentialsViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val TAG:String="main_activity"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_cre, container, false)
        credentialsViewModel.allPasswords.observe(viewLifecycleOwner, Observer {
            viewAdapter = MyAdapter(it)
            recyclerView.apply {
                adapter=viewAdapter
            }
        })

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
