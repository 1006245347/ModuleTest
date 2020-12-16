package com.jason.wifimodule;

import android.annotation.SuppressLint;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author by jason-何伟杰，2020/12/16
 * des:7.0需要位置权限
 */
public class WifiUtils {
    /**
     * 开始扫描wifi
     */
    public static void startScanWifi(WifiManager manager) {
        if (manager != null) {
            manager.startScan();
        }
    }


    /**
     * 获取wifi列表
     */
    public static List<ScanResult> getWifiList(WifiManager mWifiManager) {
        return mWifiManager.getScanResults();
    }


    /**
     * 保存网络
     */
    public static void saveNetworkByConfig(WifiManager manager, WifiConfiguration config) {
        if (manager == null) {
            return;
        }
        try {
            Method save = manager.getClass().getDeclaredMethod("save", WifiConfiguration.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (save != null) {
                save.setAccessible(true);
                save.invoke(manager, config, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 忘记网络
     */
    public static void forgetNetwork(WifiManager manager, int networkId) {
        if (manager == null) {
            return;
        }
        try {
            Method forget = manager.getClass().getDeclaredMethod("forget", int.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (forget != null) {
                forget.setAccessible(true);
                forget.invoke(manager, networkId, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 断开连接
     */
    public static boolean disconnectNetwork(WifiManager manager) {
        return manager != null && manager.disconnect();
    }


    /**
     * 获取当前wifi名字
     *
     * @return
     */
    public static String getWiFiName(WifiManager manager) {
        WifiInfo wifiInfo = manager.getConnectionInfo();
        String name = wifiInfo.getSSID();
        return name.replace("\"", "");
    }

    /**
     * 获取当前WIFI信号强度
     *
     * @param manager
     * @return
     */
    public static int getWiFiLevel(WifiManager manager) {
        WifiInfo wifiInfo = manager.getConnectionInfo();
        return wifiInfo.getRssi();
    }

    /**
     * 获取wifi加密方式
     */
    public static String getEncrypt(WifiManager mWifiManager, ScanResult scanResult) {
        if (mWifiManager != null) {
            String capabilities = scanResult.capabilities;
            if (!TextUtils.isEmpty(capabilities)) {
                if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                    return "WPA";
                } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                    return "WEP";
                } else {
                    return "没密码";
                }
            }
        }
        return "获取失败";
    }

    /**
     * 是否开启wifi，没有的话打开wifi
     */
    public static void openWifi(WifiManager mWifiManager) {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 是否开启wifi，没有的话打开wifi
     */
    public static void closeWifi(WifiManager mWifiManager) {
        mWifiManager.setWifiEnabled(false);
    }

    /**
     * 有密码连接
     *
     * @param ssid
     * @param pws
     */
    public static void connectWifiPws(WifiManager mWifiManager, String ssid, String pws) {
        mWifiManager.disableNetwork(mWifiManager.getConnectionInfo().getNetworkId());
        int netId = mWifiManager.addNetwork(getWifiConfig(mWifiManager, ssid, pws, true));
        mWifiManager.enableNetwork(netId, true);
    }

    /**
     * 连接WiFi
     *
     * @param wifiManager
     * @param wifiName
     * @param password
     * @param type
     */
    @SuppressLint("WifiManagerLeak")
    public static void connectWifi(WifiManager wifiManager, String wifiName, String password, String type) {
        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + wifiName + "\"";
        String psd = "\"" + password + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (type) {
            case "WEP":
                // 加密类型为WEP
                conf.wepKeys[0] = psd;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
                // 加密类型为WPA
                conf.preSharedKey = psd;
                break;
            default:
                //无密码
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //3、链接wifi
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(ssid)) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }

    /**
     * wifi设置
     *
     * @param ssid
     * @param pws
     * @param isHasPws
     */
    private static WifiConfiguration getWifiConfig(WifiManager mWifiManager, String ssid, String pws, boolean isHasPws) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        WifiConfiguration tempConfig = isExist(ssid, mWifiManager);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        if (isHasPws) {
            config.preSharedKey = "\"" + pws + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        } else {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        return config;
    }

    /**
     * 得到配置好的网络连接
     *
     * @param ssid
     * @return
     */
    private static WifiConfiguration isExist(String ssid, WifiManager mWifiManager) {
        List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration config : configs) {
            if (config.SSID.equals("\"" + ssid + "\"")) {
                return config;
            }
        }
        return null;
    }
}
