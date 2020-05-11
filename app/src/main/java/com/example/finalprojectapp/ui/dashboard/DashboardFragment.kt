package com.example.finalprojectapp.ui.dashboard

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.R
import com.example.finalprojectapp.databinding.FragmentDashboardBinding
import com.example.finalprojectapp.utils.InjectorUtils
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment() {
    private val dashboardViewModel: DashboardViewModel by activityViewModels{
        InjectorUtils.provideDashboardViewModelFactory(this)
    }

    private lateinit var setting: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setting = SingleEncryptedSharedPreferences().getSharedPreference(this.requireContext())
        val binding: FragmentDashboardBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard,container,false)
        binding.myViewModel=dashboardViewModel

        dashboardViewModel.data.observe(viewLifecycleOwner, Observer {
            binding.myData=it
            recyclerView.apply {
                adapter=it.viewAdapter
            }
        })

        if (setting.getBoolean("RepeatedPasswords",false))
            dashboardViewModel.addReactedPasswordListener(viewLifecycleOwner)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        viewManager = LinearLayoutManager(context)
        recyclerView = repeatedPasswordRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }
    }


}
