package com.example.finalprojectapp.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.finalprojectapp.R
import com.example.finalprojectapp.credentialsDB.CredentialsDataBase
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.workers.DBWorkerDecryption
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainMenuFragment : Fragment() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var localDB:CredentialsDataBase
    private val user = FirebaseAuth.getInstance().currentUser!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        localDB=CredentialsDataBase.getDatabase(requireContext())
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.title="PASCEMANGER"
    }

    override fun onStart() {
        super.onStart()
        val navView: BottomNavigationView =requireActivity().findViewById(R.id.nav_view2)
        val navController: NavController = requireActivity().findNavController(R.id.nav_host_fragment2)
        setOnUpdate()
        navView.setupWithNavController(navController)
    }

    private fun setOnUpdate() {

        db.collection("users").document(user.uid)
            .collection("services")
            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, firebaseFireStoreException ->
                if (firebaseFireStoreException != null)
                    return@addSnapshotListener
                    for (dc in querySnapshot!!.documentChanges) {
                        val serviceName=dc.document.toObject<Service>().name
                        when (dc.type) {
                            DocumentChange.Type.REMOVED -> localDB.applicationDAO().deleteFullService(serviceName)
                            else -> listenerForService(serviceName)

                        }
                    }

                    with(requireContext().getSharedPreferences("mainPreferences", Context.MODE_PRIVATE).edit()) {
                        putBoolean("encrypted", true)
                        apply()
                    }
                    val updateWorkRequest = OneTimeWorkRequestBuilder<DBWorkerDecryption>()
                        .build()
                    WorkManager.getInstance(requireContext()).enqueue(updateWorkRequest)
                    with(
                        requireContext().getSharedPreferences(
                            "mainPreferences",
                            Context.MODE_PRIVATE
                        ).edit()
                    ) {
                        putBoolean("encrypted", true)
                        apply()
                    }
                }
    }

    private fun listenerForService(name: String) {
        db.collection("users").document(user.uid)
            .collection("services").document(name)
            .collection("dataSets")
            .addSnapshotListener { querySnapshot, firebaseFireStoreException ->
                if (firebaseFireStoreException != null)
                    return@addSnapshotListener
                for (dc in querySnapshot!!.documentChanges) {
                    val dataSet=dc.document.toObject<DataSet>()
                    when (dc.type) {
                        DocumentChange.Type.REMOVED -> {
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO){
                                    val localDataSet= dataSet.hashData?.let {
                                        localDB.applicationDAO()
                                            .privateFindByHashData(it)
                                    }
                                    if (localDataSet != null) {
                                        localDB.applicationDAO()
                                            .privateDeleteDataSet(localDataSet)
                                    }
                                }

                            }
                        }
                        else -> {
                            lifecycleScope.launch {
                                localDB.applicationDAO().publicInsertDataSet(dataSet)
                            }
                        }
                    }
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