package com.azzam.githubusers_xml.presentation.users.usersList.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.azzam.githubusers_xml.R
import com.azzam.githubusers_xml.databinding.GithubUserListItemBinding
import com.azzam.githubusers_xml.domain.model.GithubUser
import com.azzam.githubusers_xml.utils.getValueOrNoData
import com.azzam.githubusers_xml.utils.loadImage

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    private var items = ArrayList<GithubUser?>()

    interface OnUserClickedListener {
        fun handleOnUserClicked(githubUser: GithubUser?)
    }

    var onUserClickedListener: OnUserClickedListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.github_user_list_item, null)
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    fun addItems(list: List<GithubUser?>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class UserViewHolder(private val view: View) :
        RecyclerView.ViewHolder(view) {
        fun bind(user: GithubUser?, position: Int) = with(view) {
            val binding = GithubUserListItemBinding.bind(view)

            binding.avatar.loadImage(user?.avatarUrl, binding.progressBar)
            binding.tvLogin.text = getValueOrNoData(user?.login, resources)
            binding.tvId.text = getValueOrNoData(user?.id.toString(), resources)

            setOnClickListener { onUserClickedListener?.handleOnUserClicked(user) }
        }
    }
}