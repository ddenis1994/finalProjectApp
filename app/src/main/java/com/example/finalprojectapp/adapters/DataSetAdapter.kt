package com.example.finalprojectapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.AuthActivity
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import com.example.finalprojectapp.databinding.LayoutListDataSetsBinding
import com.example.finalprojectapp.ui.credentials.CredentialsFragment
import com.example.finalprojectapp.ui.credentials.CredentialsViewModel


class DataSetAdapter(
    private val dataSets: List<LayoutDataSetView>,
    private val credentialsViewModel: CredentialsViewModel,
    private val viewLifecycleOwner: LifecycleOwner,
    private val mContext: CredentialsFragment
) :
    RecyclerView.Adapter<DataSetAdapter.DataSetViewHolder>() {
    private lateinit var credentialsAdapter: CredentialsAdapter

    class DataSetViewHolder(
        private val binding: LayoutListDataSetsBinding,
        private val mContext: CredentialsFragment
    )
        : RecyclerView.ViewHolder(binding.root){
        val recyclerView : RecyclerView = binding.credentialsRecyclerView
        private val forDeleteCode=2

        init {
            binding.setDisplayDataSet {
                binding.credentialsRecyclerView.let {
                    if (it.visibility== View.GONE)
                        it.visibility= View.VISIBLE
                    else
                        it.visibility= View.GONE
                }
            }
            binding.setDeleteDataSet{
                val deleteAuthActivity=Intent(mContext.requireContext(),AuthActivity::class.java).apply {
                    putExtra("dataSetId",binding.dataSetCard?.dataSetId?.toInt())
                }

                mContext.startActivityForResult(deleteAuthActivity,forDeleteCode)
            }
        }
        fun bind(item: LayoutDataSetView) {
            binding.apply {
                dataSetCard=item

            }
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataSetViewHolder {
        return DataSetViewHolder(LayoutListDataSetsBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ),mContext)
    }

    override fun getItemCount(): Int {
        return dataSets.size
    }



    override fun onBindViewHolder(holder: DataSetViewHolder, position: Int) {
        val data = dataSets[position]
        holder.bind(data)
        val localData=data.dataSetId?.let { credentialsViewModel.getCrede(it) }
        localData?.observe(viewLifecycleOwner, Observer {
            holder.recyclerView.apply {
                credentialsAdapter=CredentialsAdapter(it,viewLifecycleOwner,mContext)
                adapter=credentialsAdapter
                layoutManager=
                    LinearLayoutManager(holder.recyclerView.context, RecyclerView.VERTICAL, false)
            }
        })
    }



}