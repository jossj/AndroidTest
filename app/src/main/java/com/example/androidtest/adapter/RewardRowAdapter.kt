package com.example.androidtest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtest.R
import com.example.androidtest.databinding.ItemRewardRowBinding
import com.example.androidtest.model.RewardRow

class RewardRowAdapter(private val rewardTypes: List<String>) :
    RecyclerView.Adapter<RewardRowAdapter.ViewHolder>() {

    private var rows: List<RewardRow> = emptyList()

    fun submitList(list: List<RewardRow>) {
        rows = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemRewardRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = rows[position]
        with(holder.binding) {
            tvStudentName.text = row.name
            tvTotal.text = "${row.total} pts"

            llBadges.removeAllViews()
            val density = root.resources.displayMetrics.density
            row.typePoints.forEachIndexed { i, pts ->
                if (pts != null && pts > 0) {
                    TextView(root.context).apply {
                        text = "${rewardTypes[i]}: $pts"
                        textSize = 10f
                        setTextColor(ContextCompat.getColor(root.context, R.color.badge_pts_text))
                        setBackgroundResource(R.drawable.bg_badge_pts)
                        val h = (8 * density).toInt()
                        val v = (3 * density).toInt()
                        setPadding(h, v, h, v)
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).also { lp -> lp.marginEnd = (8 * density).toInt() }
                        llBadges.addView(this)
                    }
                }
            }
        }
    }

    override fun getItemCount() = rows.size

    class ViewHolder(val binding: ItemRewardRowBinding) : RecyclerView.ViewHolder(binding.root)
}
