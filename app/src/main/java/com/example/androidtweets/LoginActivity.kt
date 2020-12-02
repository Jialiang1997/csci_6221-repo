package com.example.androidtweets

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity() {

    private val TAG: String = "LoginActivity"

    private lateinit var editTextUserName: EditText
    private lateinit var editTextPasswd: EditText

    private lateinit var mAuth: FirebaseAuth

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        editTextUserName = findViewById(R.id.ed_username)
        editTextPasswd = findViewById(R.id.ed_passwd)
        mAuth = FirebaseAuth.getInstance();

        loadingDialog = AlertDialog.Builder(this).create()

        loadingDialog.setMessage("loading please wait")

    }


    fun onLogin(view: View) {

        var email = editTextUserName.text.toString()
        var paswd = editTextPasswd.text.toString()

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(paswd)) {

            login(email, paswd)
        }


    }


    fun updateUI(user: FirebaseUser?) {

        showToast("Login Suceess")
        var intent = Intent(this, SearchActivity::class.java)
        startActivity(intent)
    }


    private fun login(email: String, password: String) {


        loadingDialog.show()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                loadingDialog.dismiss()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // ...
            }

    }


    fun showToast(msg: String) {

        Toast.makeText(
            this, msg,
            Toast.LENGTH_SHORT
        ).show()

    }

}
