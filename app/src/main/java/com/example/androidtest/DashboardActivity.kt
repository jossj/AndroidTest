package com.example.androidtest

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidtest.adapter.LeaderboardAdapter
import com.example.androidtest.adapter.RewardRowAdapter
import com.example.androidtest.adapter.StudentRowAdapter
import com.example.androidtest.databinding.ActivityDashboardBinding
import com.example.androidtest.model.LeaderboardEntry
import com.example.androidtest.model.Reward
import com.example.androidtest.model.RewardRequest
import com.example.androidtest.model.RewardRow
import com.example.androidtest.model.Student
import com.example.androidtest.model.StudentIdRef
import com.example.androidtest.network.ApiClient
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    private var allStudents: List<Student> = emptyList()
    private var allRewards: List<Reward> = emptyList()
    private val rewardTypes = listOf("BEHAVIOR", "ACADEMIC", "HOMEWORK", "SPORTS")

    private val leaderboardAdapter = LeaderboardAdapter()
    private val rewardRowAdapter = RewardRowAdapter(rewardTypes)
    private val studentRowAdapter = StudentRowAdapter()

    private var selectedYear: String? = null
    private var selectedClass: String? = null
    private var classroomsByYear: Map<String, List<String>> = emptyMap()
    private var sortedYearLevels: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvLeaderboard.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = leaderboardAdapter
            isNestedScrollingEnabled = false
        }
        binding.rvRewardRows.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = rewardRowAdapter
            isNestedScrollingEnabled = false
        }
        binding.rvStudents.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = studentRowAdapter
            isNestedScrollingEnabled = false
        }

        binding.btnSignOut.setOnClickListener {
            ApiClient.setCredentials("", "")
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

        loadData()
    }

    private fun loadData() {
        setLoading(true)
        lifecycleScope.launch {
            try {
                val studentsResp = ApiClient.service.getStudents()
                val rewardsResp = ApiClient.service.getRewards()
                if (studentsResp.isSuccessful && rewardsResp.isSuccessful) {
                    allStudents = studentsResp.body() ?: emptyList()
                    allRewards = rewardsResp.body() ?: emptyList()
                    populateDashboard()
                } else {
                    showError(getString(R.string.error_server, studentsResp.code()))
                }
            } catch (e: Exception) {
                showError(getString(R.string.error_network))
            }
        }
    }

    private fun populateDashboard() {
        val totalPoints = allRewards.sumOf { it.points ?: 0 }
        binding.tvStudentCount.text = allStudents.size.toString()
        binding.tvRewardCount.text = allRewards.size.toString()
        binding.tvTotalPoints.text = totalPoints.toString()

        val leaderboardEntries = allRewards
            .filter { it.student != null }
            .groupBy { "${it.student!!.firstName} ${it.student!!.lastName}" }
            .mapValues { (_, rewards) -> rewards.sumOf { it.points ?: 0 } }
            .entries.sortedByDescending { it.value }

        leaderboardAdapter.submitList(
            leaderboardEntries.mapIndexed { i, entry ->
                LeaderboardEntry(i + 1, entry.key, entry.value, totalPoints)
            }
        )

        rewardRowAdapter.submitList(
            leaderboardEntries.map { (name, total) ->
                val studentRewards = allRewards.filter { r ->
                    r.student != null && "${r.student.firstName} ${r.student.lastName}" == name
                }
                RewardRow(
                    name = name,
                    typePoints = rewardTypes.map { type ->
                        studentRewards.filter { it.type == type }
                            .sumOf { it.points ?: 0 }
                            .takeIf { it > 0 }
                    },
                    total = total
                )
            }
        )

        setupStudentFilter()
        setupAddRewardForm()
        setLoading(false)
        binding.dashboardContent.visibility = View.VISIBLE
    }

    private fun setupStudentFilter() {
        classroomsByYear = allStudents
            .filter { it.classRoom != null }
            .groupBy { it.classRoom!!.yearLevel }
            .mapValues { (_, students) -> students.map { it.classRoom!!.name }.distinct().sorted() }

        sortedYearLevels = classroomsByYear.keys.sortedBy {
            it.removePrefix("YEAR_").toIntOrNull() ?: 0
        }

        val yearAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            listOf(getString(R.string.all_year_levels)) + sortedYearLevels.map { it.replace("YEAR_", "Year ") }
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerYear.adapter = yearAdapter

        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                selectedYear = if (pos == 0) null else sortedYearLevels[pos - 1]
                selectedClass = null
                refreshClassSpinner()
                applyStudentFilter()
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        binding.spinnerClass.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                val classes = selectedYear?.let { classroomsByYear[it] } ?: emptyList()
                selectedClass = if (pos == 0) null else classes.getOrNull(pos - 1)
                applyStudentFilter()
            }
            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        refreshClassSpinner()
        applyStudentFilter()
    }

    private fun refreshClassSpinner() {
        val classes = selectedYear?.let { classroomsByYear[it] } ?: emptyList()
        val classAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            listOf(getString(R.string.all_classes)) + classes
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerClass.adapter = classAdapter
    }

    private fun applyStudentFilter() {
        studentRowAdapter.submitList(
            allStudents.filter { s ->
                (selectedYear == null || s.classRoom?.yearLevel == selectedYear) &&
                (selectedClass == null || s.classRoom?.name == selectedClass)
            }
        )
    }

    private fun setupAddRewardForm() {
        val studentAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            listOf(getString(R.string.select_student)) + allStudents.map { "${it.firstName} ${it.lastName}" }
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerRewardStudent.adapter = studentAdapter

        val typeAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_item,
            listOf(getString(R.string.select_behavior)) + rewardTypes
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        binding.spinnerRewardType.adapter = typeAdapter

        binding.btnSubmitReward.setOnClickListener { submitReward() }
    }

    private fun submitReward() {
        val studentPos = binding.spinnerRewardStudent.selectedItemPosition
        val typePos = binding.spinnerRewardType.selectedItemPosition
        val title = binding.etRewardTitle.text?.toString()?.trim() ?: ""
        val description = binding.etRewardDescription.text?.toString()?.trim()?.ifEmpty { null }
        val points = binding.etRewardPoints.text?.toString()?.toIntOrNull()

        if (studentPos == 0 || typePos == 0 || title.isEmpty() || points == null || points <= 0) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }

        val student = allStudents[studentPos - 1]
        val type = rewardTypes[typePos - 1]

        binding.btnSubmitReward.isEnabled = false
        lifecycleScope.launch {
            try {
                val response = ApiClient.service.createReward(
                    RewardRequest(
                        title = title,
                        description = description,
                        points = points,
                        type = type,
                        student = StudentIdRef(id = student.id!!)
                    )
                )
                if (response.isSuccessful) {
                    Toast.makeText(this@DashboardActivity, "Reward added successfully!", Toast.LENGTH_SHORT).show()
                    binding.etRewardTitle.text?.clear()
                    binding.etRewardDescription.text?.clear()
                    binding.etRewardPoints.setText("10")
                    binding.spinnerRewardStudent.setSelection(0)
                    binding.spinnerRewardType.setSelection(0)
                    loadData()
                } else {
                    Toast.makeText(this@DashboardActivity, getString(R.string.error_server, response.code()), Toast.LENGTH_SHORT).show()
                    binding.btnSubmitReward.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(this@DashboardActivity, getString(R.string.error_network), Toast.LENGTH_SHORT).show()
                binding.btnSubmitReward.isEnabled = true
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        if (loading) {
            binding.dashboardContent.visibility = View.GONE
            binding.tvError.visibility = View.GONE
        }
    }

    private fun showError(message: String) {
        binding.tvError.text = message
        binding.tvError.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }
}
