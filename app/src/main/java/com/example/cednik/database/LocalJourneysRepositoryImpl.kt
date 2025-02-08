package com.example.cednik.database

import com.example.cednik.model.Journey
import com.example.cednik.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalJourneysRepositoryImpl @Inject constructor(private val dao: JourneysDao) :
    ILocalJourneysRepository {
    override fun getAllJourneys(userId: Long): Flow<List<Journey>> {
        return dao.getAllJourneys(userId)
    }

    override suspend fun insertJourney(journey: Journey): Long {
        return dao.insertJourney(journey)
    }

    override suspend fun updateJourney(journey: Journey) {
        dao.updateJourney(journey)
    }

    override suspend fun getJourney(id: Long, userId: Long): Journey {
        return dao.getJourney(id, userId)
    }

    override suspend fun deleteJourney(journey: Journey) {
        dao.deleteJourney(journey)
    }

    override suspend fun getJourneysCount(): Long {
        return dao.getJourneysCount()
    }

    override suspend fun getUser(email: String, password: String): Flow<User> {
        return dao.getUser(email, password)
    }

    override suspend fun getUserPassword(email: String): Flow<String> {
        return dao.getUserPassword(email)
    }

    override suspend fun isEmailUsed(email: String): Flow<Boolean> {
        return dao.isEmailUsed(email)
    }

    override suspend fun isPasswordCorrect(email: String, password: String): Flow<Boolean> {
        return dao.isPasswordCorrect(email, password)
    }

    override suspend fun insertUser(user: User): Long {
        return dao.insertUser(user)
    }

    override suspend fun updateUser(user: User) {
        dao.updateUser(user)
    }

    override suspend fun deleteUser(user: User) {
        dao.deleteUser(user)
    }
}