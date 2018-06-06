package com.example.leo.kotlin_1

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset

class SecondActivity : BaseActivity() {

    val jsonFile = "/sdcard/test.json"
    val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        var textView = findViewById<TextView>(R.id.textview_data)
        var name:String? = intent.getStringExtra("name")
        var age:Int? = intent.getIntExtra("age",0)
        if (name != null) {
            Log.d("SecondActivity:name",name)
            Log.d("SecondActivity:age", java.lang.String.valueOf(age))
        }
        //
        var buttonSave = findViewById<Button>(R.id.button_save)
        buttonSave.setOnClickListener(){
            val fileOutput = openFileOutput("data.txt",Activity.MODE_PRIVATE)
            val str = "'這是第一行"
            fileOutput.write(str.toByteArray(Charset.forName("utf-8")))
            fileOutput.close()
        }
        //
        var buttonLoad = findViewById<Button>(R.id.button_load)
        buttonLoad.setOnClickListener() {
            val fileInput = openFileInput("data.xml")
            var str = ""
            fileInput.reader().forEachLine { str = str + it+"\n" ; }
            textView.setText(str.toString())
            fileInput.close();
        }
        //
        val buttonSaveJson = findViewById<Button>(R.id.button_saveJson)
        buttonSaveJson.setOnClickListener() {
            if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
            else{
                saveJson()
            }
        }
        //
        val buttonLoadJson = findViewById<Button>(R.id.button_loadJson)
        buttonLoadJson.setOnClickListener() {
            val fis = FileInputStream(jsonFile)
            val products = readJsonStream(fis)
            if (products != null){
                var result = ""
                for (product in products) {
                    result += "id:" + product.id + " name:" + product.name + "\n" ;
                }
                textView.setText(result)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveJson()
                }
                else {

                }
            }
        }
    }
    //
    fun saveJson() {
        val fos = FileOutputStream(jsonFile)
        val products = arrayListOf<Product>()
        products.add(Product("0001","Leo"))
        products.add(Product("0002","Vicky"))
        writeJsonStream(fos, products) ;
        Toast.makeText(this,"Save Ok...",Toast.LENGTH_LONG).show()
    }
    //
    fun onClick_Close(view: View) {
        var intent = Intent()
        intent.putExtra("who","Leo")
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
