package com.example.leo.kotlin_1

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.*
import java.io.File
import java.io.FileOutputStream

class MyActivity : BaseActivity()
{
    val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.my_activiey)
        var sharedPreferences =PreferenceManager.getDefaultSharedPreferences(this)
        var usernameEditText = findViewById<EditText>(R.id.edittext_username)
        var passwordEditText = findViewById<EditText>(R.id.edittext_password)
        var loginButton = findViewById<Button>(R.id.button_login)
        var button_Restore = findViewById<Button>(R.id.button_Restore)
        var saveCheckBox = findViewById<CheckBox>(R.id.checkbox_save)
        //
        val buttonSaveImage = findViewById<Button>(R.id.button_saveSD)
        buttonSaveImage.setOnClickListener(){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
            else{
                saveImage()
            }
        }
        //
        val buttonReadSD = findViewById<Button>(R.id.button_readSD)
        buttonReadSD.setOnClickListener() {
            val filename = "/sdcard/11.gif" ;
            if (!File(filename).exists()) {
                Toast.makeText(this,"沒有圖片",Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val bitmap = BitmapFactory.decodeFile(filename)
            val imageview = findViewById<ImageView>(R.id.imageview)
            imageview.setImageBitmap(bitmap)
        }
        //
        loginButton.setOnClickListener() {
            var username = usernameEditText.text.toString()
            var password = passwordEditText.text.toString()
            if (username == "root" && password == "123456") {
                if (saveCheckBox.isChecked) {
                    var editor = sharedPreferences.edit()
                    editor.putString("username",username)
                    editor.putString("password",password)
                    editor.commit()
                    Toast.makeText(this,"登入成功-存回",Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(this,"登入成功",Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this,"帳號或密不對",Toast.LENGTH_LONG).show()
                usernameEditText.setText("")
                passwordEditText.setText("")
            }
        }

        button_Restore.setOnClickListener() {
            var username = sharedPreferences.getString("username","")
            var password = sharedPreferences.getString("password","")
            usernameEditText.setText(username)
            passwordEditText.setText(password)
        }
    }

    private fun saveImage() {
        val fos = FileOutputStream("/sdcard/11.gif")
        val inputStream = resources.assets.open("11.gif")
        val b = byteArrayOf(100)
        var count = 0
        while(true){
            count = inputStream.read(b)
            if (count < 0) {
                break
            }
            fos.write(b,0,count)
        }
        fos.close()
        inputStream.close()
        Toast.makeText(this,"儲存成功",Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage()
                }
                else{

                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            1-> {
                if (resultCode == Activity.RESULT_OK) {
                    var returnData = data?.getStringExtra("who")
                    Toast.makeText(this, "who:"+returnData, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun onClick_Exit(view: View) {
        finish()
    }

    fun onClick_Web(view: View) {
        var intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse("http://www.mcleo.idv.tw/doc/weather.htm"))
        startActivity(intent)
    }

    fun onClick_WebdActivity(view: View) {
        var intent = Intent(this, WebActivity::class.java)
        startActivity(intent)
    }

    fun onClick_WebActivity(view: View) {
        var intent = Intent()
        startActivityForResult(intent,1)
    }
    fun onClick_ShowSecondActivity(view: View) {
        var intent = Intent("com.example.leo.active.SECOND_ACTIVITY")
        intent.addCategory("com.example.leo.category.SECOND_ACTIVITY")
        intent.putExtra("name","Leo")
        intent.putExtra("age",55)
        startActivityForResult(intent,1)
    }
}