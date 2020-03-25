package com.example.news.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetCacheUtils {

    private LocalCacheUtils mLocalCacheUtils;
    private MemoryCacheUtils mMemoryCacheUtils;
    private Bitmap bitmap;

    public NetCacheUtils(LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        mLocalCacheUtils = localCacheUtils;
        mMemoryCacheUtils = memoryCacheUtils;
    }

    public Bitmap getBitmapFromNet(String url) {
        Bitmap result = downLoadBitmap(url);
        if (result != null) {
            mMemoryCacheUtils.setBitmapToMemory(url, result);
            Log.d("NetBitmap", "onPostExecute: 保存内存ing");
            mLocalCacheUtils.setBitmapToLocal(url, result);
            Log.d("NetBitmap", "onPostExecute: 保存本地ing");
        }
        return result;
    }

    class BitmapTask extends AsyncTask<Object, Void, Bitmap> {

        private String url;

        /**
         * 后台耗时操作,存在于子线程中
         *
         * @param params
         * @return
         */
        @Override
        protected Bitmap doInBackground(Object[] params) {
            url = (String) params[0];
            bitmap = downLoadBitmap(url);
            return bitmap;
        }

        @Override
        protected void onProgressUpdate(Void[] values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                mMemoryCacheUtils.setBitmapToMemory(url, result);
                Log.d("NetBitmap", "onPostExecute: 保存内存ing");
                mLocalCacheUtils.setBitmapToLocal(url, result);
                Log.d("NetBitmap", "onPostExecute: 保存本地ing");
            }
        }
    }

    private Bitmap downLoadBitmap(String url) {
        if (url.equals(""))
            return null;
        Log.d("NetCache", "downLoadBitma url: " + url);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_4444;
                bitmap = BitmapFactory.decodeStream(conn.getInputStream(), null, options);

                if (bitmap == null)
                    Log.d("NetCache", "downLoadBitma bitmap: null");
                else
                    Log.d("NetCache", "downLoadBitma bitmap: " + bitmap.toString().substring(0, 10));
                return bitmap;
            }
            else {
                Log.d("NetCache", "downLoadBitma bitmap: failed");
            }
        } catch (IOException e) {
            Log.d("NetCache", "downLoadBitma openConnection: failed");
            e.printStackTrace();
            return null;
        } finally {
            conn.disconnect();
        }

        return null;
    }
}
