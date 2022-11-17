package com.example.pooldasenti2;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import com.google.gson.Gson;
import org.json.JSONObject;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class JsCallJava {
    private final static String TAG = "JsCallJava";
    private String mPreloadInterfaceJS;
    private final HashMap<String, Method> mMethodsMap;
    private final static String RETURN_RESULT_FORMAT = "{\"code\": %d, \"result\": %s}";
    private Gson mGson;

    public JsCallJava (Context mContext) {
        mMethodsMap = new HashMap<String, Method>();
        //获取自身声明的所有方法（包括public private protected）， getMethods会获得所有继承与非继承的方法
        //getDeclaredMethods能拿到所有（不包括继承的方法），而getMethods只能拿到public方法（包括继承的类或接口的方法）
        Method[] methods = AppJs.class.getMethods();
        for (Method method : methods) {
            String sign = method.getName();
            if (method.getModifiers() != (Modifier.PUBLIC | Modifier.STATIC) || sign == null) {
                continue;
            }
            mMethodsMap.put(sign, method);
            Log.e(TAG, "加载AppJs所有方法:" + method.getName());
        }

        try {
            mPreloadInterfaceJS = "";
            try {
                InputStream is = mContext.getResources().getAssets().open("android_brige.js");
                int lenght = is.available();
                byte[]  buffer = new byte[lenght];
                is.read(buffer);
                mPreloadInterfaceJS = new String(buffer, StandardCharsets.UTF_8);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch(Exception e){
            Log.e(TAG, "获取注入的android_brige.js失败:" + e.getMessage());
        }
    }

    public String getPreloadInterfaceJS () {
        return mPreloadInterfaceJS;
    }

    public String call(WebView webView, String jsonStr) {
        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONObject callJson = new JSONObject(jsonStr);
                // 1.方法匹配
                String methodName = callJson.optString("method");
                Method currMethod = mMethodsMap.get(methodName);
                if (currMethod == null) {
                    return getReturn(jsonStr, 500, "not found method(" + methodName + ") with valid parameters");
                }
                //2.方法参数
                String paramStr = callJson.optString("params");
                int len = 1;
                if (paramStr == null || paramStr.equals("null")){
                    len = 0;
                }
                Object[] values = new Object[len];
                if (len>0){
                    values[0] = paramStr;
                }
                // 方法调用
                Object result=currMethod.invoke(null,values);
                // 回调处理
                String callbackName = callJson.optString("callback");
                if(callbackName == null){
                }
                else{
                    TaskExecutor.scheduleTaskOnUiThread(0, new Runnable() {
                        @Override
                        public void run() {
                            String execJs = String.format("javascript:%s('%s');", callbackName, result.toString());
                            Log.d("JsCallBack", execJs);
                            webView.loadUrl(execJs);
                        }
                    });
                }
                return getReturn(jsonStr, 200, result);
            } catch (Exception e) {
                //优先返回详细的错误信息
                if (e.getCause() != null) {
                    return getReturn(jsonStr, 500, "method execute error:" + e.getCause().getMessage());
                }
                return getReturn(jsonStr, 500, "method execute error:" + e.getMessage());
            }
        } else {
            return getReturn(jsonStr, 500, "call data empty");
        }
    }

    private String getReturn (String reqJson, int stateCode, Object result) {
        String insertRes;
        if (result == null) {
            insertRes = "null";
        } else if (result instanceof String) {
            result = ((String) result).replace("\"", "\\\"");
            insertRes = "\"" + result + "\"";
        } else if (!(result instanceof Integer)
                && !(result instanceof Long)
                && !(result instanceof Boolean)
                && !(result instanceof Float)
                && !(result instanceof Double)
                && !(result instanceof JSONObject)) {    // 非数字或者非字符串的构造对象类型都要序列化后再拼接
            if (mGson == null) {
                mGson = new Gson();
            }
            insertRes = mGson.toJson(result);
        } else {  //数字直接转化
            insertRes = String.valueOf(result);
        }
        String resStr = String.format(RETURN_RESULT_FORMAT, stateCode, insertRes);
        Log.d(TAG, "DYL" + " call json: " + reqJson + " result:" + resStr);
        return resStr;
    }
}
