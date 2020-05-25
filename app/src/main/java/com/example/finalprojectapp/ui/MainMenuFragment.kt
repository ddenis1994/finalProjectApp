package com.example.finalprojectapp.ui

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.finalprojectapp.R
import com.example.finalprojectapp.credentialsDB.CredentialsDataBase
import com.example.finalprojectapp.data.model.Credentials
import com.example.finalprojectapp.data.model.DataSet
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.workers.DBWorkerDecryption
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_main.*
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


    }





    private fun setOnUpdate() {
    if(context!=null)
        db.collection("users").document(user.uid)
            .collection("services")
            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, firebaseFireStoreException ->
                if (firebaseFireStoreException != null)
                    return@addSnapshotListener
                    for (dc in querySnapshot!!.documentChanges) {
                        val serviceName=dc.document.toObject<Service>().name
                        when (dc.type) {
                            DocumentChange.Type.REMOVED -> localDB.applicationDAO().deleteFullService(serviceName)
                            DocumentChange.Type.ADDED -> {
                                val h=localDB.applicationDAO().publicInsertService(dc.document.toObject())
                                h.observe(viewLifecycleOwner, Observer {
                                    listenerForService(serviceName)
                                })

                            }
                            DocumentChange.Type.MODIFIED -> listenerForService(serviceName)
                        }
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
                                        localDB.applicationDAO().privateFindByHashData(it)
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
                                localDB.applicationDAO().publicInsertDataSet(dataSet,name)
                                startLocalDecryption()
                            }
                        }
                    }
                }
            }

    }

    private fun startLocalDecryption() {
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