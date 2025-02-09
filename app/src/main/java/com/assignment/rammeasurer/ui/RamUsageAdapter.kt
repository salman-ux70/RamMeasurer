package com.assignment.rammeasurer.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.assignment.rammeasurer.data.local.models.RamUsageEntity
import com.assignment.rammeasurer.databinding.ItemRamUsageBinding

class RamUsageAdapter() :
    RecyclerView.Adapter<RamUsageAdapter.RamUsageViewHolder>() {
    private var ramList: List<RamUsageEntity> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RamUsageViewHolder {
        val binding = ItemRamUsageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RamUsageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RamUsageViewHolder, position: Int) {
        val item = ramList[position]
        holder.bind(item)

    }

    override fun getItemCount() = ramList.size

    class RamUsageViewHolder(val binding: ItemRamUsageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RamUsageEntity) {
            binding.tvRamUsed.text = "Used: ${item.ramUsed}"
            binding.tvRamAvailable.text = "Available: ${item.ramAvailable}"
            binding.tvTime.text = item.time
        }

    }

    fun setData(data : List<RamUsageEntity>){
        this.ramList = data
        notifyDataSetChanged()
    }
}
