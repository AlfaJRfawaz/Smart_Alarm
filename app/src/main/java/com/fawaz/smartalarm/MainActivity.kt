package com.fawaz.smartalarm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fawaz.smartalarm.adapter.AlarmAdapter
import com.fawaz.smartalarm.data.local.AlarmDB
import com.fawaz.smartalarm.data.local.AlarmDao
import com.fawaz.smartalarm.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding as ActivityMainBinding

    private var alarmAdapter: AlarmAdapter? = null

    private var alarmDao: AlarmDao? = null

    private var alarmService: AlarmService? = null


    override fun onResume() {
        super.onResume()
        alarmDao?.getAlarm()?.observe(this) {
            alarmAdapter?.setDat(it)
            Log.i("GetAlarm", "getAlarm : alarm with $it")
        }


//        CoroutineScope(Dispatchers.IO).launch {  // code lain
//            val al = alarmDao?.getAlarm()
//            withContext(Dispatchers.Main) {
//                al?.let { alarmAdapter?.setDat(it) }
//            }
//            Log.i("GetAlarm", "getAlarm : alarm with $al")
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AlarmDB.getDataBase(applicationContext)
        alarmDao = db.alarmDao()

        alarmService = AlarmService()

        initView()

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.rvReminderAlarm.apply {
            alarmAdapter = AlarmAdapter()
            layoutManager = LinearLayoutManager(applicationContext)
            adapter = alarmAdapter
            swipeToDelete(this)
        }
    }

    private fun initView() {
        binding.apply {
            cvSetOneTime.setOnClickListener {
                startActivity(
                    Intent(
                        applicationContext,
                        OneTimeAlarmActivity::class.java
                    )
                )  //bisa pake applicationContext, bisa juga pake this@MainActivity
            }

            cvSetRepeatingTime.setOnClickListener {
                startActivity(Intent(this@MainActivity, RepeatingAlarmActivity::class.java))
            }
        }
        getTimeToday()
    }

    private fun getTimeToday() {
        binding.tvTimeToday.format12Hour
        binding.tvTimeToday.format24Hour
    }

    private fun swipeToDelete(recyclerView: RecyclerView) {
        ItemTouchHelper( object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) { //TODO hapus yang sebaris notifyItemRemove
                val deletedAlarm = alarmAdapter?.listAlarm?.get(viewHolder.adapterPosition)
                CoroutineScope(Dispatchers.IO).launch {
                    deletedAlarm?.let { alarmDao?.deleteAlarm(it) }
                    Log.i("DeletedAlarm", "onSwiped : deletedAlarm $deletedAlarm")
                }
                val alarmType = deletedAlarm?.type
                alarmType?.let { alarmService?.cancelAlarm(baseContext, it) }
            }
        }).attachToRecyclerView(recyclerView)
    }
}