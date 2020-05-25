package com.example.finalprojectapp.ui.notifications

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.R
import com.example.finalprojectapp.adapters.NotificationAdapter
import com.example.finalprojectapp.data.model.Notification
import com.example.finalprojectapp.utils.InjectorUtils
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationsFragment : Fragment() {

    private val notificationsViewModel: NotificationsViewModel by activityViewModels {
        InjectorUtils.provideNotificationViewModelFactory(this)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        notificationsViewModel.data.observe(viewLifecycleOwner, Observer {
            viewAdapter=NotificationAdapter(it)

            notification_RecyclerView.adapter=NotificationAdapter(it)
        })
        return root
    }

    override fun onStart() {
        super.onStart()
        setHasOptionsMenu(true)
        viewManager = LinearLayoutManager(context)
        recyclerView = notification_RecyclerView.apply {
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
        when(item.itemId){
            R.id.navigation_settings-> {
                findNavController().navigate(R.id.action_global_settingsFragment)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
