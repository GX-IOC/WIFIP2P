package com.example.mywifiapplication.activity;

import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.mywifiapplication.R;
import com.example.mywifiapplication.task.ReceiverFileAsyncTask;

public class ReceiverFileActivity extends WifiBaseActivity {

    private static final String TAG = "ReceiverFileActivity";

    private AsyncTask<Void, Void, String> execute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver_file);
        Button create_group = findViewById(R.id.create_group);
        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.createGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "onSuccess: 创建群组成功");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e(TAG, "onFailure: 创建群组失败" + reason);
                    }
                });
//                manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
//                    @Override
//                    public void onGroupInfoAvailable(WifiP2pGroup group) {
//                        Log.e(TAG, "onGroupInfoAvailable: "+new Gson().toJson(group));
//                    }
//                });
            }
        });
        execute = new ReceiverFileAsyncTask().execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (execute != null && !execute.isCancelled()) {
            execute.cancel(true);
        }
    }
}
