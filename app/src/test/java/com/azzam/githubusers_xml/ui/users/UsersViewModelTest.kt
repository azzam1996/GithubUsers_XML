package com.azzam.githubusers_xml.ui.users

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.azzam.githubusers_xml.CoroutinesTestRule
import com.azzam.githubusers_xml.data.mappers.toGithubUser
import com.azzam.githubusers_xml.data.mappers.toGithubUsersList
import com.azzam.githubusers_xml.domain.repository.UsersRepository
import com.azzam.githubusers_xml.presentation.users.UsersViewModel
import com.azzam.githubusers_xml.searchUsersSuccessfulResponse
import com.azzam.githubusers_xml.user
import com.azzam.githubusers_xml.username
import com.azzam.githubusers_xml.utils.DELAY
import com.azzam.githubusers_xml.utils.Resource
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class UsersViewModelTest {

    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    @get: Rule
    val coroutinesRule = CoroutinesTestRule()

    @Mock
    lateinit var usersRepository: UsersRepository

    private lateinit var usersViewModel: UsersViewModel

    @Before
    fun setup() {
        usersViewModel = UsersViewModel(usersRepository)
    }

    @Test
    fun `call searchUsers in viewModel should call searchUsers in Repository`() = runTest {
        val privateField = UsersViewModel::class.java.getDeclaredField("searchGithubUsersJob")
        privateField.isAccessible = true

        whenever(usersRepository.searchUsers(username)).thenReturn(flowOf(Resource.Success(data = searchUsersSuccessfulResponse.items?.map { it?.toGithubUser() })))
        usersViewModel.searchUsers(username)
        val searchJob = privateField.get(usersViewModel) as Job?
        searchJob?.join()

        verify(usersRepository).searchUsers(username)
    }

    @Test
    fun `call searchUsers in viewModel with keyword less than 2 characters should not call searchUsers in Repository`() = runTest {
        val privateField = UsersViewModel::class.java.getDeclaredField("searchGithubUsersJob")
        privateField.isAccessible = true

        usersViewModel.searchUsers("A")
        val searchJob = privateField.get(usersViewModel) as Job?
        searchJob?.join()

        verify(usersRepository, never()).searchUsers("A")
    }

    @Test
    fun `if searchUsers in Repository return List of GithubUsers then ViewModel State Should be Updated`() = runTest {
        val searchJob = UsersViewModel::class.java.getDeclaredField("searchGithubUsersJob")
        searchJob.isAccessible = true

        whenever(usersRepository.searchUsers(username)).thenReturn(flowOf(Resource.Success(data = searchUsersSuccessfulResponse.items?.toGithubUsersList())))
        usersViewModel.searchUsers(username)
        val job = searchJob.get(usersViewModel) as Job
        job.join()

        assertThat(usersViewModel.usersList.value).isEqualTo(
            searchUsersSuccessfulResponse.items?.toGithubUsersList()
        )
    }

    @Test
    fun `if searchUsers in Repository return Error then ViewModel Should Send Error Event`() = runTest {
        val searchJob = UsersViewModel::class.java.getDeclaredField("searchGithubUsersJob")
        searchJob.isAccessible = true

        whenever(usersRepository.searchUsers(username)).thenReturn(
            flowOf(
                Resource.Error(
                    message = "Error",
                    errorCode = 404
                )
            )
        )

        usersViewModel.usersListError.test {
            usersViewModel.searchUsers(username)
            val job = searchJob.get(usersViewModel) as Job
            job.join()
            val item = awaitItem()
            assertThat(item).isNotNull()
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `call getUser in viewModel should call getUser in Repository`() = runTest {
        whenever(usersRepository.getUser(username)).thenReturn(flowOf(Resource.Success(data = user.toGithubUser())))
        usersViewModel.getUser(username)
        verify(usersRepository).getUser(username)
    }

    @Test
    fun `if getUser in Repository return GithubUser then ViewModel State Should be Updated`() = runTest {
        whenever(usersRepository.getUser(username)).thenReturn(flowOf(Resource.Success(data = user.toGithubUser())))
        usersViewModel.getUser(username)

        runBlocking { delay(DELAY) }
        assertThat(usersViewModel.user.value).isEqualTo(user.toGithubUser())
    }

    @Test
    fun `if getUser in Repository return Error then ViewModel Should Send Error Event`() = runTest {
        whenever(usersRepository.getUser(username)).thenReturn(
            flowOf(
                Resource.Error(
                    message = "Error",
                    errorCode = 404
                )
            )
        )

        usersViewModel.userDetailsError.test {
            usersViewModel.getUser(username)
            runBlocking { delay(DELAY) }
            val item = awaitItem()
            assertThat(item).isNotNull()
            cancelAndConsumeRemainingEvents()
        }
    }
}