package com.rocket.template.base.data.network

import com.rocket.template.base.data.network.model.NetworkUserListItem
import retrofit2.http.GET

interface UserListService {

    @GET("/repos/square/retrofit/stargazers")
    suspend fun getUserList(): List<NetworkUserListItem>
}