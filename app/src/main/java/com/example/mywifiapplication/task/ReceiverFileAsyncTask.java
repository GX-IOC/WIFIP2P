package com.example.mywifiapplication.task;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.example.mywifiapplication.Constants;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ReceiverFileAsyncTask extends AsyncTask<Void, Void, String> {
    private static final String TAG = "SendFileAsyncTask";

    @Override
    protected String doInBackground(Void... voids) {
        try {
            ServerSocket serverSocket = new ServerSocket(Constants.PORT);
            Socket socket = serverSocket.accept();

            try {
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                String name = dataInputStream.readUTF();
                String path = dataInputStream.readUTF();
                long fileLength = dataInputStream.readLong();
                String fileName = new File(path).getName();

                File file = new File(Environment.getExternalStorageDirectory() + "/Download/" + fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                byte[] bytes = new byte[1024];
                long total = 0;
                int length;
                while ((length = dataInputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, length);
                    total += length;
                    Log.e(TAG, "doInBackground: 文件接收进度:  " + (total * 100) / fileLength);
                }

                dataInputStream.close();
                fileOutputStream.close();
                serverSocket.close();
                return file.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.e(TAG, "onPostExecute: " + s);
    }
}
