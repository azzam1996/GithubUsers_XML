package com.azzam.githubusers_xml.presentation.users


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.azzam.githubusers_xml.domain.model.GithubUser
import com.azzam.githubusers_xml.domain.repository.UsersRepository
import com.azzam.githubusers_xml.utils.DELAY
import com.azzam.githubusers_xml.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsersViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    private val searchKeyword = MutableStateFlow("")
    val usersList = MutableStateFlow<List<GithubUser?>?>(mutableListOf())
    val loading = MutableStateFlow(false)
    val emptyList = MutableStateFlow(true)

    private val _usersListError = MutableSharedFlow<String>()
    val usersListError = _usersListError.asSharedFlow()

    private var searchGithubUsersJob: Job? = null

    val user = MutableStateFlow<GithubUser?>(null)
    private val _userDetailsError = MutableSharedFlow<String>()
    val userDetailsError = _userDetailsError.asSharedFlow()

    fun searchUsers(keyword: String) {
        searchGithubUsersJob?.cancel()
        if (keyword.length >= 2 && keyword != searchKeyword.value) {
            searchKeyword.value = keyword
            searchGithubUsersJob = viewModelScope.launch(Dispatchers.IO) {
                delay(DELAY)
                usersRepository.searchUsers(keyword = keyword).onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            withContext(Dispatchers.Main) {
                                loading.value = false
                                emptyList.value = result.data.isNullOrEmpty()
                                usersList.value = result.data
                            }
                        }

                        is Resource.Error -> {
                            withContext(Dispatchers.Main) {
                                loading.value = false
                                _usersListError.emit("${result.errorCode}\n ${result.message}")
                            }
                        }

                        is Resource.Loading -> {
                            withContext(Dispatchers.Main) {
                                loading.value = true
                            }
                        }
                    }
                }.launchIn(this)
            }
        }
    }

    fun getUser(username: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            usersRepository.getUser(username).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        withContext(Dispatchers.Main) {
                            loading.value = false
                            user.value = result.data
                        }
                    }

                    is Resource.Error -> {
                        withContext(Dispatchers.Main) {
                            loading.value = false
                            _userDetailsError.emit("${result.errorCode}\n ${result.message}")
                        }
                    }

                    is Resource.Loading -> {
                        loading.value = true
                    }
                }
            }.launchIn(this)
        }
    }
}