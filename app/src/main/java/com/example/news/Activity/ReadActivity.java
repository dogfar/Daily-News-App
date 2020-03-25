package com.example.news.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.Adapter.NewsAdapter;
import com.example.news.Bean.News;
import com.example.news.R;
import com.example.news.Utils.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class ReadActivity extends AppCompatActivity {

    private String TAG = "ReadActivity";
    private List<News> newsList = new ArrayList<>();

    private RecyclerView readlist;

    private NewsAdapter adapter;

    private MyDatabaseHelper helper;

    private TextView title;
    private View setview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        helper = MyDatabaseHelper.getInstance(this);
        initView();
        initNews();
    }
    private void initNews() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                newsList.clear();
                newsList.addAll(helper.getListedReadNews());
                if (newsList.size() == 0)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "阅读列表为空！", Toast.LENGTH_SHORT).show();
                        }
                    });
                Log.d(TAG, "run: get newsList size: " + newsList.size());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "notifyDataSetChanged");
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
    private void initView() {
        setview = findViewById(R.id.setview);
        title=findViewById(R.id.title);
        readlist = findViewById(R.id.listview_read);
        adapter = new NewsAdapter(this, "已读", newsList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        readlist.setAdapter(adapter);
        readlist.setLayoutManager(linearLayoutManager);

        if(!MainActivity.Day){
            Log.d(TAG, "initView: night!");
            title.setBackgroundColor(Color.parseColor("#5e5e5e"));
            title.setTextColor(Color.WHITE);
            readlist.setBackgroundColor(Color.parseColor("#5e5e5e"));
            setview.setBackgroundColor(Color.WHITE);
        }
        else {
            Log.d(TAG, "initView: day!");
            title.setBackgroundColor(Color.parseColor("#FFFFFF"));
            title.setTextColor(Color.BLACK);
            readlist.setBackgroundColor(Color.parseColor("#FFFFFF"));
            setview.setBackgroundColor(Color.BLACK);
        }
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "notifyDataSetChanged");
                    adapter.notifyDataSetChanged();
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
