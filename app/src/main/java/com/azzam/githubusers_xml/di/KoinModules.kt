package com.azzam.githubusers_xml.di


import com.azzam.githubusers_xml.data.api.RetrofitFactory
import com.azzam.githubusers_xml.data.repository.UsersRepositoryImpl
import com.azzam.githubusers_xml.domain.repository.UsersRepository
import com.azzam.githubusers_xml.presentation.users.UsersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repositoryModule = module {
    single { UsersRepositoryImpl(get()) as UsersRepository }
}

val viewModelModule = module {
    viewModel { UsersViewModel(get()) }
}

val networkModule = module {
    single { RetrofitFactory.create(get()) }
}