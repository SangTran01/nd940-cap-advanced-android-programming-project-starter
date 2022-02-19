package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse

interface PoliticalPreparednessRepository {
    suspend fun getElectionsFromApi(): ElectionResponse

    suspend fun getVoterInfoFromApi(address: String, electionId: String): VoterInfoResponse

    suspend fun getRepresentativeFromApi(address: String): RepresentativeResponse

    suspend fun refreshData()

    suspend fun getElectionsFromDatabase(): List<Election>

    suspend fun getSavedElectionById(id: String): Election?

    suspend fun getRepresentativeFromDatabase(): RepresentativeResponse

    suspend fun saveElection(election: Election)

    suspend fun deleteElection(election: Election): Int

}
