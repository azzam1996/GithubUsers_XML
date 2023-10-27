package com.azzam.githubusers_xml.presentation.users.usersList

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.azzam.githubusers_xml.R
import com.azzam.githubusers_xml.databinding.FragmentUserListBinding
import com.azzam.githubusers_xml.domain.model.GithubUser
import com.azzam.githubusers_xml.presentation.users.UsersViewModel
import com.azzam.githubusers_xml.presentation.users.usersList.adapter.UsersAdapter
import com.azzam.githubusers_xml.utils.USER_LOGIN
import com.azzam.githubusers_xml.utils.collectLatestLifecycleFlow
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UserListFragment : Fragment(), UsersAdapter.OnUserClickedListener {

    var binding: FragmentUserListBinding? = null

    val viewModel: UsersViewModel by sharedViewModel()
    private val usersAdapter = UsersAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserListBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initAdapter()
        collectViewModelData()
    }

    private fun initViews() {
        binding?.etSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                viewModel.searchUsers(p0.toString())
            }
        })
    }

    private fun initAdapter() {
        val verticalLayoutManager = GridLayoutManager(
            activity, 1,
            GridLayoutManager.VERTICAL, false
        )

        usersAdapter.onUserClickedListener = this

        binding?.rvUsers?.layoutManager = verticalLayoutManager
        binding?.rvUsers?.adapter = usersAdapter
    }

    private fun collectViewModelData() {
        collectLatestLifecycleFlow(viewModel.loading) {
            when (it) {
                true -> {
                    binding?.loading?.visibility = View.VISIBLE
                    binding?.tvNoData?.visibility = View.GONE
                    binding?.rvUsers?.visibility = View.GONE
                }

                else -> {
                    binding?.loading?.visibility = View.GONE
                    binding?.rvUsers?.visibility = View.VISIBLE
                }
            }
        }

        collectLatestLifecycleFlow(viewModel.emptyList) {
            when (it) {
                true -> {
                    binding?.tvNoData?.visibility = View.VISIBLE
                    binding?.rvUsers?.visibility = View.GONE
                    binding?.loading?.visibility = View.GONE
                }

                else -> {
                    binding?.tvNoData?.visibility = View.GONE
                    binding?.rvUsers?.visibility = View.VISIBLE
                }
            }
        }

        collectLatestLifecycleFlow(viewModel.usersListError) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        collectLatestLifecycleFlow(viewModel.usersList) {
            it?.let { list -> usersAdapter.addItems(list) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun handleOnUserClicked(githubUser: GithubUser?) {
        findNavController().navigate(R.id.userDetailsFragment, Bundle().apply {
            githubUser?.login?.let { putString(USER_LOGIN, it) }
        })
    }
}