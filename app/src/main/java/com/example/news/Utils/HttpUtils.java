package com.example.news.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.app.AlertDialog;
import android.util.Log;

import com.example.news.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {
    private static Dialog dialog = null;
    public static boolean isNetworkAvalible(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] net_info = connectivityManager.getAllNetworkInfo();

            if (net_info != null) {
                for (int i = 0; i < net_info.length; i++) {
                    if (net_info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void checkNetwork(final Activity activity) {
        if (!HttpUtils.isNetworkAvalible(activity)) {
            dialog = new AlertDialog.Builder(activity)
                    .setTitle("网络状态提示")
                    .setMessage("当前没有可以使用的网络，请设置网络！")
                    .setIcon(R.mipmap.app_icon)
                    .setCancelable(false)
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface di,
                                                    int whichButton) {
                                    //跳转到设置界面
//                                    activity.startActivityForResult(new Intent(
//                                                    Settings.ACTION_WIRELESS_SETTINGS),
//                                            0);
                                }
                            }).create();
            if (dialog.isShowing()) {
                Log.d("checkNetwork", "dialog is showing...");
                dialog.dismiss();
            } else {
                dialog.show();
            }


        }
        return;
    }


    public static String requestHttp(String url) {
        String responseData = null;
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Log.d("HttpUtils", "requestHttp: " + url);
            Response response = client.newCall(request).execute();
            responseData = response.body().string();
        } catch (IOException e) {
            Log.d("HttpUtils", "requestHttp: error at " + url);
            e.printStackTrace();
        }
        return responseData;
    }

    public static Bitmap decodeUriAsBitmapFromNet(String imgUrl) {
        URL fileUrl = null;
        Bitmap bitmap = null;

        try {
            fileUrl = new URL(imgUrl);

        } catch (MalformedURLException e) {
            e.printStackTrace();

        }
        try {
            HttpURLConnection conn = (HttpURLConnection) fileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
