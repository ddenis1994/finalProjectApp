package com.example.finalprojectapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.finalprojectapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView


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
        navView.setupWithNavController(navController)

    }
}