package com.example.mywifiapplication.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywifiapplication.Constants;
import com.example.mywifiapplication.R;
import com.example.mywifiapplication.adapter.MyRecyclerAdapter;
import com.example.mywifiapplication.listener.DeviceListener;
import com.example.mywifiapplication.receiver.WifiBroadcastReceiver;
import com.example.mywifiapplication.service.SendFileService;
import com.example.mywifiapplication.utils.FileUtils;
import com.example.mywifiapplication.utils.PermissionsUtils;

public class SendFileActivity extends WifiBaseActivity implements View.OnClickListener, WifiP2pManager.ConnectionInfoListener, DeviceListener {

    private static final String TAG = "SendFileActivity";

    private Button btn_search;
    private Button btn_send;
    private TextView tv_info;
    private RecyclerView info_recycle;
    private WifiBroadcastReceiver wifiBroadcastReceiver;
    private MyRecyclerAdapter mAdapter;
    private WifiP2pInfo mInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitvity_send_file);
        initView();
        initClick();
        initPermission();
        initBroadcast();
        registerReceiver(wifiBroadcastReceiver, intentFilter);
    }

    private void initView() {
        btn_search = findViewById(R.id.btn_search);
        btn_send = findViewById(R.id.btn_send);
        tv_info = findViewById(R.id.tv_info);
        info_recycle = findViewById(R.id.info_recycle);
        info_recycle.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initClick() {
        btn_search.setOnClickListener(this);
        btn_send.setOnClickListener(this);
    }

    private void initPermission() {
        PermissionsUtils.getInstance().checkPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    private void initBroadcast() {
        WifiP2pManager.PeerListListener myPeerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList list) {
                mAdapter = new MyRecyclerAdapter(SendFileActivity.this, list.getDeviceList(), manager, channel);
                info_recycle.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        };
        wifiBroadcastReceiver = new WifiBroadcastReceiver(manager, channel, myPeerListListener, this, this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_search) {
            search();
        } else if (id == R.id.btn_send) {
            selectPicSend();
        }
    }

    /**
     * 搜索
     */
    private void search() {
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "onSuccess: 搜索成功");
            }

            @Override
            public void onFailure(int reason) {
                Log.e(TAG, "onFailure: 搜索失败  reason: " + reason);
            }
        });
    }

    /**
     * 本地选择文件
     */
    private void selectPicSend() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //TODO 该功能中所有的连接，流，广播，服务等等，全部都要及时关闭
        unregisterReceiver(wifiBroadcastReceiver);
        wifiBroadcastReceiver = null;
        stopService(new Intent(this, SendFileService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                tv_info.setText(uri.toString());
                String path = FileUtils.getAbsolutePath(this, uri);
                Intent serviceIntent = new Intent(this, SendFileService.class);
                serviceIntent.setAction(Constants.ACTION_SEND_FILE);
                serviceIntent.putExtra(Constants.FILEPATH, path);
                //TODO 此处还存在一个问题，可能你选择完了文件，但是info还没拿到，因为他们还没有建立完成连接，所以这里在还没有拿到数据的时候最好给他转个圈圈，让他等待
                serviceIntent.putExtra(Constants.HOSTADDRESS, mInfo.groupOwnerAddress.getHostAddress());
                startService(serviceIntent);
            }
        }
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {//WifiP2pManager.ConnectionInfoListener的回调
        if (info != null) {
            mInfo = info;
            if (info.groupFormed && info.isGroupOwner) {
                Log.e(TAG, "onConnectionInfoAvailable: 群组所有者");
            } else {
                Log.e(TAG, "onConnectionInfoAvailable: not");
            }
        }
    }

    @Override
    public void deviceList(WifiP2pDeviceList wifiP2pDeviceList) {//DeviceListener的回调
        mAdapter = new MyRecyclerAdapter(SendFileActivity.this, wifiP2pDeviceList.getDeviceList(), manager, channel);
        info_recycle.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
