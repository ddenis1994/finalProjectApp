package com.example.finalprojectapp.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectapp.AuthActivity
import com.example.finalprojectapp.R
import com.example.finalprojectapp.data.model.adpters.LayoutDataSetView
import com.example.finalprojectapp.databinding.LayoutListDataSetsBinding
import com.example.finalprojectapp.ui.credentials.CredentialsFragment
import com.example.finalprojectapp.ui.credentials.CredentialsFragmentDirections


class DataSetAdapter(
    private val dataSets: List<LayoutDataSetView>,
    private val mContext: CredentialsFragment
) :
    RecyclerView.Adapter<DataSetAdapter.DataSetViewHolder>() {

    class DataSetViewHolder(
        private val binding: LayoutListDataSetsBinding,
        private val mContext: CredentialsFragment
    ) : RecyclerView.ViewHolder(binding.root) {
        private val forDeleteCode = 2
        private var dataSetId: Long = 0

        init {
            binding.setDisplayDataSet {
                binding.credentialsRecyclerView.let {
                    if (it.visibility == View.GONE)
                        it.visibility = View.VISIBLE
                    else
                        it.visibility = View.GONE
                }
            }
            binding.setDeleteDataSet {
                val deleteAuthActivity =
                    Intent(mContext.requireContext(), AuthActivity::class.java).apply {
                        putExtra("dataSetId", binding.dataSetCard?.dataSetId?.toInt())
                    }

                mContext.startActivityForResult(deleteAuthActivity, forDeleteCode)
            }
            binding.setDisplayDataSet {
                val action =
                    CredentialsFragmentDirections.actionNavigationPasswordToCredentialInnerFragment(
                        dataSetId
                    )
                this.mContext.requireActivity().findNavController(R.id.nav_host_fragment)
                    .navigate(action)
            }
        }

        fun bind(item: LayoutDataSetView) {
            binding.apply {
                dataSetCard = item
                dataSetId = item.dataSetId

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataSetViewHolder {
        return DataSetViewHolder(
            LayoutListDataSetsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ), mContext
        )
    }

    override fun getItemCount(): Int {
        return dataSets.size
    }


    override fun onBindViewHolder(holder: DataSetViewHolder, position: Int) {
        val data = dataSets[position]
        holder.bind(data)
    }


}