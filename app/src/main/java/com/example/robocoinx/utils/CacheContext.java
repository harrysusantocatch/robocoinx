package com.example.robocoinx.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONObject;

public class CacheContext<T> {

    private Class<T> typeParameterClass;
    private static Context ctx;

    public CacheContext(Class<T> _typeParameterClass, Context _ctx){
        ctx = _ctx;
        typeParameterClass = _typeParameterClass;
    }

    public void save(T obj, String cacheName){
        SharedPreferences preferences = ctx.getSharedPreferences(cacheName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String value = new Gson().toJson(obj);
        editor.putString(cacheName, value);
        editor.apply();
    }

    public T get(String cacheName){
        SharedPreferences preferences = ctx.getSharedPreferences(cacheName, Context.MODE_PRIVATE);
        String value = preferences.getString(cacheName, null);
        if(value == null) return null;
        T t = new Gson().fromJson(value, typeParameterClass);
        return t;
    }

    public void clear(String cacheName){
        SharedPreferences preferences = ctx.getSharedPreferences(cacheName, Context.MODE_PRIVATE);
        preferences.edit().apply();
    }
}
