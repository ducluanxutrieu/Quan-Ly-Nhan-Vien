package com.ducluanxutrieu.quanlynhanvien.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;

import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNewFriend extends DialogFragment {
    TextInputEditText inputEmail;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mAddFriendReference;
    FirebaseAuth mFirebaseAuth;
    TransferSignal transferSignal;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_new_friend_dialog, null, false);

        inputEmail = view.findViewById(R.id.input_email_friend);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAddFriendReference = mFirebaseDatabase.getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        builder.setView(view)
                .setTitle(getString(R.string.add_new_friend))
                .setIcon(android.R.drawable.ic_input_add)
                .setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        transferSignal.onTransferSignal("AddFriend", inputEmail.getText().toString());
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null);
        return builder.create();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        transferSignal = (TransferSignal) context;
    }
}
