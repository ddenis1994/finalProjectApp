package com.example.finalprojectapp.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.finalprojectapp.R
import com.example.finalprojectapp.utils.InjectorUtils

class NotificationsFragment : Fragment() {

    private val notificationsViewModel: NotificationsViewModel by activityViewModels {
        InjectorUtils.provideNotificationViewModelFactory(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.notification, container, false)
        notificationsViewModel.data.observe(viewLifecycleOwner, Observer {

        })
        return root
    }
}
