package com.example.android.politicalpreparedness.election

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepositoryImpl
import kotlinx.coroutines.launch

class ElectionsViewModel(private val application: Application, database: ElectionDatabase) : ViewModel() {

    private val repository = PoliticalPreparednessRepositoryImpl(database)

    private val _upcomingElections = MutableLiveData<ElectionResponse>()
    val upcomingElections: LiveData<ElectionResponse> get() = _upcomingElections
    private val _savedElections = MutableLiveData<List<Election>>()
    val savedElections : LiveData<List<Election>> get() = _savedElections

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading : LiveData<Boolean> get() = _showLoading

    init {
        _showLoading.value = false
    }

    fun refreshElections() {
        viewModelScope.launch {
            _showLoading.value = true
            if (isNetworkAvailable(application)) {
                fetchUpcomingElections()
                _showLoading.value = false
            }
            fetchSavedElections()
            _showLoading.value = false
        }
    }

    fun onClear() {
        _upcomingElections.value = null
        _savedElections.value = null
        _showLoading.value = false
    }

    suspend fun fetchUpcomingElections() {
        _upcomingElections.value  = repository.getElectionsFromApi()
    }

    suspend fun fetchSavedElections() {
        _savedElections.value = repository.getElectionsFromDatabase()
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
}