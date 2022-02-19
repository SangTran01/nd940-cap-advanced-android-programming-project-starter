package com.example.android.politicalpreparedness.database

import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Query("SELECT * FROM election_table")
    suspend fun getElections(): List<Election>

    @Query("SELECT * FROM election_table where id = :id")
    suspend fun getElectionById(id: String): Election?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveElection(election: Election)

    @Delete
    suspend fun deleteElection(election: Election): Int

    @Query("DELETE FROM election_table")
    suspend fun deleteAllElections()

}