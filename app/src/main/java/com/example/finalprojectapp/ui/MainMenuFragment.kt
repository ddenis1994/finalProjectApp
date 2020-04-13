package com.example.finalprojectapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.finalprojectapp.R
import com.example.finalprojectapp.localDB.PasswordRoomDatabase
import com.example.finalprojectapp.workers.DBWorkerDecryption
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.launch


class MainMenuFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onStart() {
        super.onStart()
        val navView: BottomNavigationView =requireActivity().findViewById(R.id.nav_view2)
        val navController: NavController = requireActivity().findNavController(R.id.nav_host_fragment2)
        setOnUpdate()
        navView.setupWithNavController(navController)


    }

    private fun setOnUpdate() {
        val db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser!!
        db.collection("users").document(user.uid)
            .collection("services")
            .addSnapshotListener(MetadataChanges.INCLUDE) { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException != null)
                    return@addSnapshotListener
                val localDB = PasswordRoomDatabase.getDatabase(requireContext())
                    for (dc in querySnapshot!!.documentChanges) {
                        when (dc.type) {
                            DocumentChange.Type.REMOVED -> {
                                lifecycleScope.launch {
                                    localDB.localCredentialsDAO()
                                        .deleteFullService(dc.document.toObject())
                                }
                            }
                            else ->{
                                lifecycleScope.launch {
                                    localDB.localCredentialsDAO()
                                        .insertSingleServiceCredentials(
                                            dc.document.toObject()
                                        )
                                }
                            }

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
}