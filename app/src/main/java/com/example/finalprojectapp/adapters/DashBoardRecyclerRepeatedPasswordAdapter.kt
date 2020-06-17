package com.example.finalprojectapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.databinding.LayoutDashBoardHashBinding
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel

class DashBoardRecyclerRepeatedPasswordAdapter(
    private val children: List<Pair<String?, List<DashboardViewModel.ServiceNameAndDataSet?>>>
) :
    RecyclerView.Adapter<DashBoardRecyclerRepeatedPasswordAdapter.DataSetViewHolder>() {


    class DataSetViewHolder(
        private val binding: LayoutDashBoardHashBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(dashBoardInnerAdapter: DashBoardInnerAdapter) {
            adapterSave = dashBoardInnerAdapter
            binding.repeatedPasswordInnerRecyclerView.apply {
                adapter = adapterSave
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

        }

        private lateinit var adapterSave: DashBoardInnerAdapter
    }

    override fun getItemCount(): Int {
        return children.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataSetViewHolder {
        return DataSetViewHolder(
            LayoutDashBoardHashBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: DataSetViewHolder, position: Int) {
        val data = children[position].second
        holder.bind(DashBoardInnerAdapter(data))
    }


}