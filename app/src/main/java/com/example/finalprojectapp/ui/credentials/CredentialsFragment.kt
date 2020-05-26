package com.example.finalprojectapp.ui.credentials

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.finalprojectapp.R
import com.example.finalprojectapp.adapters.ServiceAdapter
import com.example.finalprojectapp.credentialsDB.CredentialsDataBase
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.utils.InjectorUtils
import com.example.finalprojectapp.workers.DBWorkerDecryption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_cre.*
import kotlinx.android.synthetic.main.fragment_cre.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CredentialsFragment : Fragment() {
    private val credentialsViewModel: CredentialsViewModel by activityViewModels {
        InjectorUtils.provideCredentialsViewModelFactory(this)

    }
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private val db = FirebaseFirestore.getInstance()
    private lateinit var localDB: CredentialsDataBase
    private val user = FirebaseAuth.getInstance().currentUser!!



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
        localDB=CredentialsDataBase.getDatabase(requireContext())
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
//
//    private fun setOnUpdate() {
//        if(context!=null)
//            db.collection("users").document(user.uid)
//                .collection("services")
//                .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, firebaseFireStoreException ->
//                    if (firebaseFireStoreException != null)
//                        return@addSnapshotListener
//                    for (dc in querySnapshot!!.documentChanges) {
//                        val serviceName=dc.document.toObject<Service>().name
//                        when (dc.type) {
//                            DocumentChange.Type.REMOVED -> localDB.applicationDAO().deleteFullService(serviceName)
//                            DocumentChange.Type.ADDED -> {
//                                val h=localDB.applicationDAO().publicInsertService(dc.document.toObject())
//                                h.observe(viewLifecycleOwner, Observer {
//                                    listenerForService(serviceName)
//                                })
//
//                            }
//                            DocumentChange.Type.MODIFIED -> listenerForService(serviceName)
//                        }
//                    }
//                }
//    }

//    private fun listenerForService(name: String) {
//        db.collection("users").document(user.uid)
//            .collection("services").document(name)
//            .collection("dataSets")
//            .addSnapshotListener { querySnapshot, firebaseFireStoreException ->
//                if (firebaseFireStoreException != null)
//                    return@addSnapshotListener
//                for (dc in querySnapshot!!.documentChanges) {
//                    val dataSet=dc.document.toObject<DataSet>()
//                    when (dc.type) {
//                        DocumentChange.Type.REMOVED -> {
//                            lifecycleScope.launch {
//                                withContext(Dispatchers.IO){
//                                    val localDataSet= dataSet.hashData?.let {
//                                        localDB.applicationDAO().privateFindByHashData(it)
//                                    }
//                                    if (localDataSet != null) {
//                                        localDB.applicationDAO()
//                                            .privateDeleteDataSet(localDataSet)
//                                    }
//                                }
//
//                            }
//                        }
//                        else -> {
//                            lifecycleScope.launch {
//                                localDB.applicationDAO().publicInsertDataSet(dataSet,name)
//                                startLocalDecryption()
//                            }
//                        }
//                    }
//                }
//            }
//
//    }
//
//    private fun startLocalDecryption() {
//        val updateWorkRequest = OneTimeWorkRequestBuilder<DBWorkerDecryption>()
//            .build()
//        WorkManager.getInstance(requireContext()).enqueue(updateWorkRequest)
//        with(
//            requireContext().getSharedPreferences(
//                "mainPreferences",
//                Context.MODE_PRIVATE
//            ).edit()
//        ) {
//            putBoolean("encrypted", true)
//            apply()
//        }
//    }

}
