package com.ducluanxutrieu.quanlynhanvien.activity

import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast

import com.ducluanxutrieu.quanlynhanvien.models.RequestItem
import com.ducluanxutrieu.quanlynhanvien.R
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.functions.FirebaseFunctions

import java.util.HashMap

class RequestDetailActivity : AppCompatActivity() {
    //Firebase
    private lateinit var mFunctions: FirebaseFunctions

    private lateinit var name: TextView
    private lateinit var offType: TextInputEditText
    private lateinit var startDate: TextInputEditText
    private lateinit var endDate: TextInputEditText
    private lateinit var reason: TextInputEditText
    private lateinit var note: TextInputEditText
    private lateinit var checkBox: CheckBox
    private lateinit var btnSend: MaterialButton
    private lateinit var btnCancel: MaterialButton

    private lateinit var requestItem : RequestItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request_detail)

        mapping()

        getInstance()

        getDataFromIntent()

        setView()

        buttonSendClicked()

        buttonCancelClicked()
    }

    fun getInstance(){
        mFunctions = FirebaseFunctions.getInstance()
    }

    private fun getDataFromIntent() {
        requestItem = intent.getSerializableExtra("request") as RequestItem
    }

    private fun setView() {
        name.text = requestItem.name
        offType.setText(requestItem.offType)
        startDate.setText(requestItem.startDate)
        endDate.setText(requestItem.endDate)
        reason.setText(requestItem.text)
        note.setText(requestItem.note)
        checkBox.isChecked = requestItem.isAccept
    }

    private fun buttonCancelClicked() {
        btnCancel.setOnClickListener { finish() }
    }


    private fun buttonSendClicked() {
        btnSend.setOnClickListener {
            if (!checkBox.isChecked) {
                Toast.makeText(this@RequestDetailActivity, getString(R.string.denied_user_off), Toast.LENGTH_SHORT).show()
                requestItem.isAccept = false
                setAccept(requestItem)
                //DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            } else {
                requestItem.isAccept = true
                setAccept(requestItem)
            }
            addMessage(note.text!!.toString(), requestItem.uid).addOnFailureListener { e -> Toast.makeText(this@RequestDetailActivity, e.message, Toast.LENGTH_SHORT).show() }
            finish()
        }
    }


    private fun mapping() {
        name = findViewById(R.id.request_detail_name)
        offType = findViewById(R.id.request_detail_off_type)
        startDate = findViewById(R.id.request_detail_start_date)
        endDate = findViewById(R.id.request_detail_end_date)
        reason = findViewById(R.id.request_detail_reason)
        note = findViewById(R.id.request_detail_note)
        checkBox = findViewById(R.id.request_detail_accept)
        btnCancel = findViewById(R.id.request_detail_cancel)
        btnSend = findViewById(R.id.request_detail_send)
    }

    private fun addMessage(text: String, toUid: String?): Task<String> {
        // Create the arguments to the callable function.
        val data = HashMap<String, Any>()
        data["text"] = text
        data["to"] = toUid!!
        data["push"] = true

        return mFunctions
                .getHttpsCallable("addMessage")
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    task.result!!.data as String?
                }
    }

    private fun setAccept(requestItem: RequestItem) {
        val referenceRequest = FirebaseDatabase.getInstance().reference.child("request_from_staff/" + "/" + requestItem.requestKey)
        referenceRequest.setValue(requestItem)
    }
}
