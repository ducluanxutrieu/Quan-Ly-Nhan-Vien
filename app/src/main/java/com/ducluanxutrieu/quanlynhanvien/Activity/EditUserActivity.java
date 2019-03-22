package com.ducluanxutrieu.quanlynhanvien.Activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ducluanxutrieu.quanlynhanvien.Models.Users;
import com.ducluanxutrieu.quanlynhanvien.MyTask;
import com.ducluanxutrieu.quanlynhanvien.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditUserActivity extends AppCompatActivity {
    TextInputEditText name, email, phone, position, password;
    Button add, cancel;
    String signal;
    boolean isAdmin;
    Users users;
    ImageView avatar;

    static String finalName;
    static String finalEmail;
    static String finalPhone;
    static String finalPosition;
    static String finalPassword;

    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    FirebaseFunctions mFunctions;

    private static final int IMAGE_REQUEST = 1;
    private Uri uriImage;
    private StorageTask uploadTask;
    private static final String TAG = "kiemtra";
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        pd = new ProgressDialog(EditUserActivity.this);
        mFunctions = FirebaseFunctions.getInstance();
        Intent intent = getIntent();
        users = (Users) intent.getSerializableExtra("user");
        signal = intent.getStringExtra("signal");

        isAdmin = intent.getBooleanExtra("isAdmin", false);

        mapping();
        if (signal.equals("update")) {
            //mStorageReference = FirebaseStorage.getInstance().getReference("Users").child(users.getUid()).child("avatar");
        }

        if (signal.equals("update")){
            if (!isAdmin){
                password.setVisibility(View.GONE);
            }
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
            add.setText(getString(R.string.update));
            if (users.getAvatarUrl() != null) {
                Glide.with(this).load(users.getAvatarUrl()).apply(RequestOptions.circleCropTransform()).into(avatar);
            }
        }
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setTitle("Uploading...");
                pd.show();
                finalName = name.getText().toString();
                finalEmail = email.getText().toString();
                finalPhone = phone.getText().toString();
                finalPosition = position.getText().toString();
                //updateAccount();

                if (finalPhone.startsWith("0")){
                    finalPhone = "+84" + finalPhone.substring(1);
                }

                Map<String, Object> map = new HashMap<>();
                map.put("name", finalName);
                map.put("email", finalEmail);
                map.put("phone", finalPhone);
                map.put("position", finalPosition);
                if (isAdmin){
                    finalPassword = password.getText().toString();
                    map.put("password", password);
                }

                modifyUser(map, signal + "User").addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            Log.i(TAG, e.toString());
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                                //Log.i(TAG, details.toString());
                                Log.i(TAG, code.toString());
                            }

                            // ...
                        }
                        else Toast.makeText(EditUserActivity.this, "Success", Toast.LENGTH_SHORT).show();

                        // ...
                    }
                });

                if (uriImage == null){
                    uriImage = getUriToDrawable(EditUserActivity.this, R.drawable.avatar);
                }
                //uploadImage();

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
                            mDatabaseReference = FirebaseDatabase.getInstance().getReference("user").child(users.getUid());
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
    }

    private void updateAccount(){
        if (finalPhone.startsWith("0")){
            finalPhone = "+84" + finalPhone.substring(1);
        }

        final String BASE_URL = "https://quan-ly-nhan-vien.firebaseapp.com/" + signal;
        String TAG = ".EditUserActivity";
        Log.i(TAG, BASE_URL);
        //final String BASE_URL = "http://10.0.3.2:5000/create";
        Map<String, String> map = new HashMap<>();
        map.put("name", finalName);
        map.put("email", finalEmail);
        map.put("phone", finalPhone);
        if (signal.equals("update")) {
            map.put("uid", users.getUid());
        }
        JSONObject jsonObject = new JSONObject(map);
        String query = jsonObject.toString();
        Log.i("QUERY", query);
        new MyTask(EditUserActivity.this).execute(BASE_URL, query);
    }
    public static void updateUserToDatabase(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            Users users = new Users(finalName, finalEmail, finalPhone, finalPosition, user.getUid(),null, false);
            mReference.child("user/" + user.getUid()).setValue(users);
        }
    }

    public static void addAccountToDatabase(String s){
        String uid = s.substring(7);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            Users users = new Users(finalName, finalEmail, finalPhone, finalPosition, uid, null, false);
            mReference.child("user/" + uid).setValue(users);
        }
    }

    public static Uri getUriToDrawable(@NonNull Context context,
                                       @AnyRes int drawableId) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId) );
    }

    private Task<String> modifyUser(Map<String, Object> data, String signal) {
        // Create the arguments to the callable function.

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
}