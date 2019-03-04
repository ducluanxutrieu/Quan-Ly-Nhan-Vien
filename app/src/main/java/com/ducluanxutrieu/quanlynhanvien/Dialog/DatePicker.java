package com.ducluanxutrieu.quanlynhanvien.Dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;

import java.util.Calendar;

public class DatePicker extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    TransferSignal transferSignal;
    final Calendar c = Calendar.getInstance();
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        // Create a new instance of TimePickerDialog and return it
        return new DatePickerDialog(getContext(), this, year, month, day);
    }


    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
        c.set(year, month, dayOfMonth);
        transferSignal = (TransferSignal) getActivity();
        month += 1;
        transferSignal.onTransferSignal("Date", dayOfMonth + "/" + month + "/" + year);
    }
}