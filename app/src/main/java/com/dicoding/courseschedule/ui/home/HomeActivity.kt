package com.dicoding.courseschedule.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.data.Course
import com.dicoding.courseschedule.databinding.ActivityHomeBinding
import com.dicoding.courseschedule.ui.add.AddCourseActivity
import com.dicoding.courseschedule.ui.list.ListActivity
import com.dicoding.courseschedule.ui.setting.SettingsActivity
import com.dicoding.courseschedule.util.DayName
import com.dicoding.courseschedule.util.QueryType
import com.dicoding.courseschedule.util.timeDifference

//TODO 15 : Write UI test to validate when user tap Add Course (+) Menu, the AddCourseActivity is displayed
class HomeActivity : AppCompatActivity() {

    private var queryType = QueryType.CURRENT_DAY
    private val binding by lazy {
        ActivityHomeBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory.createFactory(this)
    }

    //TODO 5 : Show nearest schedule in CardHomeView and implement menu action
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.today_schedule)

        observeViewModel()

    }
    private fun observeViewModel() {
        viewModel.getScheduleNearest().observe(this) {
            showNearestSchedule(it)
        }
    }

    private fun showNearestSchedule(course: Course?) {
        checkQueryType(course)
        course?.apply {
            val dayName = DayName.getByNumber(day)
            val time = String.format(getString(R.string.time_format), dayName, startTime, endTime)
            val remainingTime = timeDifference(day, startTime)

            val cardHome = binding.viewHome

            cardHome.apply {
                setCourseName(courseName)
                setLecturer(lecturer)
                setNote(note)
                setRemainingTime(remainingTime)
                setTime(time)
            }

        }

        binding.tvEmptyHome.visibility =
            if (course == null) View.VISIBLE else View.GONE
    }

    private fun checkQueryType(course: Course?) {
        if (course == null) {
            val newQueryType: QueryType = when (queryType) {
                QueryType.CURRENT_DAY -> QueryType.NEXT_DAY
                QueryType.NEXT_DAY -> QueryType.PAST_DAY
                else -> QueryType.CURRENT_DAY
            }
            viewModel.setQueryType(newQueryType)
            queryType = newQueryType
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val intent: Intent = when (item.itemId) {

            R.id.action_settings -> Intent(this, SettingsActivity::class.java)
            R.id.action_list -> Intent(this, ListActivity::class.java)
            R.id.action_add -> Intent(this, AddCourseActivity::class.java)
            else -> null
        } ?: return super.onOptionsItemSelected(item)

        startActivity(intent)
        return true
    }
}