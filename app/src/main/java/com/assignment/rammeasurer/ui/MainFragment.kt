package com.assignment.rammeasurer.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.assignment.rammeasurer.IRamUsageCallback
import com.assignment.rammeasurer.IRamUsageService
import com.assignment.rammeasurer.RamUsageViewModel
import com.assignment.rammeasurer.data.local.models.RamUsageEntity
import com.assignment.rammeasurer.databinding.FragmentMainBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {
    private val binding by lazy { FragmentMainBinding.inflate(layoutInflater) }

    private val notificationPermission = Manifest.permission.POST_NOTIFICATIONS

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                showToast("Notification permission granted")
            } else {
                showToast("Notification permission denied")
            }
        }


    private val viewModel: RamUsageViewModel by activityViewModels()
    private var ramUsageService: IRamUsageService? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            ramUsageService = IRamUsageService.Stub.asInterface(binder)
            ramUsageService?.registerCallback(ramUsageCallback)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            ramUsageService?.unregisterCallback()
            ramUsageService = null
        }
    }

    private val ramUsageCallback = object : IRamUsageCallback.Stub() {
        override fun onRamUsageUpdated(totalRam: Int, usedRam: Int) {
            Log.d("RAM", "Total RAM: $totalRam MB, Used RAM: $usedRam MB")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkNotificationPermission()
        binding.seeAll.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToListFragment())
        }
        binding.btnStart.setOnClickListener {
            bindService()

        }
        binding.btnStop.setOnClickListener {
            unbindRamService()
        }
        setupChart()

        lifecycleScope.launch {
            viewModel.getDatabaseData().collectLatest {
                updataChart(it)
            }

        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), notificationPermission) == PackageManager.PERMISSION_GRANTED) {
                showToast("Permission already granted")
            } else {
                requestPermissionLauncher.launch(notificationPermission)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun updataChart(chartData: List<RamUsageEntity>) {
        val entriesUsed = mutableListOf<BarEntry>()
        val entriesAvailable = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        chartData.forEachIndexed { index, ramUsage ->
            entriesUsed.add(BarEntry(index.toFloat(), ramUsage.ramUsed.toFloat()))
            entriesAvailable.add(BarEntry(index.toFloat(), ramUsage.ramAvailable.toFloat()))
            labels.add(ramUsage.time) // Using time as labels
        }

        val dataSetUsed = BarDataSet(entriesUsed, "RAM Used").apply {
            color = Color.RED
            valueTextColor = Color.BLACK
        }

        val dataSetAvailable = BarDataSet(entriesAvailable, "RAM Available").apply {
            color = Color.BLUE
            valueTextColor = Color.BLACK
        }

        val barData = BarData(dataSetUsed, dataSetAvailable).apply {
            barWidth = 0.35f
        }

        binding.barChart.apply {
            data = barData
            xAxis.apply {
                axisMinimum = -0.5f
                axisMaximum = chartData.size.toFloat()
                setLabelCount(5, true) // Show fewer labels
                labelRotationAngle = 45f // Rotate labels
            }
            groupBars(0f, 0.5f, 0.15f) // Increase spacing
            setScaleEnabled(true) // Enable zooming
            setPinchZoom(true)
            isDragEnabled = true // Allow scrolling
            invalidate()
        }

    }

    private fun setupChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            setPinchZoom(true)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
        }
    }

    fun formatRamSize(bytes: Long): String {
        val kb = bytes / 1024
        val mb = kb / 1024
        val gb = mb / 1024

        return when {
            gb > 0 -> "$gb GB"
            mb > 0 -> "$mb MB"
            else -> "$kb KB"
        }
    }


    private fun unbindRamService() {
        if (ramUsageService != null) {
            ramUsageService?.unregisterCallback()
            requireActivity().unbindService(serviceConnection)
            ramUsageService = null
        }
    }

    private fun bindService() {
        val intent = Intent("com.assignment.rammeasurer.IRamUsageService").apply {
            setPackage("com.assignment.rammeasurer")
        }
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }



}