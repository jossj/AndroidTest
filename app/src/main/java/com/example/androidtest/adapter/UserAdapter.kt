package com.example.androidtest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtest.databinding.ItemUserBinding
import com.example.androidtest.model.User

class UserAdapter(private var users: List<User> = emptyList()) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    fun submitList(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.tvUsername.text = user.username
            binding.tvEmail.text = user.email
            binding.tvRole.text = user.role
            binding.tvAvatar.text = user.username.first().uppercaseChar().toString()
        }
    }
}
