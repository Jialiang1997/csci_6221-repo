package com.example.androidtweets

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


class SignUpActivity : AppCompatActivity() {

    private val TAG: String = "SignUpActivity"

    private lateinit var editTextUserName: EditText
    private lateinit var editTextPasswd: EditText
    private lateinit var editTextPasswdConfirm: EditText

    private lateinit var mAuth: FirebaseAuth

    private lateinit var loadingDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
        editTextUserName = findViewById(R.id.ed_username)
        editTextPasswd = findViewById(R.id.ed_passwd)
        editTextPasswdConfirm = findViewById(R.id.ed_passwd_again)
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = AlertDialog.Builder(this).create()

        loadingDialog.setMessage("loading please wait")



    }



    fun onSign(view: View) {
        var email = editTextUserName.text.toString()
        var paswd = editTextPasswd.text.toString()
        var paswdconfirm = editTextPasswdConfirm.text.toString()

        if (TextUtils.isEmpty(email)) {
            return
        }

        if (TextUtils.isEmpty(paswd)) {
            return
        }
        if (TextUtils.isEmpty(paswdconfirm)) {
            return
        }

        if (!TextUtils.equals(paswd, paswdconfirm)) {
            return
        }

        signup(email, paswd)
    }



      private fun signup(email: String, password: String) {

        loadingDialog.show()
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                loadingDialog.dismiss()
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = mAuth.currentUser
                    showToast("Sign Up Sucessess ")
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
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
