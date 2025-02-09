package com.assignment.rammeasurer.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.assignment.rammeasurer.R
import com.assignment.rammeasurer.RamUsageViewModel
import com.assignment.rammeasurer.databinding.FragmentListBinding
import com.assignment.rammeasurer.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListFragment : Fragment() {
    private val binding by lazy { FragmentListBinding.inflate(layoutInflater) }
    private val viewModel: RamUsageViewModel by activityViewModels()

    private var adapter: RamUsageAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        lifecycleScope.launch {
            viewModel.getDatabaseData().collectLatest {
                adapter?.setData(it)
            }
        }

    }

    private fun setupAdapter() {
        binding.rvData.layoutManager = LinearLayoutManager(requireContext())
        adapter = RamUsageAdapter()
        binding.rvData.adapter = adapter
    }

}