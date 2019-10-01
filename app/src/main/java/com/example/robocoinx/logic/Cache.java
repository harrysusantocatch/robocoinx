package com.example.robocoinx.logic;

import android.util.LruCache;

public class Cache {

    private static Cache instance;
    private LruCache<Object, Object> lru;

    private Cache() {
        lru = new LruCache<>(1024);
    }

    public static Cache getInstance() {
        if (instance == null) {
            instance = new Cache();
        }
        return instance;
    }

    public LruCache<Object, Object> getLru() {
        return lru;
    }
}
