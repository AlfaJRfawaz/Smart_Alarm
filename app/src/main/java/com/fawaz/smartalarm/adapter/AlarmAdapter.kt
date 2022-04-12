package com.fawaz.smartalarm.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.fawaz.smartalarm.data.Alarm
import com.fawaz.smartalarm.databinding.ItemRowReminderAlarmBinding

class AlarmAdapter : RecyclerView.Adapter<AlarmAdapter.MyViewHolder>() {

    val listAlarm: ArrayList<Alarm> = arrayListOf()

    inner class MyViewHolder(val binding: ItemRowReminderAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MyViewHolder(
        ItemRowReminderAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val alarm = listAlarm[position]

        holder.binding.apply {
            itemDateAlarm.text = alarm.date
            itemNoteAlarm.text = alarm.note
            itemTimeAlarm.text = alarm.time
        }
    }

    override fun getItemCount() = listAlarm.size //TODO 2 perbaharui kode

    fun setDat(list: List<Alarm>){
        val alarmDiffUtil = AlarmDifUtil(listAlarm, list)
        val alarmDiffUtilResult = DiffUtil.calculateDiff(alarmDiffUtil)
        listAlarm.clear()
        listAlarm.addAll(list)
        alarmDiffUtilResult.dispatchUpdatesTo(this)
    }
}