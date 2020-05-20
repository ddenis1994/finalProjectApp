package com.example.finalprojectapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.data.model.Notification
import com.example.finalprojectapp.databinding.NotificationBinding

class NotificationAdapter(private val data: List<Notification>) :
    RecyclerView.Adapter< NotificationAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: NotificationBinding
    ): RecyclerView.ViewHolder(binding.root) {

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

    }
}