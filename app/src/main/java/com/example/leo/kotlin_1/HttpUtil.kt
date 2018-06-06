package com.example.leo.kotlin_1

import okhttp3.OkHttpClient
import okhttp3.Request

object HttpUtil {
    fun sendOkHttpRequest(address:String, callback:okhttp3.Callback) {
        val client = OkHttpClient()
        val request = Request.Builder().url(address).build()
        client.newCall(request).equals(callback)
    }
}