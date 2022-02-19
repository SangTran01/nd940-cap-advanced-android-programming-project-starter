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
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepositoryImpl
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val app: Application,
    private val database: ElectionDatabase
) : ViewModel() {

    private val repository = PoliticalPreparednessRepositoryImpl(database)

    private val _voterInfo = MutableLiveData<VoterInfoResponse>()
    val voterInfo: LiveData<VoterInfoResponse> get() = _voterInfo

    private val _savedElection = MutableLiveData<Election?>()
    val savedElection: LiveData<Election?> get() = _savedElection

    private val _showLoading = MutableLiveData<Boolean>()
    val showLoading: LiveData<Boolean> get() = _showLoading

    private val _showError = MutableLiveData<String>()
    val showError : LiveData<String> get() = _showError

    init {
        _showLoading.value = false
    }

    suspend fun fetchSavedElection(id: Int) {
        val result = repository.getSavedElectionById(id.toString())
        _savedElection.value = result
    }

    fun onClear() {
        _voterInfo.value = null
        _savedElection.value = null
        _showLoading.value = null
        _showError.value = null
    }

    fun fetchVoterInfo(
        address: String,
        electionId: Int
    ) {
        viewModelScope.launch {
            if (isNetworkAvailable(app)) {
                _showLoading.value = true
                val response = repository.getVoterInfoFromApi(address, electionId.toString())
                _voterInfo.value = response
                _showLoading.value = false
            }
        }
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
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

    fun refreshVoterInfo(voterInfoResponse: VoterInfoResponse) {
        _voterInfo.value = voterInfoResponse
    }

    fun setLoading(isLoading: Boolean) {
        _showLoading.value = isLoading
    }

    fun saveCurrentElection(election: Election) {
        viewModelScope.launch {
            repository.saveElection(election)
            _savedElection.value = election
        }
    }

    fun deleteCurrentElection(election: Election) {
        viewModelScope.launch {
            val isDeleted = repository.deleteElection(election) == 1
            if (isDeleted) {
                _savedElection.value = null
            } else {
                _showError.value = "Error Deleting Election"
            }
        }
    }
}