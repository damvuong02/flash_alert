package com.rocket.template.base.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.rocket.template.base.data.network.UserDetailsService
import com.rocket.template.base.data.network.model.asDatabaseModel
import com.rocket.template.base.data.database.UsersDatabase
import com.rocket.template.base.data.database.asDomainModel
import com.rocket.template.base.data.domain.UserDetails
import timber.log.Timber
import javax.inject.Inject

class UserDetailsRepository @Inject constructor(
    private val userDetailsService: UserDetailsService,
    private val database: UsersDatabase
) {

    fun getUserDetails(user: String): LiveData<UserDetails> {
        return database.usersDao.getUserDetails(user).map {
            it.asDomainModel()
        }
    }


    suspend fun refreshUserDetails(user: String) {
        try {
            val userDetails = userDetailsService.getUserDetails(user)
            database.usersDao.insertUserDetails(userDetails.asDatabaseModel())
        } catch (e: Exception) {
            Timber.w(e)
        }
    }

}