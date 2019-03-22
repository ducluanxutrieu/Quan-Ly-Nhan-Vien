package com.ducluanxutrieu.quanlynhanvien.Dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;
import com.google.firebase.database.annotations.NotNull;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class DatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    String signal;
    TransferSignal transferSignal;
    final Calendar c = Calendar.getInstance();
    int day = 0, month = 0, year = 0;
    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        if (day == 0){
            day = c.get(Calendar.DAY_OF_MONTH);
            month = c.get(Calendar.MONTH);
            year = c.get(Calendar.YEAR);
            signal = "start";
        }else {
            signal = "end";
        }

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(Objects.requireNonNull(getContext()), this, year, month, day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        c.set(year, month, dayOfMonth);
        transferSignal = (TransferSignal) getActivity();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = c.getTime();
        String dateGet = dateFormat.format(date);
        assert transferSignal != null;
        if (signal.equals("start")) {
            transferSignal.onTransferSignal("startDate", dateGet);
        }else {
            transferSignal.onTransferSignal("endDate", dateGet);
        }
    }

    public void onSetDate(int endDay, int endMonth, int endYear){
        day = endDay;
        month = endMonth;
        year = endYear;
    }
}