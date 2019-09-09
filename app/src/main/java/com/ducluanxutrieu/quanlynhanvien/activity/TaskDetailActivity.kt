package com.ducluanxutrieu.quanlynhanvien.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText

import com.ducluanxutrieu.quanlynhanvien.models.Tasks
import com.google.android.gms.tasks.Task
import com.ducluanxutrieu.quanlynhanvien.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.functions.FirebaseFunctions

import java.util.HashMap

class TaskDetailActivity : AppCompatActivity() {
    private var inputTitleTask: EditText? = null
    private var inputContentTask: EditText? = null
    private var btnCancel: Button? = null
    private var btnSave: Button? = null

    private var task: Tasks? = null
    private var keyTask: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        mapping()

        val intent = intent
        val signal = intent.getStringExtra("signal")

        if (signal == "edit") {
            task = intent.getSerializableExtra("task") as Tasks
            keyTask = intent.getStringExtra("key")
            inputTitleTask!!.setText(task!!.taskTitle)
            inputContentTask!!.setText(task!!.taskContent)
        }
        inputTitleTask!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                btnSave!!.isEnabled = s.toString().trim { it <= ' ' }.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        btnSave!!.setOnClickListener {
            val title = inputTitleTask!!.text.toString()
            val content = inputContentTask!!.text.toString()
            if (title.isNotEmpty() && content.isNotEmpty()) {
                if (task == null) {
                    addTask(title, content)
                } else {
                    task!!.taskTitle = title
                    task!!.taskContent = content
                    val reference = FirebaseDatabase.getInstance().reference.child("task/" + FirebaseAuth.getInstance().uid!!)
                    reference.child(keyTask!!).setValue(task)
                }
            }
            finish()
        }

        btnCancel!!.setOnClickListener { finish() }
    }

    private fun mapping() {
        inputTitleTask = findViewById(R.id.input_title_task_edit_activity)
        inputContentTask = findViewById(R.id.input_content_task_edit_text_activity)
        btnCancel = findViewById(R.id.btn_cancel_task_activity)
        btnSave = findViewById(R.id.btn_save_task_activity)
    }

    private fun addTask(title: String, content: String): Task<String> {
        val mFunctions: FirebaseFunctions = FirebaseFunctions.getInstance()
        val map = HashMap<String, Any>()
        map["taskTitle"] = title
        map["taskContent"] = content

        return mFunctions.getHttpsCallable("addTask")
                .call(map)
                .continueWith { task -> task.result!!.data as String? }

    }
}
