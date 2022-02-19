package com.example.android.politicalpreparedness.repository

import androidx.core.os.BuildCompat
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PoliticalPreparednessRepositoryImpl(
    private val database: ElectionDatabase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) :
    PoliticalPreparednessRepository {
    override suspend fun getElectionsFromApi(): ElectionResponse {
        return CivicsApi.retrofitService.getElections(BuildConfig.API_KEY)
    }

    override suspend fun getVoterInfoFromApi(
        address: String,
        electionId: String
    ): VoterInfoResponse = withContext(ioDispatcher) {
        return@withContext CivicsApi.retrofitService.getVoterInfo(
            BuildConfig.API_KEY,
            address,
            electionId
        )
    }

    override suspend fun getRepresentativeFromApi(address: String): RepresentativeResponse =
        withContext(ioDispatcher) {
            return@withContext CivicsApi.retrofitService.getRepresentatives(
                BuildConfig.API_KEY,
                address
            )
        }

    override suspend fun refreshData() {
        withContext(ioDispatcher) {
            val elections = getElectionsFromApi()
//            database.asteroidDao.insertAll(*asteroids.asDatabaseModel())
        }
    }

    override suspend fun getElectionsFromDatabase(): List<Election> = withContext(ioDispatcher) {
        return@withContext database.electionDao.getElections()
    }

    override suspend fun getSavedElectionById(id: String): Election? = withContext(ioDispatcher) {
        return@withContext database.electionDao.getElectionById(id)
    }

    override suspend fun getRepresentativeFromDatabase(): RepresentativeResponse =
        withContext(ioDispatcher) {
            TODO("Not yet implemented")
        }

    override suspend fun saveElection(election: Election) = withContext(ioDispatcher) {
        database.electionDao.saveElection(election)
    }

    override suspend fun deleteElection(election: Election): Int = withContext(ioDispatcher){
        return@withContext database.electionDao.deleteElection(election)
    }


}