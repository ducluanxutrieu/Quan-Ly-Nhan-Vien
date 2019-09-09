package com.ducluanxutrieu.quanlynhanvien.activity

import android.annotation.SuppressLint
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import com.ducluanxutrieu.quanlynhanvien.dialog.DatePicker
import com.ducluanxutrieu.quanlynhanvien.interfaces.TransferSignal
import com.ducluanxutrieu.quanlynhanvien.R
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions

import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.HashMap
import kotlin.math.abs

class AskOffDayActivity : AppCompatActivity(), TransferSignal {
    private lateinit var chooseStartDate: TextInputEditText
    private lateinit var chooseEndDate: TextInputEditText
    private lateinit var autoDateType: AutoCompleteTextView
    private lateinit var sendRequestOffDays: MaterialButton
    private lateinit var cancel: MaterialButton
    private lateinit var inputContent: EditText
    private lateinit var dateTypeLayout: TextInputLayout
    private lateinit var dateStartLayout: TextInputLayout
    private lateinit var dateEndLayout: TextInputLayout
    private lateinit var textViewNumberDayCanOff: TextView
    private lateinit var textViewNumberThanOff: TextView
    private lateinit var date: String
    private lateinit var dateType: String
    private var numberOffDays = 0
    private var thanDays = 0
    private var dayStart = 0
    private var monthStart = 0
    private var yearStart = 0
    private var dayEnd = 0
    private var monthEnd = 0
    private var yearEnd = 0

    private lateinit var dateTypes: MutableList<String>

    private var mFunctions: FirebaseFunctions? = null
    private val timeNow: String
        get() {
            val time: String
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            time = "$hour:$minute"
            return time
        }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ask_off_day)

        mFunctions = FirebaseFunctions.getInstance()

        mapping()
        addDateType()

        //Adapter AutoCompleteTextView
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dateTypes)
        autoDateType.setAdapter(arrayAdapter)
        autoDateType.threshold = 1
        autoDateType.setOnClickListener { autoDateType.showDropDown() }

        autoDateType.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            dateType = dateTypes[position]
            dateTypeLayout.isErrorEnabled = false
            getNumberOffDays(position)
            textViewNumberDayCanOff.text = "getString(R.string.you_can_off) $numberOffDays days"
            textViewNumberDayCanOff.visibility = View.VISIBLE
        }

        chooseStartDate.setOnClickListener {
            val newFragment = DatePicker()
            newFragment.show(supportFragmentManager, "timePicker")
        }

        chooseEndDate.setOnClickListener {
            val newFragment = DatePicker()
            newFragment.onSetDate(dayEnd, monthEnd - 1, yearEnd)
            newFragment.show(supportFragmentManager, chooseEndDate.text!!.toString())
        }

        inputContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                sendRequestOffDays.isEnabled = s.toString().trim { it <= ' ' }.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        sendRequestOffDays.setOnClickListener {
            val content = inputContent.text.toString()
            val startDate = chooseStartDate.text!!.toString()
            val endDate = chooseEndDate.text!!.toString()
            if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
                //String text, String startDate, String endDate, String timeRequest, String offType, String thanDays,
                if (autoDateType.text.toString().isEmpty()) {
                    dateTypeLayout.isErrorEnabled = true
                    dateTypeLayout.isHelperTextEnabled = true
                } else {
                    val map = HashMap<String, Any>()
                    map["text"] = content
                    map["offType"] = autoDateType.text.toString()
                    map["startDate"] = startDate
                    map["endDate"] = endDate
                    map["thanDays"] = thanDays
                    map["timeRequest"] = timeNow
                    addRequestOff(map).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@AskOffDayActivity, getString(R.string.send_request_successful), Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { e -> Toast.makeText(this@AskOffDayActivity, e.message, Toast.LENGTH_SHORT).show() }
                }
            }
            finish()
        }

        cancel.setOnClickListener { finish() }
        getDateNow()
    }

    private fun getNumberOffDays(position: Int) {
        when (position) {
            0 -> numberOffDays = 1
            1 -> numberOffDays = 2
            2 -> numberOffDays = 4
            3 -> numberOffDays = 3
            4 -> numberOffDays = 1
            5 -> numberOffDays = 3
            6 -> numberOffDays = 180
            7 -> numberOffDays = 5
            8 -> numberOffDays = 7
            9 -> numberOffDays = 7
            10 -> numberOffDays = 10
            11 -> numberOffDays = 14
        }//Đột xuất
        //Bị bệnh có báo trước
        //Tai nan
        //Ket hon
        //Con ket hon
        //Dam tang
        //Sinh De
        //Vo sinh thuong
        //Vo sinh phau thuat
        //Vo sinh con duoi 32 tuan tuoi
        //Vo sinh doi
        //Vo sinh doi & phau thuat
    }

    //12/2/2019 - 10/4/2018
    //15/2/2019 - 10/2/2019
    private fun numberDaysOffEdit(): Int {
        var temp = 0
        temp += dayEnd
        while (yearEnd > yearStart) {
            if (monthEnd > 0) {
                temp += checkMonth(monthEnd, yearEnd)
                --monthEnd

            } else {
                monthEnd = 12
                --yearEnd
            }
        }

        while (monthEnd > monthStart) {
            temp += checkMonth(monthEnd, yearEnd)
            --monthEnd
        }
        //dayEnd = checkMonth(monthStart, yearStart);
        temp -= dayStart

        return temp
    }

    @SuppressLint("SimpleDateFormat")
    private fun endDayOff(): String? {
        val c = Calendar.getInstance()

        if (numberOffDays == 0) {
            return null
        } else if (numberOffDays == 180) {
            monthEnd = monthStart + 6
            if (monthEnd > 12) {
                monthEnd -= 12
                yearEnd = yearStart + 1
                dayEnd = dayStart
            } else {
                dayEnd = dayStart
                yearEnd = yearStart
            }
        } else {
            nextCoupleDay()
        }
        c.set(yearEnd, monthEnd - 1, dayEnd)
        val dateFormat = SimpleDateFormat(getString(R.string.date_format_day_month_year))
        val date = c.time
        return dateFormat.format(date)
    }

    private fun nextCoupleDay() {
        dayEnd = dayStart + numberOffDays
        if (dayEnd <= checkMonth(monthStart, yearStart)) {
            monthEnd = monthStart
            yearEnd = yearStart
        } else {
            dayEnd -= checkMonth(monthStart, yearStart)
            monthEnd = monthStart + 1
            if (monthEnd > 12) {
                monthEnd -= 12
                yearEnd = yearStart + 1
            } else {
                yearEnd = yearStart
            }
        }
    }

    private fun addDateType() {
        dateTypes = ArrayList()
        dateTypes.add(getString(R.string.off_urgent_busy))
        dateTypes.add(getString(R.string.off_sick))
        dateTypes.add(getString(R.string.off_accident))
        dateTypes.add(getString(R.string.off_married))
        dateTypes.add(getString(R.string.off_married_children))
        dateTypes.add(getString(R.string.off_obsequies))
        dateTypes.add(getString(R.string.off_birth))
        dateTypes.add(getString(R.string.off_wife_gives_birth_normal))
        dateTypes.add(getString(R.string.off_wife_gives_birth_by_surgery))
        dateTypes.add(getString(R.string.off_wife_gives_birth_under_32_weeks_old))
        dateTypes.add(getString(R.string.off_wife_gives_birth_twins))
        dateTypes.add(getString(R.string.off_wife_gives_birth_twins_by_surgery))
    }

    private fun mapping() {
        chooseStartDate = findViewById(R.id.input_date_start_ask)
        chooseEndDate = findViewById(R.id.input_date_end_ask)
        sendRequestOffDays = findViewById(R.id.btn_send_ask_off)
        cancel = findViewById(R.id.btn_cancel_ask_off)
        inputContent = findViewById(R.id.input_content_ask)
        autoDateType = findViewById(R.id.choose_date_type)
        dateTypeLayout = findViewById(R.id.date_type_layout)
        dateStartLayout = findViewById(R.id.layout_date_start_ask)
        dateEndLayout = findViewById(R.id.layout_date_end_ask)
        textViewNumberDayCanOff = findViewById(R.id.text_view_number_can_off)
        textViewNumberThanOff = findViewById(R.id.text_view_number_than_off)
    }

    @SuppressLint("DefaultLocale")
    override fun onTransferSignal(signalMessage: String, message: String) {
        if (signalMessage == "startDate") {
            dayStart = Integer.parseInt(message.substring(0, 2))
            monthStart = Integer.parseInt(message.substring(3, 5))
            yearStart = Integer.parseInt(message.substring(6))
            if (!checkValidStart(yearStart, monthStart, dayStart)) {
                dateStartLayout.isErrorEnabled = true
                dateStartLayout.error = getString(R.string.not_allowed_before_today)
                sendRequestOffDays.isEnabled = false
            } else {
                chooseStartDate.setText(message)
                sendRequestOffDays.isEnabled = true
                dateStartLayout.isErrorEnabled = false

                val numberOffDay = endDayOff()
                chooseEndDate.setText(numberOffDay)
            }
        } else {
            dayEnd = Integer.parseInt(message.substring(0, 2))
            monthEnd = Integer.parseInt(message.substring(3, 5))
            yearEnd = Integer.parseInt(message.substring(6))

            if (chooseStartDate.text!!.toString().isEmpty()) {
                dateEndLayout.error = "Please select start day off first!"
                dateEndLayout.isErrorEnabled = true
            } else {
                if (!checkValidEnd()) {
                    dateEndLayout.isErrorEnabled = true
                    sendRequestOffDays.isEnabled = false
                } else {
                    chooseEndDate.setText(message)
                    //Toast.makeText(this, "Number Off Day Edit" + numberDaysOffEdit(), Toast.LENGTH_SHORT).show();
                    thanDays = numberDaysOffEdit() - numberOffDays
                    Log.i(TAG, thanDays.toString() + "")
                    if (thanDays > 0) {
                        textViewNumberThanOff.visibility = View.VISIBLE
                        textViewNumberThanOff.text = String.format("%s%d%s", getString(R.string.more_than), thanDays, getString(R.string.days))
                    } else if (thanDays < 0) {
                        textViewNumberThanOff.visibility = View.VISIBLE
                        textViewNumberThanOff.text = String.format("%s%d%s", getString(R.string.less_than), abs(thanDays), getString(R.string.days))
                    }
                    sendRequestOffDays.isEnabled = true
                    dateEndLayout.isErrorEnabled = false

                    chooseEndDate.setText(message)
                }
            }
        }
    }

    private fun getDateNow() {
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        date = "$dayOfMonth/$month/$year"
        //chooseStartDate.setText(date);
    }

    private fun checkValidStart(yearStart: Int, monthStart: Int, dayStart: Int): Boolean {
        //get day of today
        val c = Calendar.getInstance()
        val dayNow = c.get(Calendar.DAY_OF_MONTH)
        //because month start with 0
        val monthNow = c.get(Calendar.MONTH) + 1
        val yearNow = c.get(Calendar.YEAR)
        if (yearStart > yearNow) {
            return true
        } else if (yearStart == yearNow) {
            if (monthStart > monthNow) {
                return true
            } else if (monthStart == monthNow) {
                return dayStart > dayNow
            }
        }
        return false
    }

    private fun checkValidEnd(): Boolean {
        return when {
            yearEnd > yearStart -> true
            yearEnd == yearStart -> when {
                monthEnd > monthStart -> true
                monthEnd == monthStart -> dayEnd >= dayStart
                else -> false
            }
            else -> false
        }
    }

    private fun checkMonth(month: Int, year: Int): Int {
        when (month) {
            1 -> return 31
            3 -> return 31
            5 -> return 31
            7 -> return 31
            8 -> return 31
            10 -> return 31
            12 -> return 31

            4 -> return 30
            6 -> return 30
            9 -> return 30
            11 -> return 30

            2 -> return if (leapYears(year)) {
                29
            } else {
                28
            }
            else -> return 0
        }
    }

    private fun leapYears(year: Int): Boolean {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0
    }

    private fun addRequestOff(data: Map<String, Any>): Task<String> {
        // Create the arguments to the callable function.
        return mFunctions!!
                .getHttpsCallable("addRequestOff")
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    task.result!!.data as String?
                }
    }

    override fun onBackPressed() {
        if (autoDateType.isPopupShowing) {
            autoDateType.dismissDropDown()
        } else {
            super.onBackPressed()
        }

    }

    companion object {

        private const val TAG = ".AskOffDayActivity"
    }
}
