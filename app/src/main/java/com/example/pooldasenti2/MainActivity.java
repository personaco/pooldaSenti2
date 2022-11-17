package com.example.pooldasenti2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    public static final String HOST = "http://studyeng.peso.co.kr/";

    private WebView webView;
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webview);
        //WebView加载web资源
        webView.loadUrl(HOST);

        //启用支持javascript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        //设置 缓存模式
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        //开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
        //支持中文，否则页面中中文显示乱码
        webView.getSettings().setDefaultTextEncodingName("UTF-8");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                //view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                dialog = ProgressDialog.show(MainActivity.this, null, "页面加载中，请稍后..");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                dialog.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                }
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) { // Handle the error
                if (error != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    }
                }

                dialog.dismiss();

                AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
                alert.setTitle("提示"); // 设置对话框的标题
                //alert.setMessage("加载失败！"); // 设置要显示的内容
                alert.setButton(DialogInterface.BUTTON_POSITIVE, "刷新",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                webView.loadUrl(HOST);
                            }
                        });
                alert.setButton(DialogInterface.BUTTON_NEGATIVE, "关闭",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        });
                alert.show(); // 创建对话框并显示

                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
            }
        });

        webView.setWebChromeClient(new MyWebChromClient(MainActivity.this) );

        //优先使用缓存
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //不使用缓存
        //webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    }

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(webView.canGoBack())
            {
                webView.goBack();//返回上一页面
                return true;
            }
            else
            {
                System.exit(0);//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    // 网络状态
    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

}