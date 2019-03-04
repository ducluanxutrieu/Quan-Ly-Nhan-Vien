package com.ducluanxutrieu.quanlynhanvien.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Interface.TransferSignal;
import com.ducluanxutrieu.quanlynhanvien.Item.Users;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends DialogFragment {
    TextInputEditText currentPassword, newPassword, confirmPassword;
    Button cancel, change;
    TransferSignal transferSignal;

    //Firebase
    FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private FirebaseUser mFirebaseUser;

    public String password, email;
    View view;
    Users users;
    final static String TAG = ".ChangePassword";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.change_password_dialog, null, false);

        currentPassword = view.findViewById(R.id.input_current_password);
        newPassword = view.findViewById(R.id.input_new_password);
        confirmPassword = view.findViewById(R.id.input_confirm_new_password);
        cancel = view.findViewById(R.id.btn_cancel_reset_password);
        change = view.findViewById(R.id.btn_change_password);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mReference = mFirebaseDatabase.getReference().child("user/" + mFirebaseUser.getEmail().replace(".", ""));
        mReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users = dataSnapshot.getValue(Users.class);
                if (users != null){
                    email = users.getEmail();
                    password = users.getPassword();
                }else {
                    Toast.makeText(view.getContext(), getString(R.string.can_not_change_password), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view)
                .setTitle(getString(R.string.change_password));

        currentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 5){
                    change.setEnabled(true);
                }else {
                    change.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentPassword.getText().toString().equals(password)) {
                    currentPassword.requestFocus();
                    currentPassword.setError("Password incorrect!");
                } else if (newPassword.getText().toString().trim().length() < 6) {
                    newPassword.setError("Password need longer than 6 character");
                    newPassword.requestFocus();
                } else if (!newPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                    confirmPassword.requestFocus();
                    confirmPassword.setError("Confirm password incorrect!");
                } else {
                    if (!newPassword.getText().toString().isEmpty() && !currentPassword.getText().toString().isEmpty() && !confirmPassword.getText().toString().isEmpty()) {
                        AuthCredential credential = EmailAuthProvider.getCredential(mFirebaseUser.getEmail(), password);
                        mFirebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mFirebaseUser.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(view.getContext(), getString(R.string.update_password_successful), Toast.LENGTH_SHORT).show();
                                                users.setPassword(newPassword.getText().toString());
                                                mReference.setValue(users);
                                                dismiss();
                                            } else {
                                                Toast.makeText(view.getContext(), getString(R.string.error_password_not_update), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        transferSignal = (TransferSignal) context;
    }
}
