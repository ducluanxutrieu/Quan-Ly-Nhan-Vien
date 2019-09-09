package com.ducluanxutrieu.quanlynhanvien.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.google.android.material.textfield.TextInputEditText
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog

import com.ducluanxutrieu.quanlynhanvien.interfaces.TransferSignal
import com.ducluanxutrieu.quanlynhanvien.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddNewFriend : DialogFragment() {
     private lateinit var inputEmail: TextInputEditText
     lateinit var mFirebaseDatabase: FirebaseDatabase
     lateinit var mAddFriendReference: DatabaseReference
     lateinit var mFirebaseAuth: FirebaseAuth
     lateinit var transferSignal: TransferSignal
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_new_friend, null, false)

        inputEmail = view.findViewById(R.id.input_email_friend)
        mFirebaseDatabase = FirebaseDatabase.getInstance()
        mAddFriendReference = mFirebaseDatabase.reference
        mFirebaseAuth = FirebaseAuth.getInstance()

        builder.setView(view)
                .setTitle(getString(R.string.add_new_friend))
                .setIcon(android.R.drawable.ic_input_add)
                .setPositiveButton(getString(R.string.add)) { dialog, which -> transferSignal.onTransferSignal("AddFriend", inputEmail.text!!.toString()) }
                .setNegativeButton(getString(R.string.cancel), null)
        return builder.create()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        transferSignal = context as TransferSignal
    }
}
