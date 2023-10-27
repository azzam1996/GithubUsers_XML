package com.azzam.githubusers_xml.domain.repository

import com.azzam.githubusers_xml.domain.model.GithubUser
import com.azzam.githubusers_xml.utils.Resource
import kotlinx.coroutines.flow.Flow


interface UsersRepository {
    fun searchUsers(keyword: String?): Flow<Resource<List<GithubUser?>?>>
    fun getUser(username: String?): Flow<Resource<GithubUser?>>
}