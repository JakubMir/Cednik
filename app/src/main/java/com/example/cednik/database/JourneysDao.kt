package com.example.cednik.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.cednik.model.Journey
import com.example.cednik.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneysDao {

    @Query("SELECT * FROM journeys WHERE userId = :userId")
    fun getAllJourneys(userId: Long): Flow<List<Journey>>

    @Insert
    suspend fun insertJourney(journey: Journey): Long

    @Update
    suspend fun updateJourney(journey: Journey)

    @Query("SELECT * FROM journeys WHERE id = :id AND userId = :userId")
    suspend fun getJourney(id: Long, userId: Long): Journey

    @Delete
    suspend fun deleteJourney(journey: Journey)

    @Query("SELECT COUNT(*) FROM journeys")
    suspend fun getJourneysCount(): Long

    @Query("SELECT * FROM users WHERE email =:email AND password=:password")
    fun getUser(email: String, password: String): Flow<User>

    @Query("SELECT password FROM users WHERE email = :email")
    fun getUserPassword(email: String): Flow<String>

    @Query("SELECT EXISTS(SELECT * FROM users WHERE email =:email)")
    fun isEmailUsed(email: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT * FROM users WHERE email =:email AND password=:password)")
    fun isPasswordCorrect(email: String, password: String): Flow<Boolean>
    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)
}