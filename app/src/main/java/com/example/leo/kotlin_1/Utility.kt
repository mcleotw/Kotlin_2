package com.example.leo.kotlin_1

import android.text.TextUtils
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object Utility {
    fun handleProvinceResponse(response:String):List<Province> {
        var provinces = mutableListOf<Province>()
        if (!TextUtils.isEmpty(response)) {
            try {
                val allprovinces = JSONArray(response)
                for (i in 0..allprovinces.length()-1) {
                    val provinceObject = allprovinces.getJSONObject(i)
                    val province = Province(provinceName = provinceObject.getString("name"),provinceCode = provinceObject.getString("id"))
                    provinces.add(provinces.size,province)
                }
            } catch (e:JSONException) {
                e.printStackTrace()
            }
        }
        return provinces
    }
    //
    fun handleCityResponse(response: String, provinceCode:String) :List<City> {
        var cities = mutableListOf<City>()
        if (!TextUtils.isEmpty(response)) {
            try {
                val allCities = JSONArray(response)
                for (i in 0..allCities.length()-1) {
                    val cityObject = allCities.getJSONObject(i)
                    val city = City(cityName = cityObject.getString("name"), cityCode = cityObject.getString("id"), provinceCode = provinceCode)
                    cities.add(city)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return cities
    }
    //
    fun handleCountyResponse(response: String, cityCode:String) : List<County> {
        var counties = mutableListOf<County>()
        if (!TextUtils.isEmpty(response)) {
            try {
                val allCounties = JSONArray(response)
                for (i in 0..allCounties.length()-1){
                    val countObject = allCounties.getJSONObject(i)
                    val county = County(countyName = countObject.getString("name"), countyCode = countObject.getString("id"),cityCode = cityCode)
                    counties.add(county)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return counties
    }
    //
    fun handleWeatherResponse(response: String) : Weather? {
        try {
            val jsonObject = JSONObject(response)
            val jsonArray = jsonObject.getJSONObject("HeWeather")
            val weatherContent = jsonArray.getJSONObject("0").toString()
            return Gson().fromJson(weatherContent,Weather::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}