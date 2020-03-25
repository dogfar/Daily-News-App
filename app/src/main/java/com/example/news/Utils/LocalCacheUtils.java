package com.example.news.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class LocalCacheUtils {

    private Context context;
    String path = null;

    public LocalCacheUtils(Context context) {
        this.context = context;
        path = context.getCacheDir().getAbsolutePath();
        Log.d("LocalCacheUtils", "path: " + path);
    }


    private static final String CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WebNews";

    public Bitmap getBitmapFromLocal(String url) {
        String fileName = null;
        try {
            fileName = MD5Encoder.encode(url);
            File file = new File(path, fileName);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                return bitmap;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setBitmapToLocal(String url, Bitmap bitmap) {
        try {
            Log.d("setBitmapToLocal", "url:" + url);

            String fileName = MD5Encoder.encode(url);
            Log.d("setBitmapToLocal", "fileName:" + fileName);
            File file = new File(path, fileName);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(file));


        } catch (Exception e) {

            e.printStackTrace();
        }

    }
}

