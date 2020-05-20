package com.example.finalprojectapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.model.Notification
import com.example.finalprojectapp.databinding.NotificationBinding

class NotificationAdapter(private val data: List<Notification>) :
    RecyclerView.Adapter< NotificationAdapter.ViewHolder>() {

    private val localChange=R.drawable.ic_devices_black_24dp
    private val remoteChange=R.drawable.ic_cloud_black_24dp
    private val mainPasswordChange=R.drawable.ic_lock_black_24dp


    class ViewHolder(
        private val binding: NotificationBinding
    ): RecyclerView.ViewHolder(binding.root) {



        fun bind(data:Notification,type:Int) {
            binding.notificationData=data
            binding.startNotifactionIcon.setImageResource(type)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            NotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        )

    }

    override fun getItemCount(): Int =data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = data[position]
        val type=when (data.type){
            0->localChange
            1->remoteChange
            2->mainPasswordChange
            else -> -1
        }
        holder.bind(data,type)
    }
}