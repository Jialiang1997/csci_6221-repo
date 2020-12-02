package com.example.androidtweets

import android.content.Intent
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import java.util.ArrayList

class SearchActivity : AppCompatActivity() {

    private lateinit var geoCoder: Geocoder

    private lateinit var loadingDialog: AlertDialog

    lateinit var destination: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        destination = findViewById(R.id.ed_dest)
        geoCoder = Geocoder(this);

        loadingDialog = AlertDialog.Builder(this).create()

        loadingDialog.setMessage("loading please wait")

    }



    fun onSearch(view: View) {

        var input = destination.editableText.toString()

        if (TextUtils.isEmpty(input)){

            return
        }

        loadingDialog.show();


        val addresslist = geoCoder.getFromLocationName(input, 10)
        val addressStringList : ArrayList<String> = ArrayList<String>()
        addresslist.forEach { addr ->
            val name = addr.getAddressLine(0)
            Log.d("geoCoder", "name==" + name)
            addressStringList.add(name)
        }

        loadingDialog.dismiss()

        // select_dialog_singlechoice is a pre-defined XML layout for a RadioButton row
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice)
        arrayAdapter.addAll(addressStringList)

        AlertDialog.Builder(this)
            .setTitle("Possible Matches")
            .setAdapter(arrayAdapter) { dialog, which ->

                Toast.makeText(this, "You picked: ${addressStringList[which]}", Toast.LENGTH_SHORT).show()

                gotoMapActivity(addresslist[which].latitude,
                    addresslist[which].longitude, addressStringList[which]
                )

            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }


    private fun gotoMapActivity(lat:Double,lon:Double,addres:String){
        val intent: Intent = Intent(this, MapActivity::class.java)
        intent.putExtra("LAT",lat)
        intent.putExtra("LON",lon)
        intent.putExtra("ADDR",addres)
        startActivity(intent)

    }
}
