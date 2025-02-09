package com.assignment.rammeasurer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.rammeasurer.data.local.models.RamUsageEntity
import com.assignment.rammeasurer.data.repo.RamUsageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RamUsageViewModel @Inject constructor(
    private val repository: RamUsageRepository
) : ViewModel() {




    fun getDatabaseData(): Flow<List<RamUsageEntity>> {
        Log.d("RAM","${repository.getData()}")
        return repository.getData()

    }

    fun insertRamUsage(ramUsed: Int, ramAvailable: Int) {
        viewModelScope.launch {
            val ramUsage = RamUsageEntity(
                time = RamUsageEntity.getCurrentTime(),
                ramUsed = ramUsed,
                ramAvailable = ramAvailable
            )
            repository.insertRamUsage(ramUsage)
        }
    }

}