package com.example.leo.kotlin_1

import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

object DataSupport {
    private fun getBytesByInputStream(content: InputStream) : ByteArray {
        var bytes : ByteArray? = null
        val bis = BufferedInputStream(content)
        val baos = ByteArrayOutputStream()
        val bos = BufferedOutputStream(baos)
        val buffer = ByteArray(1024*8)
        var length = 0
        try {
            while (true) {
                length = bis.read(buffer)
                if (length < 0 )
                    break
                bos.write(buffer, 0, length)
            }
            bos.flush()
            bytes = baos.toByteArray()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bos.close()
            } catch (e:IOException) {
                e.printStackTrace()
            }
            try {
                bis.close()
            } catch (e:IOException) {
                e.printStackTrace()
            }
        }
        return bytes !!
    }
    //
    private fun getServerContent(urlStr:String):String {
        var url = URL(urlStr)
        var conn = url.openConnection() as HttpURLConnection
        conn.setRequestMethod("GET")
        conn.setDoInput(true)
        conn.setUseCaches(false)
        val content = conn.getInputStream()
        var responeBoty = getBytesByInputStream(content)
        var str = kotlin.text.String(responeBoty, Charset.forName("utf-8"))
        return str
    }
    //
    fun getProvinces(provinces:(List<Province>)->Unit) {
        Thread() {
            var content = getServerContent("https://geekori.com/api/china")
            var provinces = Utility.handleProvinceResponse(content)
            provinces(provinces)
        }.start()
    }
    //
    fun getCities(provinceCode:String, cities:(List<City>)->Unit) {
        Thread() {
            var content = getServerContent("https://geekori.com/api/china/${provinceCode}")
            var cities = Utility.handleCityResponse(content,provinceCode)
            cities(cities)
        }.start()
    }
    //
    fun getCounties(provinceCode: String, cityCode:String, counties:(List<County>)->Unit) {
        Thread() {
            var content = getServerContent("https://geekori.com/api/china/${provinceCode}/${cityCode}")
            var counties  = Utility.handleCountyResponse(content,cityCode)
            counties(counties)
        }.start()
    }
}