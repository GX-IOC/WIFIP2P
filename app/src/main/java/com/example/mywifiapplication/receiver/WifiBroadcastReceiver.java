package com.example.mywifiapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import com.example.mywifiapplication.listener.DeviceListener;

public class WifiBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "WifiBroadcastReceiver";
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    private DeviceListener deviceListener;

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private WifiP2pManager.PeerListListener peerListListener;

    public WifiBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, WifiP2pManager.PeerListListener myPeerListListener, DeviceListener deviceListener, WifiP2pManager.ConnectionInfoListener connectionInfoListener) {
        this.manager = manager;
        this.channel = channel;
        this.peerListListener = myPeerListListener;
        this.deviceListener = deviceListener;
        this.connectionInfoListener = connectionInfoListener;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            //判断是否支持 wifi点对点传输
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            //查找设备列表
            WifiP2pDeviceList wifiP2pDeviceList = intent.getParcelableExtra(WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
            //单纯的使用requestPeers在部分机型上是获取不到的设备列表的，但是实际上是存在这个列表的
            if (wifiP2pDeviceList != null && wifiP2pDeviceList.getDeviceList().size() > 0) {
                deviceListener.deviceList(wifiP2pDeviceList);
            } else if (manager != null) {
                manager.requestPeers(channel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            //获取链接状态信息
            if (manager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo != null && networkInfo.isConnected()) {
                manager.requestConnectionInfo(channel, connectionInfoListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            //设备信息更改了会回调此方法
            Log.e(TAG, "onReceive: 设备信息更改");
        }
    }
}
