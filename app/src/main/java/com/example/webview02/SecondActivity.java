package com.example.webview02;

import android.app.Activity;
import android.os.Bundle;

/**
 * 自定义拦截，协议通过可打开这个页面
 */
public class SecondActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_layout);
    }
}
