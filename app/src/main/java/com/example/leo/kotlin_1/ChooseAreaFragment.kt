package com.example.leo.kotlin_1

import android.app.Fragment
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class ChooseAreaFragment : Fragment() {
    private var progressDialog:ProgressDialog? = null
    private var titleText : TextView? = null
    private var backButton : Button? = null
    private var listView : ListView? = null
    // 用於為ListView提供資料來源的Adapter物件, 資料來源為陣列
    private var adapter: ArrayAdapter<String>? = null
    private var handler = MyHander()
    // ListView的資料來源
    private var dataList = ArrayList<String>()
    // 省列表
    private var provinceList : List<Province>? = null
    //市列表
    private var cityList : List<City>? = null
    //縣區列表
    private var countyList : List<County>? = null
    //目前選取的省
    private var selectProvince : Province? = null
    //目前選取的市
    private var selectCity : City? = null
    //目前選取的等級
    private var currentLevel : Int = 0
    //等級伴隨物件
    companion object {
        val LEVEL_PROVINCE = 0
        val LEVEL_CITY = 1
        val LEVEL_COUNTY = 2
    }
    // Fragment的初始化方法, 類似Activity的OnCreat方法
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.activity_choose_area_fragment,container,false)
        titleText = view.findViewById(R.id.title_text) as TextView
        backButton = view.findViewById(R.id.back_button) as Button
        listView = view.findViewById(R.id.list_view) as ListView
        adapter = ArrayAdapter(context,android.R.layout.simple_list_item_1,dataList)
        // 將Adaptder與ListView元件綁定, 這樣就可以在ListView元件中顯示資料
        listView!!.adapter = adapter
        return view
    }
    // 當包含Fragment 的 Activity被建立時呼叫
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 設定ListView物件的Item點擊事件
        listView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            // 選擇省
            if (currentLevel == LEVEL_PROVINCE) {
                selectProvince = provinceList!![position]
                queryCities()
            //選擇市
            } else if (currentLevel == LEVEL_CITY) {
                selectCity = cityList!![position]
                queryCounties()
            //選擇縣區
            } else if (currentLevel == LEVEL_COUNTY) {
                val countyName = countyList!![position].countyName
                //選擇縣區後, 如果Fragment處於開啟狀態 則隱藏Fragment, 然後顯示目前縣區
                //的天氣情況
                if (activity is MainActivity) {
                    val intent = Intent(activity, WeatherActivity::class.java)
                    intent.putExtra("name",countyName)
                    startActivity(intent)
                    activity.finish()
                } else if (activity is WeatherActivity) {
                    val activity = activity as WeatherActivity
                    activity.drawerLayout?.closeDrawers()
                    activity.swipeRefresh?.setRefreshing(true)
                    activity.requestWeather(countyName)
                }
            }
        }
        // 回復按鈕的點擊事件
        backButton!!.setOnClickListener(){
            //目前處於縣區級, 回復到市級
            if (currentLevel == LEVEL_COUNTY) {
                queryCities()
            //目前處於市級, 回復到省級
            } else if (currentLevel == LEVEL_CITY) {
                queryProvinces()
            }
        }
        //預設為省級
        queryProvinces()
    }
    //用於更新ListView元件
    class MyHander:Handler() {
        override fun handleMessage(msg: Message?) {
            var activity = msg?.obj as ChooseAreaFragment
            while (msg?.arg1) {
                // 在ListView元件中顯示省清單
                ChooseAreaFragment.LEVEL_PROVINCE -> {
                    if (activity.provinceList!!.size > 0) {
                        activity.dataList.clear()
                        for (province in activity.provinceList!!) {
                            activity.dataList.add(province.provinceName)
                        }
                        activity.adapter!!.notifyDataSetChanged()
                        activity.listView!!.setSelection(0)
                        activity.currentLevel = LEVEL_PROVINCE
                    }
                }
                //在ListView元件顯示市清單
                ChooseAreaFragment.LEVEL_CITY -> {
                    if (activity.cityList!!.size > 0) {
                        activity.dataList.clear()
                        for (city in activity.cityList!!) {
                            activity.dataList.add(city.cityName)
                        }
                        activity.adapter!!.notifyDataSetChanged()
                        activity.listView!!.setSelection(0)
                        activity.currentLevel = LEVEL_CITY
                    }
                }
                //在ListView元件顯示縣區清單
                ChooseAreaFragment.LEVEL_COUNTY -> {
                    if (activity.cityList!!.size > 0) {
                        activity.dataList.clear()
                        for (county in activity.countyList!!) {
                            activity.dataList.add(county.countyName)
                        }
                        activity.adapter!!.notifyDataSetChanged()
                        activity.listView!!.setSelection(0)
                        activity.currentLevel = LEVEL_COUNTY
                    }
                }
            }
        }
    }
    //查詢所有的省
    private fun queryProvinces() {
        titleText!!.text = "中國"
        backButton!!.visibility = View.GONE
        DataSupport.getProvinces {
            provinceList = it
            var msg =Message()
            msg.obj = this
            msg.arg1 = LEVEL_PROVINCE
            handler.sendMessage(msg)
        }
    }
    //依選擇省查詢所有的城市
    private fun queryCities() {
        titleText!!.setText(selectProvince!!.provinceName)
        backButton!!.visibility = View.VISIBLE
        DataSupport.getCities(selectProvince!!.provinceCode) {
            cityList = it
            var msg =Message()
            msg.obj = this
            msg.arg1 = LEVEL_CITY
            handler.sendMessage(msg)
        }
    }
    //依選擇城市查詢所有的縣區
    private fun queryCounties() {
        titleText!!.setText(selectCity!!.cityName)
        backButton!!.visibility = View.VISIBLE
        DataSupport.getCounties(selectProvince!!.provinceCode, selectCity!!.cityCode) {
            countyList = it
            var msg =Message()
            msg.obj = this
            msg.arg1 = LEVEL_COUNTY
            handler.sendMessage(msg)
        }
    }
}
