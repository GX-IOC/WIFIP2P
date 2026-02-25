package com.example.mywifiapplication.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mywifiapplication.Constants;
import com.example.mywifiapplication.bean.FileBean;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendFileService extends IntentService {

    private static final String TAG = "SendFileService";

    private static final int SOCKET_TIMEOUT = 3000;
    private Socket socket;
    private OutputStream outputStream;
    private DataOutputStream dataOutputStream;
    private FileInputStream fileInputStream;


    public SendFileService(String name) {
        super(name);
    }

    public SendFileService() {
        super("SendFileService");
    }

    @Override
    public void onHandleIntent(@Nullable Intent intent) {
        if (intent != null && intent.getAction() != null && intent.getAction().equals(Constants.ACTION_SEND_FILE)) {
            String path = intent.getExtras().getString(Constants.FILEPATH);
            String host = intent.getExtras().getString(Constants.HOSTADDRESS);
            if (path == null && host == null) {
                return;
            }
            File file = new File(path);
            FileBean fileBean = new FileBean();
            fileBean.setLength(file.length());
            int indexOf = path.lastIndexOf("/");
            String fileName = path.substring(indexOf + 1);
            fileBean.setName(fileName);
            fileBean.setPath(path);
            sendFileSocket(host, Constants.PORT, file, fileBean);

        }
    }

    private void sendFileSocket(String host, int port, File file, FileBean fileBean) {
        socket = new Socket();
        try {
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(fileBean.getName());
            dataOutputStream.writeUTF(fileBean.getPath());
            dataOutputStream.writeLong(fileBean.getLength());
            dataOutputStream.flush();
            fileInputStream = new FileInputStream(file);

            long size = file.length();
            long total = 0;
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fileInputStream.read(bytes)) != -1) {
                dataOutputStream.write(bytes, 0, length);
                total += length;
                Log.e(TAG, "onHandleIntent: 文件发送进度" + (total * 100) / size);
            }
            dataOutputStream.flush();
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "onHandleIntent: 出现了错误");
            sendFileSocket(host, port, file, fileBean);
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (dataOutputStream != null) {
                dataOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}