package com.example.finalprojectapp.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.MainApplication
import com.example.finalprojectapp.R
import com.example.finalprojectapp.databinding.FragmentDashboardBinding
import com.example.finalprojectapp.ui.credentials.CredentialsViewModel
import com.example.finalprojectapp.utils.InjectorUtils
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences
import kotlinx.android.synthetic.main.fragment_dashboard.*
import javax.inject.Inject

class DashboardFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val dashboardViewModel by viewModels<DashboardViewModel> {
        viewModelFactory }



    private lateinit var setting: SharedPreferences
    private lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().applicationContext as MainApplication).appComponent.uiComponent()
            .create().dashBoardFragmentComponent().create().inject(this)
    }

    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setting = SingleEncryptedSharedPreferences().getSharedPreference(this.requireContext())
        val binding: FragmentDashboardBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dashboard, container, false
        )
        binding.myViewModel = dashboardViewModel

        dashboardViewModel.data.observe(viewLifecycleOwner, Observer {
            binding.myData = it
            recyclerView.apply {
                adapter = it.viewAdapter
            }
        })

        if (setting.getBoolean("RepeatedPasswords", false))
            dashboardViewModel.addReactedPasswordListener(viewLifecycleOwner)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        viewManager = LinearLayoutManager(context)
        recyclerView = repeatedPasswordRecyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar_menu, menu)
        this.findNavController()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_settings -> {
                findNavController().navigate(R.id.action_global_settingsFragment)
            }
        }

        return super.onOptionsItemSelected(item)
    }


}
