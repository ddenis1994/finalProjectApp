package com.example.finalprojectapp.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.finalprojectapp.R
import com.example.finalprojectapp.databinding.FragmentDashboardBinding
import com.example.finalprojectapp.utils.InjectorUtils

class DashboardFragment : Fragment() {
    private val dashboardViewModel: DashboardViewModel by activityViewModels{
        InjectorUtils.provideDashboardViewModelFactory(this)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        val binding: FragmentDashboardBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard,container,false)
        binding.myViewModel=dashboardViewModel

        dashboardViewModel.data.observe(viewLifecycleOwner, Observer {
            binding.myData=it
        })

        return binding.root
    }


}
