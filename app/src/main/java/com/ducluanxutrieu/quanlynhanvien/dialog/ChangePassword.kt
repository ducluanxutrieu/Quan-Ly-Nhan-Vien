package com.ducluanxutrieu.quanlynhanvien.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ducluanxutrieu.quanlynhanvien.interfaces.TransferSignal
import com.ducluanxutrieu.quanlynhanvien.R
import com.ducluanxutrieu.quanlynhanvien.models.Staff
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class ChangePassword : DialogFragment() {
    private lateinit var newPassword: TextInputEditText
    private lateinit var confirmPassword: TextInputEditText
    private lateinit var cancel: Button
    private lateinit var change: Button
    private lateinit var transferSignal: TransferSignal

    //Firebase
    private lateinit var mFirebaseDatabase: FirebaseDatabase
    private var mReference: DatabaseReference? = null
    private var mFirebaseUser: FirebaseUser? = null

    var password: String? = null
    var email: String? = null
    private var staff: Staff? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_change_password, null, false)

        newPassword = view.findViewById(R.id.input_new_password)
        confirmPassword = view.findViewById(R.id.input_confirm_new_password)
        cancel = view.findViewById(R.id.btn_cancel_reset_password)
        change = view.findViewById(R.id.btn_change_password)

        mFirebaseDatabase = FirebaseDatabase.getInstance()
        val mFirebaseAuth = FirebaseAuth.getInstance()
        mFirebaseUser = mFirebaseAuth.currentUser
        mReference = mFirebaseDatabase.reference.child("user/" + mFirebaseUser!!.email!!.replace(".", ""))
        mReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                staff = dataSnapshot.getValue(Staff::class.java)
                if (staff != null) {
                    email = staff!!.email
                } else {
                    Toast.makeText(view.context, getString(R.string.can_not_change_password), Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        val builder = AlertDialog.Builder(context!!)
        builder.setView(view)
                .setTitle(getString(R.string.change_password))

        change.setOnClickListener {
            if (newPassword.text!!.toString().trim { it <= ' ' }.length < 6) {
                newPassword.error = "Password need longer than 6 character"
                newPassword.requestFocus()
            } else if (newPassword.text!!.toString() != confirmPassword.text!!.toString()) {
                confirmPassword.requestFocus()
                confirmPassword.error = "Confirm password incorrect!"
            } else {
                if (!newPassword.text!!.toString().isEmpty() && !confirmPassword.text!!.toString().isEmpty()) {
                    val credential = EmailAuthProvider.getCredential(mFirebaseUser!!.email!!, password!!)
                    mFirebaseUser!!.reauthenticate(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            mFirebaseUser!!.updatePassword(newPassword.text!!.toString()).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(view.context, getString(R.string.update_user_successful), Toast.LENGTH_SHORT).show()
                                    mReference!!.setValue(staff)
                                    dismiss()
                                } else {
                                    Toast.makeText(view.context, getString(R.string.error_password_not_update), Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
        cancel.setOnClickListener { dismiss() }
        return builder.create()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        transferSignal = (context as TransferSignal?)!!
    }
}
