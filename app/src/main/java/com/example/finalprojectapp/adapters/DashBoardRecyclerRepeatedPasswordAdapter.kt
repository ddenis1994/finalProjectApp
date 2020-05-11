package com.example.finalprojectapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.data.model.adpters.LayoutDashBoardRepeatedPassword
import com.example.finalprojectapp.databinding.LayoutDashBoardHashBinding
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashBoardRecyclerRepeatedPasswordAdapter(
    private val children: List<Pair<Long, List<DashboardViewModel.HashAndId>>>,
    private val owner: LifecycleOwner,
    private val dashboardViewModel: DashboardViewModel
):
    RecyclerView.Adapter<DashBoardRecyclerRepeatedPasswordAdapter.DataSetViewHolder>() {


    class DataSetViewHolder(
        private val binding: LayoutDashBoardHashBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(dashBoardInnerAdapter: DashBoardInnerAdapter) {
            adapterSave=dashBoardInnerAdapter
            binding.repeatedPasswordInnerRecyclerView.apply {
                adapter=adapterSave
                layoutManager=LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

        }

        private lateinit var adapterSave:DashBoardInnerAdapter
    }

    override fun getItemCount(): Int = children.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataSetViewHolder {
        return DataSetViewHolder(LayoutDashBoardHashBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }


    override fun onBindViewHolder(holder: DataSetViewHolder, position: Int) {
        val data=children[position]
        dashboardViewModel.viewModelScope.launch {
                val h =
                    dashboardViewModel.findServiceAndDataSet(data.first)
                holder.bind(DashBoardInnerAdapter(h))

        }



    }


}