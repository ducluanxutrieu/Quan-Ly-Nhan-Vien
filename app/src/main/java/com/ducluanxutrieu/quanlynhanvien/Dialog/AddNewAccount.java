package com.ducluanxutrieu.quanlynhanvien.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.ducluanxutrieu.quanlynhanvien.Interface.TransferData;
import com.ducluanxutrieu.quanlynhanvien.R;

public class AddNewAccount extends DialogFragment {
    public AddNewAccount(){}
    EditText name, email, password, phone, position;

    TransferData transferData;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_new_account_dialog, null);

        mapping(view);
        builder.setView(view)
                .setIcon(R.drawable.add_icon)
                .setTitle(getString(R.string.add_new_member))
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        transferData.onTransferDialog(
                                name.getText().toString(),
                                email.getText().toString(),
                                password.getText().toString(),
                                phone.getText().toString(),
                                position.getText().toString());
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null);

        return builder.create();
    }

    public void mapping(View view){
        name = view.findViewById(R.id.edit_text_name_dialog);
        email = view.findViewById(R.id.edit_text_email_dialog);
        password = view.findViewById(R.id.edit_text_password_dialog);
        phone = view.findViewById(R.id.edit_text_phone_dialog);
        position = view.findViewById(R.id.edit_text_position_dialog);
        }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        transferData = (TransferData) getContext();
    }
}
