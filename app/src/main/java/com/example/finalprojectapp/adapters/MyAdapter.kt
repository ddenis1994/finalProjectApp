package com.example.finalprojectapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.data.ViewServiceData


import com.example.finalprojectapp.databinding.ListServicePasswordBinding
import com.example.finalprojectapp.ui.credentials.CredentialsViewModel

class MyAdapter(
    private val myDataSet: List<ViewServiceData>,
    private val credentialsViewModel: CredentialsViewModel,
    private val viewLifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: ListServicePasswordBinding)
        : RecyclerView.ViewHolder(binding.root){
        val recyclerView : RecyclerView = binding.mySecondLayout
        init {
            binding.setMoreDataListener {
                // TODO
                //  not finish the more information gathering
                Toast.makeText(it.context, "more Info", Toast.LENGTH_SHORT).show()
            }
            binding.setDisplayData {
                binding.mySecondLayout.let {
                    if (it.visibility== View.GONE)
                        it.visibility= View.VISIBLE
                    else
                        it.visibility= View.GONE
                }
            }
        }
        fun bind(item: ViewServiceData) {
            binding.apply {
                //make right value for string to fill
                val temp= item.serviceName
                    ?.replace("."," ", false)
                    ?.split(" ")
                cardData=item.copy(serviceName = temp?.get(temp.size-1)?.capitalize())

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ListServicePasswordBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        ))
    }

    override fun getItemCount(): Int {
        return myDataSet.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = myDataSet[position]
        holder.bind(data)
        val localData=data.dataSetId?.let { credentialsViewModel.getCrede(it) }
        localData?.observe(viewLifecycleOwner, Observer {
            holder.recyclerView.apply {
                adapter=InnerCredentialsAdapter(it)
                layoutManager=
                    LinearLayoutManager(holder.recyclerView.context, RecyclerView.VERTICAL, false)
            }
        })
    }
}
