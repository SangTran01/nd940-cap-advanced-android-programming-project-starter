package com.example.android.politicalpreparedness.representative

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.repository.PoliticalPreparednessRepositoryImpl
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(
    private val app: Application,
    private val database: ElectionDatabase
): ViewModel() {

    private val repository = PoliticalPreparednessRepositoryImpl(database)

    private val _representatives = MutableLiveData<List<Representative>>()
    val representatives : LiveData<List<Representative>> get() = _representatives

    private val _showError = MutableLiveData<String>()
    val showError : LiveData<String> get() = _showError


    val addressLine1 = MutableLiveData<String>()
    val addressLine2 = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val state = MutableLiveData<String>()
    val zip = MutableLiveData<String>()

    fun getRepresentativesFromAPI(address: String) {
        viewModelScope.launch {
            val results: RepresentativeResponse = repository.getRepresentativeFromApi(address)

            _representatives.value = results.offices.flatMap { office ->
                office.getRepresentatives(results.officials)
            }
        }
    }

    fun getAddressFromLocation(a: Address) {
        addressLine1.value = a.line1
        addressLine2.value = a.line2 ?: ""
        city.value = a.city
        state.value = a.state
        zip.value = a.zip

        val formattedAddress = a.toFormattedString()
        getRepresentativesFromAPI(formattedAddress)
    }

    fun getAddressFromFields() {
        val address = addressLine1.value
        val address2 = addressLine2.value
        val city = city.value
        val state = state.value
        val zip = zip.value

        if (!address.isNullOrEmpty() &&
            !city.isNullOrEmpty() &&
            !zip.isNullOrEmpty()) {
            getRepresentativesFromAPI("$address $address2 $city $state $zip")
        } else {
            _showError.value = "Missing Address, City, or Zip Fields"
        }
    }

}
