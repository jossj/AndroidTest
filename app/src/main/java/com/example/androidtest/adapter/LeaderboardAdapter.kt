package com.example.androidtest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtest.databinding.ItemLeaderboardEntryBinding
import com.example.androidtest.model.LeaderboardEntry

class LeaderboardAdapter : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    private var entries: List<LeaderboardEntry> = emptyList()

    fun submitList(list: List<LeaderboardEntry>) {
        entries = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemLeaderboardEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        with(holder.binding) {
            tvRank.text = when (position) {
                0 -> "🥇"
                1 -> "🥈"
                2 -> "🥉"
                else -> "${position + 1}."
            }
            tvName.text = entry.name
            tvPoints.text = "${entry.points} pts"
            pbLeaderboard.max = 100
            pbLeaderboard.progress =
                if (entry.totalPoints > 0) (entry.points * 100) / entry.totalPoints else 0
        }
    }

    override fun getItemCount() = entries.size

    class ViewHolder(val binding: ItemLeaderboardEntryBinding) : RecyclerView.ViewHolder(binding.root)
}
