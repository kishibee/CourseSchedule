package com.dicoding.courseschedule.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.databinding.ActivityAddCourse2Binding
import com.dicoding.courseschedule.util.TimePickerFragment
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    private val viewModel by viewModels<AddCourseViewModel> {
        AddCourseViewModelFactory.createFactory(this)
    }
    private val binding by lazy {
        ActivityAddCourse2Binding.inflate(layoutInflater)
    }
    private var startTime = ""
    private var endTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_insert -> {
                val courseName = binding.edCourseName.text.toString()
                val spinner = binding.spinnerDay.selectedItem.toString()
                val spinnerNumber = getDayNumber(spinner)
                val lecturer = binding.edLecturer.text.toString()
                val note = binding.edNote.text.toString()

                when {
                    courseName.isEmpty() -> false
                    startTime.isEmpty() -> false
                    endTime.isEmpty() -> false
                    spinnerNumber == -1 -> false
                    lecturer.isEmpty() -> false
                    note.isEmpty() -> false
                    else -> {
                        viewModel.insertCourse(
                            courseName,
                            spinnerNumber,
                            startTime,
                            endTime,
                            lecturer,
                            note
                        )
                        finish()
                        true
                    }
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun pickerShowTime(view: View) {
        val tag = when (view.id) {
            R.id.ib_start_time -> "start_time"
            R.id.ib_end_time -> "end_time"
            else -> "default"
        }

        val dialogFragment = TimePickerFragment()
        dialogFragment.show(supportFragmentManager, tag)
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when (tag) {
            "start_time" -> {
                findViewById<TextView>(R.id.tv_start_time).text = timeFormat.format(calendar.time)
                startTime = timeFormat.format(calendar.time)
            }
            "end_time" -> {
                findViewById<TextView>(R.id.tv_end_time).text = timeFormat.format(calendar.time)
                endTime = timeFormat.format(calendar.time)
            }
        }
    }

    private fun getDayNumber(dayName: String): Int {
        val days = resources.getStringArray(R.array.day)
        return days.indexOf(dayName)
    }
}