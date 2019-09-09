package com.ducluanxutrieu.quanlynhanvien.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.app.LoaderManager.LoaderCallbacks

import android.content.CursorLoader
import android.content.Loader
import android.database.Cursor
import android.net.Uri

import android.os.Bundle
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.ducluanxutrieu.quanlynhanvien.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), LoaderCallbacks<Cursor> {
    private var mEmailView: EditText? = null
    private var mPasswordView: EditText? = null
    private var mProgressView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        autoLogin()
        // Set up the login form.

        mEmailView = findViewById(R.id.email)
        mPasswordView = findViewById(R.id.password)
        mPasswordView!!.setOnEditorActionListener { _, _, _ -> false }

        val mEmailSignInButton = findViewById<Button>(R.id.email_sign_in_button)
        mEmailSignInButton.setOnClickListener { attemptLogin() }

        mProgressView = findViewById(R.id.login_progress)
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptLogin() {
        // Reset errors.
        mEmailView!!.error = null
        mPasswordView!!.error = null

        // Store values at the time of the login attempt.
        val email = mEmailView!!.text.toString()
        val password = mPasswordView!!.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView!!.error = getString(R.string.error_invalid_password)
            focusView = mPasswordView
            cancel = true
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView!!.error = getString(R.string.error_field_required)
            focusView = mEmailView
            cancel = true
        } else if (!isEmailValid(email)) {
            mEmailView!!.error = getString(R.string.error_invalid_email)
            focusView = mEmailView
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
            loginTest(email, password)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        //TODO: Replace this with your own logic
        return email.contains("@")
    }

    private fun isPasswordValid(password: String): Boolean {
        //TODO: Replace this with your own logic
        return password.length > 4
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private fun showProgress(show: Boolean) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        val shortAnimTime = resources.getInteger(android.R.integer.config_shortAnimTime)

        mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
        mProgressView!!.animate().setDuration(shortAnimTime.toLong()).alpha(
                (if (show) 1 else 0).toFloat()).setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mProgressView!!.visibility = if (show) View.VISIBLE else View.GONE
            }
        })
    }

    override fun onCreateLoader(i: Int, bundle: Bundle): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(cursorLoader: Loader<Cursor>, cursor: Cursor) {
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            cursor.moveToNext()
        }
    }

    override fun onLoaderReset(cursorLoader: Loader<Cursor>) {

    }


    private interface ProfileQuery {
        companion object {
            val PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY)
        }
    }

    private fun autoLogin() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            Log.i("USERLOGIN", "STARTAUTO")
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    private fun loginTest(mEmail: String, mPassword: String) {
        val mAuth = FirebaseAuth.getInstance()
        mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(this@LoginActivity) { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, getString(R.string.login_successful), Toast.LENGTH_LONG).show()
                loginResult(true, mEmail, mPassword)
            } else {
                Toast.makeText(applicationContext, getString(R.string.there_are_a_problem_signing_in), Toast.LENGTH_LONG).show()
                loginResult(false, mEmail, mPassword)
            }
        }
    }

    private fun loginResult(success: Boolean, mEmail: String, mPassword: String) {
        showProgress(false)
        if (success) {
            val sharedPreferences = getSharedPreferences("com.ducluanxutrieu.quanlynhanvien", 0)
            sharedPreferences.edit().putString("email", mEmail)
                    .putString("password", mPassword)
                    .apply()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            Log.i("USERLOGIN", "post")
            startActivity(intent)
            finish()
        } else {
            mPasswordView!!.error = getString(R.string.error_incorrect_password)
            mPasswordView!!.requestFocus()
        }
    }
}

