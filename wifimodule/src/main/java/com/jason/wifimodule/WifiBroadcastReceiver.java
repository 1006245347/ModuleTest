package com.jason.wifimodule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String TAG = "TAG";
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            //wifi开关变化
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (state) {
                case WifiManager.WIFI_STATE_DISABLED: {
                    //wifi关闭
                    Log.i(TAG, "已经关闭");
                    break;
                }
                case WifiManager.WIFI_STATE_DISABLING: {
                    //wifi正在关闭
                    Log.i(TAG, "正在关闭");
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLED: {
                    //wifi已经打开
                    Log.i(TAG, "WIFI已打开");
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLING: {
                    //wifi正在打开
                    Log.i(TAG, "正在打开");
                    break;
                }
                case WifiManager.WIFI_STATE_UNKNOWN: {
                    //未知
                    Log.i(TAG, "未知状态");
                    break;
                }
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            //监听wifi连接状态
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
//                Log.i(TAG, "--NetworkInfo--" + info.toString());
           /* if (NetworkInfo.State.DISCONNECTED == info.getState()) {//wifi没连接上
                Log.i(TAG, "wifi没连接上");
            } else if (NetworkInfo.State.CONNECTED == info.getState()) {//wifi连接上了
                mScanResultList = MyWifiManager.getWifiList(mWifiManager);//获取Wifi列表
                for (int i = 0; i < mScanResultList.size(); i++) {
                    mScanResultList.get(i).SSID//Wifi名称
                    mScanResultList.get(i).level//wifi信号强度
                    //其他就不列举了，根据自己的需求去取
                    //在这增加一个获取当前已连接WiFi的名称以及信号强度
                    WifiInfo wifiInfo = mWifiManager .getConnectionInfo();
                    String name = wifiInfo.getSSID();//名称
                    WifiInfo wifiInfo = manager.getConnectionInfo();
                    int wiFiLevel = wifiInfo.getRssi();//信号强度
                }
            } else if (NetworkInfo.State.CONNECTING == info.getState()) {//正在连接
                Log.i(TAG, "wifi正在连接");
                tvConnectionCtatus.setText("正在连接");
            }*/
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
            //监听wifi列表变化
            Log.i(TAG, "wifi列表发生变化");
        }
    }
}
