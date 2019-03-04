package com.ducluanxutrieu.quanlynhanvien.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ducluanxutrieu.quanlynhanvien.Interface.TransferTask;
import com.ducluanxutrieu.quanlynhanvien.R;

public class AddNewTask extends DialogFragment {
    private EditText inputTitleTask, inputContentTask;
    private Button btnCancel, btnAdd;
    private TransferTask transferTask;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.add_new_task_dialog, null, false);

        mapping(view);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle(getString(R.string.add_new_task));
        inputTitleTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (inputTitleTask.getText().toString().trim().length() > 0 && inputContentTask.getText().toString().trim().length() > 0){
                    btnAdd.setEnabled(true);
                }else {
                    btnAdd.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputContentTask.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (inputTitleTask.getText().toString().trim().length() > 0 && inputContentTask.getText().toString().trim().length() > 0){
                    btnAdd.setEnabled(true);
                }else {
                    btnAdd.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = inputTitleTask.getText().toString();
                String content = inputContentTask.getText().toString();
                transferTask.onTransferTask(title, content);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder.create();
    }

    private void mapping(View view) {
        inputTitleTask = view.findViewById(R.id.input_title_task_edit);
        inputContentTask = view.findViewById(R.id.input_content_task_edit_text);
        btnCancel = view.findViewById(R.id.btn_cancel_add_task);
        btnAdd = view.findViewById(R.id.btn_add_task);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        transferTask = (TransferTask) context;
    }
}
