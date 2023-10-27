package com.azzam.githubusers_xml.data.repository

import com.azzam.githubusers_xml.data.api.UsersApi
import com.azzam.githubusers_xml.data.api.safeApiCall
import com.azzam.githubusers_xml.data.mappers.toGithubUser
import com.azzam.githubusers_xml.data.mappers.toGithubUsersList
import com.azzam.githubusers_xml.domain.model.GithubUser
import com.azzam.githubusers_xml.domain.repository.UsersRepository
import com.azzam.githubusers_xml.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UsersRepositoryImpl(private val api: UsersApi) : UsersRepository {
    override fun searchUsers(keyword: String?): Flow<Resource<List<GithubUser?>?>> = flow {
        emit(Resource.Loading())

        when (val response = safeApiCall { api.searchUsers(keyword = keyword) }) {

            is Resource.Success -> {
                val githubUsersList = response.data?.items?.toGithubUsersList() ?: emptyList()
                emit(Resource.Success(data = githubUsersList))
            }

            is Resource.Error -> {
                emit(Resource.Error(message = response.message, errorCode = response.errorCode))
            }

            else -> {
                emit(Resource.Loading())
            }
        }
    }

    override fun getUser(username: String?): Flow<Resource<GithubUser?>> = flow {
        emit(Resource.Loading())

        when (val response = safeApiCall { api.getUser(username = username) }) {

            is Resource.Success -> {
                emit(Resource.Success(data = response.data?.toGithubUser()))
            }

            is Resource.Error -> {
                emit(Resource.Error(message = response.message, errorCode = response.errorCode))
            }

            else -> {
                emit(Resource.Loading())
            }
        }
    }
}