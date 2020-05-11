package com.example.finalprojectapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.databinding.LayoutDashBoardHashBinding
import com.example.finalprojectapp.ui.dashboard.DashboardViewModel

class DashBoardRecyclerRepeatedPasswordAdapter(
    private val children: List<Pair<Long, List<DashboardViewModel.HashAndId>>>,
    private val owner: LifecycleOwner,
    private val dashboardViewModel: DashboardViewModel
):
    RecyclerView.Adapter<DashBoardRecyclerRepeatedPasswordAdapter.DataSetViewHolder>() {

    class DataSetViewHolder(
        binding: LayoutDashBoardHashBinding
    ) : RecyclerView.ViewHolder(binding.root){
        val recyclerView : RecyclerView = binding.repeatedPasswordInnerRecyclerView
    }

    override fun getItemCount(): Int = children.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataSetViewHolder {
        return DataSetViewHolder(LayoutDashBoardHashBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun onBindViewHolder(holder: DataSetViewHolder, position: Int) {
        val data=children[position]
        dashboardViewModel.findServiceAndDataSet(data.first).observe(owner, Observer {
            holder.recyclerView.apply {
                adapter=DashBoardInnerAdapter(it)
                layoutManager=
                    LinearLayoutManager(holder.recyclerView.context, RecyclerView.VERTICAL, false)
            }
        })


    }


}