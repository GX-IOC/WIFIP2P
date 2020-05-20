package com.example.mywifiapplication.task;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import com.example.mywifiapplication.Constants;
import com.example.mywifiapplication.bean.FileBean;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
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
                ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                FileBean fileBean = (FileBean) objectInputStream.readObject();
                String fileName = new File(fileBean.getPath()).getName();

                File file = new File(Environment.getExternalStorageDirectory() + "/Download/" + fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);

                byte[] bytes = new byte[1024];
                long total = 0;
                int length;
                while ((length = inputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, 0, length);
                    total += length;
                    Log.e(TAG, "doInBackground: 文件接收进度:  " + (total * 100) / fileBean.getLength());
                }

                inputStream.close();
                objectInputStream.close();
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
