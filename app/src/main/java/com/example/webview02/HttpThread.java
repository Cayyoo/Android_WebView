package com.example.webview02;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.util.Log;

/**
 * 下载文件的线程类
 */
public class HttpThread extends Thread {
    private String mUrl;

    public HttpThread(String url){
        this.mUrl=url;
    }

    @Override
    public void run() {
        try {
            System.out.println("start download");
            Log.i("Tiger", "start download");

            URL httpUrl=new URL(mUrl);
            //打开连接
            HttpURLConnection conn=(HttpURLConnection) httpUrl.openConnection();
            //接收输入流
            conn.setDoInput(true);
            //发送输出流
            conn.setDoOutput(true);
            //拿到输入流对象
            InputStream in=conn.getInputStream();

            /**
             * 判断SDCard是否存在
             */
            File downloadFile,sdFile;
            FileOutputStream out=null;
            //检查SDCard是否挂载
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                //拿到存储目录
                downloadFile=Environment.getExternalStorageDirectory();
                //创建文件目录，给下载文件命名
                sdFile=new File(downloadFile,"text.apk");
                //获得输出流对象
                out=new FileOutputStream(sdFile);
            }

            //定义缓存大小
            byte[] b=new byte[6*1024];
            //读出流的位置信息
            int len;
            //-1表示终止
            while ((len=in.read(b)) != -1) {
                if (out!=null) {
                    out.write(b,0,len);
                }
            }

            //输出流不为空则关闭
            if (out!=null) {
                out.close();
            }
            //输入流不为空则关闭
            if (in!=null) {
                in.close();
            }

            System.out.println("download success");
            Log.i("Tiger", "download success");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
