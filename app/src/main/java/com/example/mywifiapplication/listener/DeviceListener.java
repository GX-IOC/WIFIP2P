package com.example.mywifiapplication.listener;

import android.net.wifi.p2p.WifiP2pDeviceList;

public interface DeviceListener {
    void deviceList(WifiP2pDeviceList wifiP2pDeviceList);
}
