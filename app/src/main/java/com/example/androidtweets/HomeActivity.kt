package com.example.androidtweets

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun onToLogin(view: View){
        var i = Intent(this,LoginActivity::class.java)
        startActivity(i)
    }

    fun onToSign(view: View){
        var i = Intent(this,SignUpActivity::class.java)
        startActivity(i)
    }
}
