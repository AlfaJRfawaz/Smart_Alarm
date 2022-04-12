package com.fawaz.smartalarm

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fawaz.smartalarm.data.Alarm
import com.fawaz.smartalarm.data.local.AlarmDB
import com.fawaz.smartalarm.data.local.AlarmDao
import com.fawaz.smartalarm.databinding.ActivityRepeatingAlarmBinding
import com.fawaz.smartalarm.fragment.TimePickerFragment
import com.fawaz.smartalarm.helper.timeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RepeatingTimeActivity : AppCompatActivity(), TimePickerFragment.TimeDialogListener {

    private var _binding: ActivityRepeatingAlarmBinding? = null
    private val binding get() = _binding as ActivityRepeatingAlarmBinding

    private var alarmDao: AlarmDao? = null

    private var _alarmService: AlarmService? = null
    private val alarmService get() = _alarmService as AlarmService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityRepeatingAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AlarmDB.getDataBase(this)
        alarmDao = db.alarmDao()

        _alarmService = AlarmService()

        initView()
    }

    private fun initView() {
        binding.apply {
            btnSetTimeRepeating.setOnClickListener {
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.show(supportFragmentManager, "TimePickerDialog")
            }

            btnAddSetRepeatingAlarm.setOnClickListener {
                val time = tvRepeatingTime.text.toString()
                val note = etNoteRepeating.text.toString()

                if (time != "Time") {
                    alarmService.setRepeatingAlarm(
                        applicationContext,
                        AlarmService.TYPE_Repeating,
                        time,
                        note
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        alarmDao?.addAlarm(
                            Alarm(
                                0,
                                "Repeating Alarm",
                                time,
                                note,
                                AlarmService.TYPE_Repeating
                            )
                        )
                        finish()
                    }
                } else {
                    Toast.makeText(applicationContext, "Set Alarmnya dulu oi", Toast.LENGTH_LONG).show()
                }
            }

            btnCancelSetRepeatingAlarm.setOnClickListener {
                finish()
            }
        }
    }

    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        binding.tvRepeatingTime.text = timeFormatter(hourOfDay, minute)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}