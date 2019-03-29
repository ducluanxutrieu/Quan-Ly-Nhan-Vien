package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Dialog.DatePicker;
import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AskOffDayActivity extends AppCompatActivity implements TransferSignal {
    TextInputEditText chooseStartDate, chooseEndDate;
    AutoCompleteTextView autoDateType;
    MaterialButton sendRequestOffDays, cancel;
    EditText inputContent;
    TextInputLayout dateTypeLayout, dateStartLayout, dateEndLayout;
    TextView textViewNumberDayCanOff, textViewNumberThanOff;
    String date;
    String dateType;
    int numberOffDays = 0;
    int thanDays = 0;
    int dayStart = 0, monthStart = 0, yearStart = 0;
    int dayEnd = 0, monthEnd = 0, yearEnd = 0;

    List<String> dateTypes;

    private FirebaseFunctions mFunctions;

    private static final String TAG = ".AskOffDayActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_off_day);

        mFunctions = FirebaseFunctions.getInstance();

        mapping();
        addDateType();

        //Adapter AutoCompleteTextView
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dateTypes);
        autoDateType.setAdapter(arrayAdapter);
        autoDateType.setThreshold(1);
        autoDateType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoDateType.showDropDown();
            }
        });

        autoDateType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dateType = dateTypes.get(position);
                dateTypeLayout.setErrorEnabled(false);
                getNumberOffDays(position);
                textViewNumberDayCanOff.setText("You can off " + numberOffDays + " days");
                textViewNumberDayCanOff.setVisibility(View.VISIBLE);
            }
        });

        chooseStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePicker();
                newFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

        chooseEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePicker();
                ((DatePicker) newFragment).onSetDate(dayEnd, monthEnd - 1, yearEnd);
                newFragment.show(getSupportFragmentManager(), chooseEndDate.getText().toString());

            }
        });

        inputContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0){
                    sendRequestOffDays.setEnabled(true);
                }else {
                    sendRequestOffDays.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendRequestOffDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = inputContent.getText().toString();
                String startDate = chooseStartDate.getText().toString();
                String endDate = chooseEndDate.getText().toString();
                if (!startDate.isEmpty() && !endDate.isEmpty()){
                    //String text, String startDate, String endDate, String timeRequest, String offType, String thanDays,
                    if (autoDateType.getText().toString().isEmpty()){
                        dateTypeLayout.setErrorEnabled(true);
                        dateTypeLayout.setHelperTextEnabled(true);
                    }else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("text", content);
                        map.put("offType", autoDateType.getText().toString());
                        map.put("startDate", startDate);
                        map.put("endDate", endDate);
                        map.put("thanDays", thanDays);
                        map.put("timeRequest", getTimeNow());
                        addRequestOff(map).addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(AskOffDayActivity.this, getString(R.string.send_request_successful), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AskOffDayActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getDateNow();
    }

    private void getNumberOffDays(int position) {
        switch (position){
            case 0: numberOffDays = 1; break; //Đột xuất
            case 1 : numberOffDays = 2; break; //Bị bệnh có báo trước
            case 2 : numberOffDays = 4; break; //Tai nan
            case 3 : numberOffDays = 3; break; //Ket hon
            case 4 : numberOffDays = 1; break; //Con ket hon
            case 5 : numberOffDays = 3; break; //Dam tang
            case 6 : numberOffDays = 180; break; //Sinh De
            case 7 : numberOffDays = 5; break; //Vo sinh thuong
            case 8 : numberOffDays = 7; break; //Vo sinh phau thuat
            case 9 : numberOffDays = 7; break; //Vo sinh con duoi 32 tuan tuoi
            case 10 : numberOffDays = 10; break; //Vo sinh doi
            case 11 : numberOffDays = 14; break; //Vo sinh doi & phau thuat
        }
    }

    //12/2/2019 - 10/4/2018
    //15/2/2019 - 10/2/2019
    private int numberDaysOffEdit(){
        int temp = 0;
        temp += dayEnd;
        while (yearEnd > yearStart){
            if (monthEnd > 0){
                temp += checkMonth(monthEnd, yearEnd);
                --monthEnd;

            }else {
                monthEnd = 12;
                --yearEnd;
            }
        }

        while (monthEnd > monthStart){
            temp += checkMonth(monthEnd, yearEnd);
            --monthEnd;
        }
        //dayEnd = checkMonth(monthStart, yearStart);
        temp -= dayStart;

        return temp;
    }

    private String endDayOff(){
        Calendar c = Calendar.getInstance();

        if (numberOffDays == 0){
            return null;
        }else if (numberOffDays == 180){
            monthEnd = monthStart + 6;
            if (monthEnd > 12){
                monthEnd -= 12;
                yearEnd = yearStart + 1;
                dayEnd = dayStart;
            }else {
                dayEnd = dayStart;
                yearEnd = yearStart;
            }
        }else {
            nextCoupleDay();
        }
        c.set(yearEnd, monthEnd - 1, dayEnd);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = c.getTime();
        return dateFormat.format(date);
    }

    private void nextCoupleDay() {
        dayEnd = dayStart + numberOffDays;
        if (dayEnd <= checkMonth(monthStart, yearStart)){
            monthEnd = monthStart;
            yearEnd = yearStart;
        }else {
            dayEnd -= checkMonth(monthStart, yearStart);
            monthEnd = monthStart + 1;
            if (monthEnd > 12){
                monthEnd -= 12;
                yearEnd = yearStart + 1;
            }else {
                yearEnd = yearStart;
            }
        }
    }

    private void addDateType() {
        dateTypes = new ArrayList<>();
        dateTypes.add(getString(R.string.off_urgent_busy));
        dateTypes.add(getString(R.string.off_sick));
        dateTypes.add(getString(R.string.off_accident));
        dateTypes.add(getString(R.string.off_married));
        dateTypes.add(getString(R.string.off_married_children));
        dateTypes.add(getString(R.string.off_obsequies));
        dateTypes.add(getString(R.string.off_birth));
        dateTypes.add(getString(R.string.off_wife_gives_birth_normal));
        dateTypes.add(getString(R.string.off_wife_gives_birth_by_surgery));
        dateTypes.add(getString(R.string.off_wife_gives_birth_under_32_weeks_old));
        dateTypes.add(getString(R.string.off_wife_gives_birth_twins));
        dateTypes.add(getString(R.string.off_wife_gives_birth_twins_by_surgery));
    }

    private void mapping() {
        chooseStartDate = findViewById(R.id.input_date_start_ask);
        chooseEndDate = findViewById(R.id.input_date_end_ask);
        sendRequestOffDays = findViewById(R.id.btn_send_ask_off);
        cancel = findViewById(R.id.btn_cancel_ask_off);
        inputContent = findViewById(R.id.input_content_ask);
        autoDateType = findViewById(R.id.choose_date_type);
        dateTypeLayout = findViewById(R.id.date_type_layout);
        dateStartLayout = findViewById(R.id.layout_date_start_ask);
        dateEndLayout = findViewById(R.id.layout_date_end_ask);
        textViewNumberDayCanOff = findViewById(R.id.text_view_number_can_off);
        textViewNumberThanOff = findViewById(R.id.text_view_number_than_off);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onTransferSignal(String signalMessage, String message) {
        if (signalMessage.equals("startDate")) {
            dayStart = Integer.parseInt(message.substring(0, 2));
            monthStart = Integer.parseInt(message.substring(3, 5));
            yearStart = Integer.parseInt(message.substring(6));
            if (!checkValidStart(yearStart, monthStart, dayStart)) {
                dateStartLayout.setErrorEnabled(true);
                dateStartLayout.setError(getString(R.string.not_allowed_before_today));
                sendRequestOffDays.setEnabled(false);
            } else {
                chooseStartDate.setText(message);
                sendRequestOffDays.setEnabled(true);
                dateStartLayout.setErrorEnabled(false);

                String numberOffDay = endDayOff();
                chooseEndDate.setText(numberOffDay);
            }
        }else {
            dayEnd = Integer.parseInt(message.substring(0, 2));
            monthEnd = Integer.parseInt(message.substring(3, 5));
            yearEnd = Integer.parseInt(message.substring(6));

            if (chooseStartDate.getText().toString().isEmpty()){
                dateEndLayout.setError("Please select start day off first!");
                dateEndLayout.setErrorEnabled(true);
            }else {
                if (!checkValidEnd()) {
                    dateEndLayout.setErrorEnabled(true);
                    sendRequestOffDays.setEnabled(false);
                } else {
                    chooseEndDate.setText(message);
                    //Toast.makeText(this, "Number Off Day Edit" + numberDaysOffEdit(), Toast.LENGTH_SHORT).show();
                    thanDays = numberDaysOffEdit() - numberOffDays;
                    Log.i(TAG, thanDays + "");
                    if (thanDays > 0) {
                        textViewNumberThanOff.setVisibility(View.VISIBLE);
                        textViewNumberThanOff.setText(String.format("%s%d%s", getString(R.string.more_than), thanDays, getString(R.string.days)));
                    } else if (thanDays < 0) {
                        textViewNumberThanOff.setVisibility(View.VISIBLE);
                        textViewNumberThanOff.setText(String.format("%s%d%s", getString(R.string.less_than), Math.abs(thanDays), getString(R.string.days)));
                    }
                    sendRequestOffDays.setEnabled(true);
                    dateEndLayout.setErrorEnabled(false);

                    chooseEndDate.setText(message);
                }
            }
        }
    }
    private String getTimeNow() {
        String time;
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        time = hour + ":" + minute;
        return time;
    }

    private void getDateNow(){
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        date = dayOfMonth + "/" + month + "/" + year;
        //chooseStartDate.setText(date);
    }

    private boolean checkValidStart(int yearStart, int monthStart, int dayStart) {
        //get day of today
        final Calendar c = Calendar.getInstance();
        int dayNow = c.get(Calendar.DAY_OF_MONTH);
        //because month start with 0
        int monthNow = c.get(Calendar.MONTH) + 1;
        int yearNow = c.get(Calendar.YEAR);
        if (yearStart > yearNow) {
            return true;
        } else if (yearStart == yearNow) {
            if (monthStart > monthNow) {
                return true;
            } else if (monthStart == monthNow) {
                return dayStart > dayNow;
            }
        }
        return false;
    }

    private boolean checkValidEnd() {
        if (yearEnd > yearStart) {
            return true;
        } else if (yearEnd == yearStart) {
            if (monthEnd > monthStart) {
                return true;
            } else if (monthEnd == monthStart) {
                return dayEnd >= dayStart;
            } else return false;
        } else return false;
    }
    private int checkMonth(int month, int year) {
        switch (month) {
            case 1: return 31;
            case 3: return 31;
            case 5: return 31;
            case 7: return 31;
            case 8: return 31;
            case 10: return 31;
            case 12: return 31;

            case 4: return 30;
            case 6: return 30;
            case 9: return 30;
            case 11: return 30;

            case 2:
                if (leapYears(year)) {
                    return 29;
                } else {
                    return 28;
                }
            default: return 0;
        }
    }
    private boolean leapYears(int year){
        return (year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0);
    }

    private Task<String> addRequestOff(Map<String, Object> data) {
        // Create the arguments to the callable function.
        return mFunctions
                .getHttpsCallable("addRequestOff")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        return (String) task.getResult().getData();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (autoDateType.isPopupShowing()){
            autoDateType.dismissDropDown();
        }else {
            super.onBackPressed();
        }

    }
}
