package com.example.webview02;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

/**
 * 实现WebView以下功能：
 *
 * 自定义WebView的title
 * 下载文件
 * 处理错误码
 * 同步Cookie
 * 与JS调用混淆问题
 * 远程注入问题，4.2以后已解决
 * 自定义拦截
 */
public class MainActivity extends Activity {
    private WebView mWebView;
    private Button back;
    private Button refresh;
    private TextView mTitle,mTextView_error;

    /**
     * 处理Cookie
     */
    private Handler handler=new Handler(){
        public void hanlderMessage(Message msg){
            String cookie=(String) msg.obj;
            CookieSyncManager.createInstance(MainActivity.this);

            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setCookie("http://192.168.0.107:8080/web", msg.obj.toString());
            CookieSyncManager.getInstance().sync();
            //cookie验证通过后跳转页面
            mWebView.loadUrl("http://192.168.0.107:8080/web/index.jsp");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化mWebView
        mWebView=(WebView) findViewById(R.id.webView1);
        //加载网页地址
        mWebView.loadUrl("https://www.baidu.com/");

        //初始化Button
        back=(Button) findViewById(R.id.back);
        refresh=(Button) findViewById(R.id.refresh);
        //初始化TextView
        mTitle=(TextView) findViewById(R.id.title);
        mTextView_error=(TextView) findViewById(R.id.textview_error);

        //屏蔽WebView在浏览器中打开，步骤一
        mWebView.setWebChromeClient(new WebChromeClient(){

            @Override
            public void onReceivedTitle(WebView view, String title) {
                //给顶部TextView设置title文本
                mTitle.setText(title);
                super.onReceivedTitle(view, title);
            }

        });

        //屏蔽WebView在浏览器中打开，步骤二
        mWebView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /**
                 * 自定义拦截：根据约定的协议判断是否打开页面
                 */
                //jsp页面中给以个带参的url:http://192.168.0.107/web?这里是约定的协议或者参数
                if (url.endsWith("?这里是约定的协议或者参数")) {
                    Intent intent=new Intent(MainActivity.this,SecondActivity.class);
                    startActivity(intent);
                    return true;
                }

                //让url在项目中加载
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            /**
             * 错误码处理
             */
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //错误码处理方式一：加载本地错误页面
                mWebView.loadUrl("file:///android_asset/error.html");

                //错误码处理方式二：加载本地native布局
//				mTextView_error.setText("Native布局错误码：404 error");
//				mWebView.setVisibility(View.GONE);
            }

        });

        //启动Cookie同步线程
        new HttpCookie(handler).start();

        //设置点击监听
        refresh.setOnClickListener(new MyListener());
        back.setOnClickListener(new MyListener());
        //设置下载监听
        mWebView.setDownloadListener(new MyDownload());
    }

    /**
     * 自定义点击事件监听类
     */
    class MyListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.refresh:
                    mWebView.reload();
                    break;
                case R.id.back:
                    finish();
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 自定义下载监听的类
     */
    class MyDownload implements DownloadListener{
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
                                    long contentLength) {
            System.out.println("url-----------"+url);
            //如果是apk文件则下载
            if (url.endsWith("apk")) {
                //下载方式一：自定义方法，调用应用程序本身下载
                //new HttpThread(url).start();

                //下载方式二：系统方法，调用系统浏览器下载
                Uri uri=Uri.parse(url);
                Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
