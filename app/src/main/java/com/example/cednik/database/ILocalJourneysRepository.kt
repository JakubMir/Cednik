package com.example.cednik.database

import com.example.cednik.model.Journey
import com.example.cednik.model.User
import kotlinx.coroutines.flow.Flow

interface ILocalJourneysRepository {
    fun getAllJourneys(userId: Long): Flow<List<Journey>>
    suspend fun insertJourney(journey: Journey): Long
    suspend fun updateJourney(journey: Journey)
    suspend fun getJourney(id: Long, userId: Long): Journey
    suspend fun deleteJourney(journey: Journey)
    suspend fun getJourneysCount(): Long

    suspend fun getUser(email: String, password: String): Flow<User>
    suspend fun getUserPassword(email: String): Flow<String>

    suspend fun isEmailUsed(email: String): Flow<Boolean>

    suspend fun isPasswordCorrect(email: String, password: String): Flow<Boolean>

    suspend fun insertUser(user: User): Long

    suspend fun updateUser(user: User)

    suspend fun deleteUser(user: User)
}