package com.example.mywifiapplication.adapter;

import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mywifiapplication.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "MyRecyclerAdapter";

    private WifiP2pManager manage;
    private WifiP2pManager.Channel channel;
    private Context mContext;
    private List<WifiP2pDevice> wifiP2pDeviceList;
    private WifiP2pDevice p2pDevice;

    public MyRecyclerAdapter(Context context, Collection<WifiP2pDevice> deviceList, WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.mContext = context;
        wifiP2pDeviceList = new ArrayList<>();
        wifiP2pDeviceList.addAll(deviceList);
        this.manage = manager;
        this.channel = channel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        WifiP2pDevice wifiP2pDevice = wifiP2pDeviceList.get(position);
        viewHolder.item_name.setText(wifiP2pDevice.deviceName);
        viewHolder.item_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                p2pDevice = wifiP2pDeviceList.get(position);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = p2pDevice.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                manage.connect(channel, config, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.e(TAG, "onSuccess: 连接成功");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e(TAG, "onFailure: 连接失败");
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return wifiP2pDeviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView item_name;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name = itemView.findViewById(R.id.recycler_item_name);
        }
    }
}
