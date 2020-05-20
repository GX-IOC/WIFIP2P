package com.example.mywifiapplication.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mywifiapplication.Constants;
import com.example.mywifiapplication.bean.FileBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class SendFileService extends IntentService {

    private static final String TAG = "SendFileService";

    private static final int SOCKET_TIMEOUT = 3000;
    private Socket socket;
    private OutputStream outputStream;
    private ObjectOutputStream objectOutputStream;
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
            objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(fileBean);//对象输出
            fileInputStream = new FileInputStream(file);

            long size = file.length();
            long total = 0;
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);//传输文件
                total += length;
                Log.e(TAG, "onHandleIntent: 文件发送进度" + (total * 100) / size);
            }
            if (outputStream != null) {
                outputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "onHandleIntent: 出现了错误");
            sendFileSocket(host, port, file, fileBean);//此处是否要这样去重连未知，有时候找不到路由器主机，重新进入该页面就好了，但是我怀疑问题还是重连的问题
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
            if (outputStream != null) {
                outputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}