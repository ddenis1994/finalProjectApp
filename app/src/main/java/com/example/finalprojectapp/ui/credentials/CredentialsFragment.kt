package com.example.finalprojectapp.ui.credentials

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.MainApplication
import com.example.finalprojectapp.R
import com.example.finalprojectapp.adapters.ServiceAdapter
import com.example.finalprojectapp.credentialsDB.LocalDataBase
import kotlinx.android.synthetic.main.fragment_cre.*
import kotlinx.android.synthetic.main.fragment_cre.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


class CredentialsFragment : Fragment() {
//    private val credentialsViewModel: CredentialsViewModel by activityViewModels {
//        InjectorUtils.provideCredentialsViewModelFactory(this)
//
//    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var localDB: LocalDataBase

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val credentialsViewModel by viewModels<CredentialsViewModel> {
        viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MainApplication).appComponent.credentialViewModelComponent().create().inject(this)
    }



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
        localDB=LocalDataBase.getDatabase(requireContext())
        return root
    }

    override fun onStart() {
        super.onStart()
//        setOnUpdate()
        setHasOptionsMenu(true)
        viewManager = LinearLayoutManager(context)
        recyclerView = list_recycle_view_services.apply {
            setHasFixedSize(true)
            layoutManager = viewManager

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==2) {
            when (resultCode ){
                AppCompatActivity.RESULT_CANCELED->Log.i("test","test321")
                AppCompatActivity.RESULT_OK-> if (data != null) {
                    deleteDataSet(data.getIntExtra("dataSetId",-1).toLong())
                }
            }
        }

    }
    private fun deleteDataSet(dataSetId:Long) {
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO){

                credentialsViewModel.deleteDataSet(dataSetId)

            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar_menu, menu)
        this.findNavController()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.navigation_settings-> {
                findNavController().navigate(R.id.action_global_settingsFragment)
            }
        }

        return super.onOptionsItemSelected(item)
    }


}
