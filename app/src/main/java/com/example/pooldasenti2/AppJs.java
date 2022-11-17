package com.example.pooldasenti2;

import android.app.Activity;
import android.os.Build;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


class AppJs {
    public static String getUserInfo() {
        try{
            JSONObject result =  new JSONObject();
            result.put("nickName","李香成1");
            result.put("id","0486b270-4261-4caa-a5d5-568f65d891dc");
            result.put("phoneNumber","18516029856");
            return result.toString();
        }
        catch (Exception e) {
            return "";
        }
    }

    public static String execApiRequest(String params) {
        try{
            JSONObject result =  new JSONObject(params);
            result.put("code","200");
            result.put("domain","李香成");
            result.put("userInfo","xxx");
            return result.toString();
        }
        catch (Exception e) {
            return params;
        }
    }
}

