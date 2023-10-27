package com.azzam.githubusers_xml.presentation.users.userDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.azzam.githubusers_xml.databinding.FragmentUserDetailsBinding
import com.azzam.githubusers_xml.domain.model.GithubUser
import com.azzam.githubusers_xml.presentation.users.UsersViewModel
import com.azzam.githubusers_xml.utils.USER_LOGIN
import com.azzam.githubusers_xml.utils.collectLatestLifecycleFlow
import com.azzam.githubusers_xml.utils.getValueOrNoData
import com.azzam.githubusers_xml.utils.loadImage
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UserDetailsFragment : Fragment() {

    private var binding: FragmentUserDetailsBinding? = null
    private val viewModel: UsersViewModel by sharedViewModel()

    private var userLogin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userLogin = it.getString(USER_LOGIN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserDetailsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectViewModelData()

        if (!userLogin.isNullOrEmpty()) {
            viewModel.getUser(userLogin)
        }
    }

    private fun collectViewModelData() {
        collectLatestLifecycleFlow(viewModel.loading) {
            when (it) {
                true -> {
                    binding?.loading?.visibility = View.VISIBLE
                    binding?.clContent?.visibility = View.GONE
                }

                else -> {
                    binding?.loading?.visibility = View.GONE
                    binding?.clContent?.visibility = View.VISIBLE
                }
            }
        }


        collectLatestLifecycleFlow(viewModel.userDetailsError) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }

        collectLatestLifecycleFlow(viewModel.user) {
            it?.let { handleUIData(it) }
        }
    }

    private fun handleUIData(githubUser: GithubUser) {
        binding?.let {
            it.avatar.loadImage(githubUser.avatarUrl, it.progressBar)

            it.tvBio.text = getValueOrNoData(githubUser.bio, resources)
            it.tvName.text = getValueOrNoData(githubUser.name, resources)
            it.tvId.text = getValueOrNoData(githubUser.id?.toString(), resources)

            it.lvEmail.setValues("Email", getValueOrNoData(githubUser.email, resources))
            it.lvCompany.setValues("Company", getValueOrNoData(githubUser.company, resources))
            it.lvFollowers.setValues("Followers", getValueOrNoData(githubUser.followers.toString(), resources))
            it.lvFollowing.setValues("Following", getValueOrNoData(githubUser.following.toString(), resources))
            it.lvPublicRepos.setValues("Public Repos", getValueOrNoData(githubUser.publicRepos.toString(), resources))
            it.lvPublicGists.setValues("Public Gists", getValueOrNoData(githubUser.publicGists.toString(), resources))
            it.lvLocation.setValues("Location", getValueOrNoData(githubUser.location, resources))
            it.lvTwitter.setValues("Twitter", getValueOrNoData(githubUser.twitterUsername, resources))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}