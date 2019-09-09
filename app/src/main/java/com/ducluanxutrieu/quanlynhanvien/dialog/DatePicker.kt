package com.ducluanxutrieu.quanlynhanvien.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment

import com.ducluanxutrieu.quanlynhanvien.interfaces.TransferSignal
import com.google.firebase.database.annotations.NotNull


import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Objects

class DatePicker : DialogFragment(), DatePickerDialog.OnDateSetListener {
    internal var signal: String
    internal var transferSignal: TransferSignal? = null
    internal val c = Calendar.getInstance()
    internal var day = 0
    internal var month = 0
    internal var year = 0
    @NotNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        if (day == 0) {
            day = c.get(Calendar.DAY_OF_MONTH)
            month = c.get(Calendar.MONTH)
            year = c.get(Calendar.YEAR)
            signal = "start"
        } else {
            signal = "end"
        }

        // Create a new instance of TimePickerDialog and return it
        return DatePickerDialog(Objects.requireNonNull<Context>(context), this, year, month, day)
    }

    override fun onDateSet(view: android.widget.DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        c.set(year, month, dayOfMonth)
        transferSignal = activity as TransferSignal?
        val dateFormat = SimpleDateFormat("dd-MM-yyyy")
        val date = c.time
        val dateGet = dateFormat.format(date)
        assert(transferSignal != null)
        if (signal == "start") {
            transferSignal!!.onTransferSignal("startDate", dateGet)
        } else {
            transferSignal!!.onTransferSignal("endDate", dateGet)
        }
    }

    fun onSetDate(endDay: Int, endMonth: Int, endYear: Int) {
        day = endDay
        month = endMonth
        year = endYear
    }
}