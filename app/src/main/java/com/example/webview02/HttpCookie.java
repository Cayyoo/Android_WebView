package com.example.webview02;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.Handler;
import android.os.Message;

/**
 * 同步Cookie的线程类
 */
public class HttpCookie extends Thread {
    private Handler handler;

    public HttpCookie(Handler handler){
        this.handler=handler;
    }

    @Override
    public void run() {
        HttpClient client=new DefaultHttpClient();
        //登录页面
        HttpPost post=new HttpPost("http://192.168.0.107:8080/web/login");
        //自定义登录信息，可据此判断与jsp页面的Cookie
        List<NameValuePair> list=new ArrayList<NameValuePair>();
        list.add(new BasicNameValuePair("name", "nates"));
        list.add(new BasicNameValuePair("pwd", "12"));

        try {
            post.setEntity(new UrlEncodedFormEntity(list));

            HttpResponse response=client.execute(post);
            //登录成功
            if (response.getStatusLine().getStatusCode()==200) {
                AbstractHttpClient absClient=(AbstractHttpClient) client;
                List<Cookie> cookies=absClient.getCookieStore().getCookies();
                //遍历cookie
                for (Cookie cookie:cookies) {
                    System.out.println("name="+cookie.getName()+",pwd="+cookie.getValue());

                    Message msg=new Message();
                    System.out.println("cookie----"+cookie);
                    msg.obj=cookie;

                    handler.sendMessage(msg);
                    return;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
