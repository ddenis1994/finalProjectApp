package com.example.finalprojectapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.data.model.Service
import com.example.finalprojectapp.data.model.ServiceCredentialsServer
import com.example.finalprojectapp.databinding.ListServicePasswordBinding

class MyAdapter(private val myDataSet: List<ServiceCredentialsServer>) :
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
        fun bind(item: ServiceCredentialsServer) {
            binding.apply {
                //make right value for string to fill
                val temp=item.name
                    .replace("."," ", false)
                    .split(" ")
                item.name=temp[temp.size-1].capitalize()
                cardData=item

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
        holder.recyclerView.apply {
            adapter=InnerCredentialsAdapter(data.credentials!!)
            layoutManager=
                LinearLayoutManager(holder.recyclerView.context, RecyclerView.VERTICAL, false)

        }
    }


}
