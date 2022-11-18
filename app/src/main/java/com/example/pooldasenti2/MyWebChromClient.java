package com.example.pooldasenti2;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

public class MyWebChromClient extends WebChromeClient {
    private final JsCallJava mJsCallJava;
    private boolean mIsInjectedJS; //是否已注入

    public MyWebChromClient(Context mContext) {
        mJsCallJava = new JsCallJava(mContext); //获取要注入的android_brige.js
    }

    /**
     * 获取WebView的标题
     * @param view
     * @param title
     */
    @Override
    public void onReceivedTitle(WebView view, String title) {

    }

    /**
     * 捕获html5 js中的alert事件, 将alert事件转换为Toast形式显示,但是不道为啥我的能toast,
     * 但是toast之后输入框的焦点就没有了，不能再输入第二次了，我用的是魅族手机
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        //CookieBarUtils.showMessageWithTip((Activity)view.getContext(),message);
        result.cancel();
        return true;
    }

    /**
     * 处理Javascript中的Confirm对话框
     * @param view
     * @param url
     * @param message
     * @param result
     * @return
     */
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
        //CookieBarUtils.showMessageWithTip((Activity)view.getContext(),message);
        result.cancel();
        return true;
    }

    /**
     * 处理Javascript中的Prompt对话框。
     * @param view
     * @param url
     * @param message
     * @param defaultValue
     * @param result
     * @return
     */
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        result.confirm(mJsCallJava.call(view, message));
        result.cancel();
        return true;
    }

    /**
     * 进度条加载
     * @param view
     * @param newProgress
     */
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (newProgress == 100) {
            // 网页加载完成
        } else {
            // 加载中
        }
        //여기에 JS를 주입하는 이유
        //1 OnPageStarted의 주입은 전역 주입에 실패할 수 있으므로 페이지 스크립트의 모든 인터페이스를 언제든지 사용할 수 없게 됩니다.
        //2 OnPageFinished에 주입합니다. 전역 주입은 결국 성공하지만 완료 시간이 너무 늦어 인터페이스 기능이 초기화될 때 페이지가 너무 오래 기다릴 수 있습니다.
        //3 위의 두 가지 문제 사이의 타협점을 얻기 위해 진행률이 변경될 때 주입
        //진행률이 25% 이상일 때 인젝션이 수행되는 이유는 테스트에서 진행률이 이 숫자 페이지보다 클 때만 프레임을 새로고침하고 로드할 수 있고 100% 인젝션이 성공할 수 있기 때문입니다.
        if (newProgress <= 25) {
            mIsInjectedJS = false;
        } else if (!mIsInjectedJS) {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                view.loadUrl("javascript:" + mJsCallJava.getPreloadInterfaceJS());
            }
            else{
                view.evaluateJavascript(mJsCallJava.getPreloadInterfaceJS(), new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.i("onReceiveValue", value);
                    }
                });
            }
            mIsInjectedJS = true;
            Log.d("MyWebChromClient"," 注入js接口完成 on progress " + newProgress);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
    }

    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        super.onShowCustomView(view, requestedOrientation, callback);
    }

    @Override
    public void onHideCustomView() {
        super.onHideCustomView();
    }

    @Override
    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onRequestFocus(WebView view) {
        super.onRequestFocus(view);
    }

    @Override
    public void onCloseWindow(WebView window) {
        super.onCloseWindow(window);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
        return super.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota, long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize, totalQuota, quotaUpdater);
    }

    @Override
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        super.onGeolocationPermissionsHidePrompt();
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        super.onPermissionRequest(request);
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        super.onPermissionRequestCanceled(request);
    }

    @Override
    public boolean onJsTimeout() {
        return super.onJsTimeout();
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        super.onConsoleMessage(message, lineNumber, sourceID);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        return super.onConsoleMessage(consoleMessage);
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        return super.getDefaultVideoPoster();
    }

    @Override
    public View getVideoLoadingProgressView() {
        return super.getVideoLoadingProgressView();
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        super.getVisitedHistory(callback);
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }
}
