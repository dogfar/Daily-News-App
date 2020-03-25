package com.example.news.Utils;

import android.graphics.Bitmap;
import android.util.LruCache;
import android.util.Log;

import java.util.Iterator;
import java.util.Map;

public class MemoryCacheUtils {
    private static final String TAG = "MemoryCacheUtils";

    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCacheUtils() {
        long maxMemory = Runtime.getRuntime().maxMemory() / 8;
        Log.d(TAG, "getBitmapFromMemory-->maxMemory:" + maxMemory);
        mMemoryCache = new LruCache<String, Bitmap>((int) maxMemory) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int byteCount = value.getByteCount();
                Log.d(TAG, "getBitmapFromMemory-->sizeOf:" + byteCount);
                return byteCount;
            }
        };


    }

    public Bitmap getBitmapFromMemory(String url) {
        Bitmap bitmap = mMemoryCache.get(url);

        Map<String, Bitmap> snapshot = mMemoryCache.snapshot();
        Log.d(TAG, "getBitmapFromMemory-->url:" + url + "******size:--->" + snapshot.size());
        Iterator<Map.Entry<String, Bitmap>> it = snapshot.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Bitmap> entry = it.next();
            Log.d("Map", entry.getKey() + "******" + entry.getValue());
        }

        return bitmap;

    }

    public void setBitmapToMemory(String url, Bitmap bitmap) {
        try {

            mMemoryCache.put(url, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setBitmapToMemory: " + url + "***------->" + bitmap.getByteCount());
    }
}