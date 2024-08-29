package com.rocket.template.base.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.rocket.template.base.data.network.UserListService
import com.rocket.template.base.data.network.model.asDatabaseModel
import com.rocket.template.base.data.database.UsersDatabase
import com.rocket.template.base.data.database.asDomainModel
import com.rocket.template.base.data.domain.UserListItem
import timber.log.Timber
import javax.inject.Inject

class UserListRepository @Inject constructor(
    private val userListService: UserListService,
    private val database: UsersDatabase,
) {

    val users: LiveData<List<UserListItem>> =
        database.usersDao.getDatabaseUsers().map {
            it.asDomainModel()
        }

    suspend fun refreshUserList() {
        try {
            val users = userListService.getUserList()
            database.usersDao.insertAll(users.asDatabaseModel())
        } catch (e: Exception) {
            Timber.w(e)
        }
    }
}