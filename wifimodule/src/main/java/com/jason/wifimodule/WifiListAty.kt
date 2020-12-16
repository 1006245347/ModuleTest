package com.jason.wifimodule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_wifi_list_aty.*

class WifiListAty : AppCompatActivity() {

    inner class WifiReceiver : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.action)) {
                //wifi开关变化
                val state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0)
                when (state) {
                    WifiManager.WIFI_STATE_DISABLED -> {    //已关闭
                        Log.v("TAG", "已经关闭")
                    }
                    WifiManager.WIFI_STATE_DISABLING -> {   //wifi正在关闭
                        Log.v("TAG", "正在关闭")
                    }
                    WifiManager.WIFI_STATE_ENABLED -> {     //wifi已打开
                        Log.v("TAG", "已经打开")
                    }
                    WifiManager.WIFI_STATE_ENABLING -> {    //正在打开
                        Log.v("TAG", "正在打开")
                    }
                    WifiManager.WIFI_STATE_UNKNOWN -> {     //未知状态
                        Log.v("TAG", "未知状态")
                    }
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.action)) { //监听wifi链接状态
                var info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO) as NetworkInfo?
                info?.let {
                    if (NetworkInfo.State.DISCONNECTED == it.state) {
                        Log.v("TAG", "wifi没有连接上")
                    } else if (NetworkInfo.State.CONNECTED == it.state) {
                        Log.v("TAG", "wifi连接上了")
                        val scanList = WifiUtils.getWifiList(wifiMgr)
                        scanList?.let {
                            for (i in scanList!!.indices) {
                                val wifiName = scanList!!.get(i).SSID
                                val wifiLevel = scanList!!.get(i).level
                                Log.v("TAG", "scan_$i $wifiName $wifiLevel")
                            }
                            val wifiInfo = wifiMgr?.connectionInfo
                            val connedWifi = wifiInfo?.ssid
                            val connedLevel = wifiInfo?.ssid
                            Log.v("TAG", "wifi_conned=$connedWifi $connedLevel")
                        }
                    } else if (NetworkInfo.State.CONNECTING == it.state) {
                        Log.v("TAG", "wifi正在连接..")
                    } else {
//                        Log.v("")
                    }
                }

            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.action)) {
                Log.v("TAG", "wifi列表发生变化")
            }
        }
    }

    private var wifiReceiver: WifiBroadcastReceiver? = null
    private var wifiMgr: WifiManager? = null
    private var locationMgr: LocationManager? = null
    private var scanList: MutableList<ScanResult>? = null
    private val wifiList = mutableListOf<WifiBean>()
    private var mAdapter: WifiListAdapter? = null
    private var curIndex = -1    //当前wifi的指针
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_list_aty)
        initAty()
    }
//https://github.com/wuqingsen/WifiDemoWu/blob/master/WifiDemoWu/app/src/main/java/com/wifidemowu/MainActivity.java
    private fun initAty() {
        Toast.makeText(this, "权限！", Toast.LENGTH_LONG).show()
        tv_scan.setOnClickListener {
            wifiList.clear()
            WifiUtils.openWifi(wifiMgr)
            scanList = WifiUtils.getWifiList(wifiMgr)
            scanList?.let {
                for (i in scanList!!.indices) {
                    val item = WifiBean()
                    item.wifiName = it.get(i).SSID
                    item.encrypt = WifiUtils.getEncrypt(wifiMgr, scanList!!.get(i))
                    wifiList.add(item)
                }
            }
            if (wifiList.size > 0) {
                Toast.makeText(this, "获取wifi列表成功", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "wifi列表无数据", Toast.LENGTH_SHORT).show()
            }
            mAdapter?.notifyDataSetChanged()
        }
        tv_open.setOnClickListener {
            WifiUtils.openWifi(wifiMgr)
        }
        wifiMgr = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        locationMgr =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        wifiReceiver = WifiBroadcastReceiver()
        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        registerReceiver(wifiReceiver, filter)

        mAdapter = WifiListAdapter(R.layout.item_wifi, wifiList)
        rvList.layoutManager = GridLayoutManager(this, 2)
        rvList.adapter = mAdapter
        mAdapter?.setOnItemClickListener { adapter, view, position ->
            val item = adapter.getItem(position) as WifiBean
            tv_cur.text = item.wifiName
            curIndex = position
        }

        tv_save.setOnClickListener {
            if (curIndex==-1)return@setOnClickListener
            WifiUtils.disconnectNetwork(wifiMgr)    //断开当前wifi
            val type = WifiUtils.getEncrypt(wifiMgr, scanList?.get(curIndex))//加密方式
            WifiUtils.connectWifi(
                wifiMgr,
                scanList?.get(curIndex)?.SSID,
                et_input.text.toString(),
                type
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (wifiReceiver != null)
            unregisterReceiver(wifiReceiver)
    }
}
