package com.ducluanxutrieu.quanlynhanvien.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ducluanxutrieu.quanlynhanvien.R
import com.ducluanxutrieu.quanlynhanvien.activity.MainActivity.Companion.mCatView
import com.ducluanxutrieu.quanlynhanvien.models.Staff
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

class EditUserActivity : AppCompatActivity() {
    internal lateinit var name: TextInputEditText
    internal lateinit var email: TextInputEditText
    private lateinit var phone: TextInputEditText
    internal lateinit var position: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var inputPasswordLayout: LinearLayout
    internal lateinit var add: Button
    internal lateinit var cancel: Button
    private var signal: String? = null
    private var isAdmin: Boolean = false
    private var staff: Staff? = null
    private lateinit var avatar: ImageView

    private var mName: String? = ""

    private lateinit var mStorageReference: StorageReference
    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mFunctions: FirebaseFunctions
    private var uriImage: Uri? = null
    private var uploadTask: UploadTask? = null

    private fun getStr(editText: EditText): String {
        return editText.text.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        mFunctions = FirebaseFunctions.getInstance()

        //get Data and signal from parent activity
        val intent = intent
        staff = intent.getSerializableExtra("user") as Staff
        signal = intent.getStringExtra("signal")
        isAdmin = intent.getBooleanExtra("isAdmin", false)

        binding()

        updateUser()

        cancel.setOnClickListener { finish() }

        avatar.setOnClickListener { openImage() }
    }

    private fun updateUser() {
        if (signal == "updateUser") {
            avatar.isEnabled = true
            mStorageReference = FirebaseStorage.getInstance().reference.child("Staff/" + staff!!.uid + "/avatar")
            if (staff!!.name != null) {
                name.setText(staff!!.name)
            }
            if (staff!!.email != null) {
                email.setText(staff!!.email)
            }
            if (staff!!.phone != null) {
                phone.setText(staff!!.phone)
            }
            if (staff!!.position != null) {
                position.setText(staff!!.position)
            }
            if (staff!!.password != null) {
                password.setText(staff!!.password)
            }
            add.text = getString(R.string.update)
            if (staff!!.avatarUrl != null) {
                Glide.with(this).load(staff!!.avatarUrl).apply(RequestOptions.circleCropTransform()).into(avatar)
            }
        } else {
            avatar.isEnabled = false
        }
    }

    fun buttonAddClicked(view: View) {
        mCatView.setText("Uploading...")
        mName = getStr(name)
        //mName =( name.getText().toString() == null ? "" : name.getText().toString());
        mEmail = getStr(email)
        mPhone = getStr(phone)
        mPosition = getStr(position)
        mPassword = getStr(password)

        if (mPhone.startsWith("0")) {
            mPhone = "+84" + mPhone.substring(1)
        }

        val map: HashMap<String, Any>?
        if (putToHashMap() == null) {
            return
        } else {
            map = putToHashMap()
        }

        modifyUser(map!!, signal)

        if (signal == "updateUser") {
            uploadImage()
        }
        mCatView.dismiss()
        finish()
    }

    private fun putToHashMap(): HashMap<String, Any>? {
        val map = HashMap<String, Any>()
        if (mName == null || mName!!.isEmpty()) {
            return null
        } else {
            map["name"] = mName!!
        }

        if (mEmail == null || mEmail!!.isEmpty()) {
            return null
        } else {
            map["email"] = mEmail!!
        }

        map["phone"] = mPhone
        map["position"] = mPosition

        if (mPassword == null || mPassword!!.isEmpty()) {
            return null
        } else {
            map["password"] = mPassword!!
        }
        if (staff != null) {
            map["uidFriend"] = staff!!.uid!!
            map["avatarUrl"] = staff!!.avatarUrl!!
        } else {
            map["avatarUrl"] = "https://firebasestorage.googleapis.com/v0/b/quan-ly-nhan-vien.appspot.com/o/585e4bf3cb11b227491c339a.png?alt=media&token=01b70fd2-2eb0-4afb-a7e0-864be1a874d9"
        }
        return map
    }

    private fun openImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    private fun uploadImage() {
        if (uriImage != null) {

            uploadTask = mStorageReference.putFile(uriImage!!)
            uploadTask!!.continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                mStorageReference.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    if (downloadUri != null) {
                        val mUri = downloadUri.toString()
                        mDatabaseReference = FirebaseDatabase.getInstance().reference.child("users/" + staff!!.uid!!)
                        val map = HashMap<String, Any>()
                        map["avatarUrl"] = mUri
                        mDatabaseReference.updateChildren(map)
                    } else {
                        Toast.makeText(this@EditUserActivity, getString(R.string.can_not_upload_image), Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this@EditUserActivity, e.message, Toast.LENGTH_SHORT).show()
                Log.i(TAG, e.message!!)
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            uriImage = data.data
            if (uploadTask != null && uploadTask!!.isInProgress) {
                Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else {
                Glide.with(this).load(uriImage).apply(RequestOptions.circleCropTransform()).into(avatar)
            }
        }
    }

    private fun binding() {
        name = findViewById(R.id.edit_text_name)
        email = findViewById(R.id.edit_text_email)
        phone = findViewById(R.id.edit_text_phone)
        position = findViewById(R.id.edit_text_position)
        add = findViewById(R.id.btn_add)
        cancel = findViewById(R.id.btn_cancel)
        avatar = findViewById(R.id.user_profile_photo_edit)
        password = findViewById(R.id.edit_text_password)
        inputPasswordLayout = findViewById(R.id.edit_text_password_layout)
    }

    private fun modifyUser(data: HashMap<String, Any>, signal: String?) {
        // Create the arguments to the callable function.
        Log.i(TAG, signal!!)
        Log.i(TAG, data.toString())
        mFunctions.getHttpsCallable(signal)
                .call(data)
                .continueWith { task ->
                    // This continuation runs on either success or failure, but if the task
                    // has failed then getResult() will throw an Exception which will be
                    // propagated down.
                    task.result!!.data as String?
                }
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        val e = task.exception
                        if (e != null) {
                            Log.i(TAG, e.toString())
                        }
                        if (e is FirebaseFunctionsException) {
                            val ffe = e as FirebaseFunctionsException?
                            val code = ffe!!.code
                            Log.i(TAG, code.toString())
                        }
                        Log.w(TAG, "addNumbers:onFailure", e)
                        showSnackBar()
                    } else {
                        val result = task.result
                        Toast.makeText(this@EditUserActivity, result, Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e -> Toast.makeText(this@EditUserActivity, e.message, Toast.LENGTH_SHORT).show() }
    }

    private fun showSnackBar() {
        Snackbar.make(findViewById(android.R.id.content), "An error occurred.", Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        internal var mEmail: String? = ""
        internal var mPhone = ""
        internal var mPosition = ""
        internal var mPassword: String? = ""

        private const val IMAGE_REQUEST = 1
        private const val TAG = "kiemtra"
    }
}