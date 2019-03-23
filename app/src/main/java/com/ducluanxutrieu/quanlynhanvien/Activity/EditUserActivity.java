package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ducluanxutrieu.quanlynhanvien.Models.Users;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditUserActivity extends AppCompatActivity {
    TextInputEditText name, email, phone, position, password;
    LinearLayout inputPasswordLayout;
    Button add, cancel;
    String signal;
    boolean isAdmin;
    Users users;
    ImageView avatar;

    String mName = "";
    static String mEmail = "";
    static String mPhone = "";
    static String mPosition = "";
    static String mPassword = "";

    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    FirebaseFunctions mFunctions;

    private static final int IMAGE_REQUEST = 1;
    private static final String TAG = "kiemtra";
    private Uri uriImage;
    private StorageTask uploadTask;

    ProgressDialog pd;


    private String getStr(EditText editText){
        return editText.getText().toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        //create progressDialog
        pd = new ProgressDialog(EditUserActivity.this);
        mFunctions = FirebaseFunctions.getInstance();

        //get Data and signal from parent activity
        Intent intent = getIntent();
        users = (Users) intent.getSerializableExtra("user");
        signal = intent.getStringExtra("signal");
        isAdmin = intent.getBooleanExtra("isAdmin", false);

        mapping();

        if (signal.equals("updateUser")){
            avatar.setEnabled(true);
            mStorageReference = FirebaseStorage.getInstance().getReference().child("Users/" + users.getUid() + "/avatar");
            if (users.getName() != null) {
                name.setText(users.getName());
            }
            if (users.getEmail() != null) {
                email.setText(users.getEmail());
            }
            if (users.getPhone() != null) {
                phone.setText(users.getPhone());
            }
            if (users.getPosition() != null) {
                position.setText(users.getPosition());
            }
            if (users.getPassword() != null){
                password.setText(users.getPassword());
            }
            add.setText(getString(R.string.update));
            if (users.getAvatarUrl() != null) {
                Glide.with(this).load(users.getAvatarUrl()).apply(RequestOptions.circleCropTransform()).into(avatar);
            }
        }else {
            avatar.setEnabled(false);
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setTitle("Uploading...");
                pd.show();
                mName = getStr(name);
                //mName =( name.getText().toString() == null ? "" : name.getText().toString());
                mEmail = getStr(email);
                mPhone = getStr(phone);
                mPosition = getStr(position);
                mPassword = getStr(password);

                if (mPhone.startsWith("0")){
                    mPhone = "+84" + mPhone.substring(1);
                }

                HashMap<String, Object> map;
                if (putToHashMap() == null){
                    return;
                }else {
                    map = putToHashMap();
                }

                modifyUser(map, signal).addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e != null) {
                                Log.i(TAG, e.toString());
                            }
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                //Object details = ffe.getDetails();
                                //Log.i(TAG, details.toString());
                                Log.i(TAG, code.toString());
                            }
                            // [START_EXCLUDE]
                            Log.w(TAG, "addNumbers:onFailure", e);
                            showSnackbar("An error occurred.");
                            // [END_EXCLUDE]
                            // ...
                        }else {
                            // [START_EXCLUDE]
                            String result = task.getResult();

                            // [END_EXCLUDE]
                            Toast.makeText(EditUserActivity.this, result, Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                if (signal.equals("updateUser")) {
                    uploadImage();
                }
                pd.dismiss();
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });
    }

    private HashMap<String, Object> putToHashMap() {
        HashMap<String, Object> map = new HashMap<>();
        if (mName == null || mName.isEmpty()){
            return null;
        }else {
            map.put("name", mName);
        }

        if (mEmail == null || mEmail.isEmpty()){
            return null;
        }else {
            map.put("email", mEmail);
        }

        map.put("phone", mPhone);
        map.put("position", mPosition);

        if (mPassword == null || mPassword.isEmpty()){
            return null;
        }else {
            map.put("password", mPassword);
        }
        if (users != null){
            map.put("uid", users.getUid());
            map.put("avatarUrl", users.getAvatarUrl());
        }
        return map;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

/*    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }*/

    private void uploadImage(){
        if (uriImage!= null){
            //final StorageReference storageReference = mStorageReference.child(System.currentTimeMillis() + "." + getFileExtension(uriImage));

            uploadTask = mStorageReference.putFile(uriImage);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return mStorageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        if(downloadUri != null) {
                            String mUri = downloadUri.toString();
                            mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users/" + users.getUid());
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("avatarUrl", mUri);
                            mDatabaseReference.updateChildren(map);
                        }else {
                            Toast.makeText(EditUserActivity.this, getString(R.string.can_not_upload_image), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(EditUserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i(TAG, e.getMessage());
                }
            });
        }else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data!= null && data.getData() != null){
            uriImage = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
            }else {
                Glide.with(this).load(uriImage).apply(RequestOptions.circleCropTransform()).into(avatar);
            }
        }
    }

    public void mapping() {
        name = findViewById(R.id.edit_text_name);
        email = findViewById(R.id.edit_text_email);
        phone = findViewById(R.id.edit_text_phone);
        position = findViewById(R.id.edit_text_position);
        add = findViewById(R.id.btn_add);
        cancel = findViewById(R.id.btn_cancel);
        avatar = findViewById(R.id.user_profile_photo_edit);
        password = findViewById(R.id.edit_text_password);
        inputPasswordLayout = findViewById(R.id.edit_text_password_layout);
    }

    private Task<String> modifyUser(HashMap<String, Object> data, String signal) {
        // Create the arguments to the callable function.
        Log.i(TAG, signal);
        return mFunctions
                .getHttpsCallable(signal)
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

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT).show();
    }
}