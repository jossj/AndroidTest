package com.example.androidtest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtest.databinding.ItemStudentRowBinding
import com.example.androidtest.model.Student

class StudentRowAdapter : RecyclerView.Adapter<StudentRowAdapter.ViewHolder>() {

    private var students: List<Student> = emptyList()

    fun submitList(list: List<Student>) {
        students = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemStudentRowBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        with(holder.binding) {
            tvStudentName.text = "${student.firstName} ${student.lastName}"
            tvStudentEmail.text = student.email
            tvYearLevel.text = student.classRoom?.yearLevel?.replace("YEAR_", "Year ") ?: "—"
            tvClassRoom.text = student.classRoom?.name ?: "—"
        }
    }

    override fun getItemCount() = students.size

    class ViewHolder(val binding: ItemStudentRowBinding) : RecyclerView.ViewHolder(binding.root)
}
