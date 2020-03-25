package com.example.news.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

public class MyBitmapUtils {

    private NetCacheUtils mNetCacheUtils;
    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;

    public MyBitmapUtils(Context context) {

        Log.d("MyBitmapUtils", "MyBitmapUtils: ");
        mMemoryCacheUtils = new MemoryCacheUtils();
        mLocalCacheUtils = new LocalCacheUtils(context);
        mNetCacheUtils = new NetCacheUtils(mLocalCacheUtils, mMemoryCacheUtils);
    }

    public Bitmap getBitmap(String url) {
        Bitmap bitmap;
        bitmap = mMemoryCacheUtils.getBitmapFromMemory(url);
        if (bitmap!= null) {
            Log.d("getBitmap", "内存获取Bitmap");
            return bitmap;
        }

        bitmap = mLocalCacheUtils.getBitmapFromLocal(url);
        if (bitmap != null) {
            mMemoryCacheUtils.setBitmapToMemory(url, bitmap);
            Log.d("getBitmap", "本地获取Bitmap");
            return bitmap;
        }

        bitmap = mNetCacheUtils.getBitmapFromNet(url);
        Log.d("getBitmap", "网络获取Bitmap url: " + url + " " + bitmap);
        return bitmap;
    }
}
