package com.example.finalprojectapp.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.MainApplication
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.model.DashBoardData
import com.example.finalprojectapp.databinding.FragmentDashboardBinding
import com.example.finalprojectapp.utils.SingleEncryptedSharedPreferences
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import javax.inject.Inject

class DashboardFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val dashboardViewModel by viewModels<DashboardViewModel> {
        viewModelFactory
    }


    private lateinit var setting: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

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
        val binding = inflater.inflate(
            R.layout.fragment_dashboard, container, false
        )
        dashboardViewModel.serviceCount.observe(
            viewLifecycleOwner, Observer {
                binding.service_count_dashboard.text = it.toString()
            }
        )
        dashboardViewModel.getNumOfService().observe(viewLifecycleOwner, Observer {
            dashboardViewModel.updateServiceCount(it)
        })

        dashboardViewModel.connectionToRemote.observe(viewLifecycleOwner, Observer {
            binding.connection_to_remote.text=it.toString()
        })

        dashboardViewModel.viewAdapter.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if (it.itemCount == 0)
                    recyclerView.visibility = View.GONE
                else {
                    viewAdapter = it
                    recyclerView.apply {
                        adapter = viewAdapter
                    }
                    recyclerView.visibility = View.VISIBLE
                }
            }

        })

        dashboardViewModel.securityRisks.observe(
            viewLifecycleOwner, Observer {
                if (it != 0) {
                    binding.security_risks.text = it.toString()
                    binding.security_risks_container.visibility = View.VISIBLE
                } else binding.security_risks_container.visibility = View.GONE
            }
        )

        if (setting.getBoolean("RepeatedPasswords", false))
            dashboardViewModel.checkForRepeatedPassword().observe(viewLifecycleOwner, Observer {
                dashboardViewModel.updateRepeatedPassword(it)
            })

        return binding
    }

    override fun onStart() {
        super.onStart()
        viewManager = LinearLayoutManager(requireContext())
        recyclerView = repeatedPasswordRecyclerView2.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
        }
        setHasOptionsMenu(true)


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
