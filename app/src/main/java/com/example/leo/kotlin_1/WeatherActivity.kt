package com.example.leo.kotlin_1

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.telecom.Call
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.*
import okhttp3.Response
import java.io.IOException
import javax.security.auth.callback.Callback

class WeatherActivity : AppCompatActivity() {
    var drawerLayout :DrawerLayout? = null
    var swipeRefresh : SwipeRefreshLayout? = null
    private var weatherLayout : ScrollView?=null
    private var navButton : Button? = null
    private var titleCity : TextView? = null
    private var titleUpdateTime: TextView? = null
    private var degreeText : TextView? = null
    private var weatherInfoText : TextView? = null
    private var forecastLayout : LinearLayout? = null
    private var aqiText : TextView? = null
    private var pm25Text : TextView? = null
    private var comfortText : TextView? = null
    private var carWashText : TextView? = null
    private var sportText : TextView? = null
    private var bingPicImg : ImageView? = null
    //
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            window.statusBarColor = Color.TRANSPARENT
        }
        setContentView(R.layout.activity_weather)
        //
        navButton = findViewById(R.id.nav_button) as Button
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val weatherString = prefs.getString("weather",null)
        val weatherId : String?
        if (weatherString != null) {
            val weather = Utility.handleWeatherResponse(weatherString)
            weatherId = weather?.basic?.weatherId
            showWeatherInfo(weather)
        } else {
            weatherId =intent.getStringExtra("weather_id")
            weatherLayout!!.visibility = View.INVISIBLE
            requestWeather(weatherId)
        }
        //
        swipeRefresh!!.setOnRefreshListener { requestWeather(weatherId) }
        navButton!!.setOnClickListener { drawerLayout?.openDrawer(GravityCompat.START) }
        val bingPic = prefs.getString("bing_pic", null)
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg)
        } else {
            loadBingPic()
        }
    }
    //
    fun requestWeather(id:String) {
        val weatherUrl = "https://geekori/com/api/weather?id=${id}"
        HttpUtil.sendOkHttpRequest(weatherUrl, object:Callback{
            @Throws(IOException::class)
            override fun onResponse(call:Call, response: Response) {
                val responseText = response.body()!!.string()
                val weather = Utility.handleWeatherResponse(responseText)
                runOnUiThread {
                    if (weather != null && "ok" == weather!!.Status) {
                        val editor = PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity).edit()
                        editor.putString("weather", responseText)
                        editor.apply()
                        showWeatherInfo(weather)
                    } else {
                        Toast.makeText(this@WeatherActivity, "取得天氣資訊失敗",Toast.LENGTH_LONG).show()
                    }
                    swipeRefresh?.isRefreshing = false
                }
            }
            //
            override fun onFailure(call:Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@WeatherActivity, "取得天氣資訊失敗",Toast.LENGTH_LONG).show()
                    swipeRefresh?.isRefreshing = false
                }
            }
        })
        loadBingPic()
    }
    //
    private fun loadBingPic() {
        val requestBingPic = "https://geekori.com/api/background/pic"
        HttpUtil.sendOkHttpRequest(requestBingPic, object : Callback {
            @Throws(IOException::class)
            override fun onResponse(call:Call, response: Response) {
                val bingPic = response.body()!!.string()
                val editor = PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity).edit()
                editor.putString("bing_pic",bingPic)
                editor.apply()
                runOnUiThread { Glide.with(this@WeatherActivity).load(bingPic).into(bingPicImg) }
            }
            //
            override fun onFailure(call:Call, e: IOException) {
                e.printStackTrace()
            }
        })
    }
    //
    private fun showWeatherInfo(weather: Weather??) {
        val cityName = weather?.basic?.cityName
        val updateTime = weather?.basic?.updateTime!!.spilt(" ")[1]
        val degree = weather?.now?.temperature+"C"
        val weatherInfo = weather?.now?.more?.info
        titleCity!!.setText(cityName)
        titleUpdateTime!!.setText(updateTime)
        degreeText!!.setText(degree)
        weatherInfoText!!.setText(weatherInfo)
        forecastLayout!!.removeAllViews()
        for (forcast in weather.forcastList) {
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
        }
    }
}
