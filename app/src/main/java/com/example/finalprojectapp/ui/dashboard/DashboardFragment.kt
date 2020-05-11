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
import com.example.finalprojectapp.R
import com.example.finalprojectapp.databinding.FragmentDashboardBinding
import com.example.finalprojectapp.utils.InjectorUtils
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences

class DashboardFragment : Fragment() {
    private val dashboardViewModel: DashboardViewModel by activityViewModels{
        InjectorUtils.provideDashboardViewModelFactory(this)
    }

    private lateinit var setting: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setting = SingleEncryptedSharedPreferences().getSharedPreference(this.requireContext())
        val binding: FragmentDashboardBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard,container,false)
        binding.myViewModel=dashboardViewModel

        dashboardViewModel.data.observe(viewLifecycleOwner, Observer { binding.myData=it })
        if (setting.getBoolean("RepeatedPasswords",false))
            dashboardViewModel.addReactedPasswordListener(viewLifecycleOwner)

        return binding.root
    }


}
