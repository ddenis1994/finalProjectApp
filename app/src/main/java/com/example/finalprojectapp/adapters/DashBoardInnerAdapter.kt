package com.example.finalprojectapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.data.model.adpters.LayoutDashBoardRepeatedPassword
import com.example.finalprojectapp.databinding.LayoutDashBoardDataSetsBinding
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel

class DashBoardInnerAdapter(
    private val children: List<DashboardViewModel.ServiceNameAndDataSet?>
) :
    RecyclerView.Adapter<DashBoardInnerAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: LayoutDashBoardDataSetsBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LayoutDashBoardRepeatedPassword) {
            binding.apply {
                val temp = item.serviceName
                    .replace(".", " ", false)
                    .split(" ")
                dataSetCard = item.copy(serviceName = temp[temp.size - 1].capitalize())

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutDashBoardDataSetsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = children.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = children[position]
        if (data != null)
            holder.bind(LayoutDashBoardRepeatedPassword(data.dataSetName, data.serviceName))
    }
}