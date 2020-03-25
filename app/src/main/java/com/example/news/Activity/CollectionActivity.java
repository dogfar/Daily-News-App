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

public class CollectionActivity extends AppCompatActivity {
    private String TAG = "CollectionActivity";
    private List<News> newsList = new ArrayList<>();

    private RecyclerView collection;

    private NewsAdapter adapter;

    private MyDatabaseHelper helper;

    private TextView title;
    private View intern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        helper = MyDatabaseHelper.getInstance(this);
        initView();
        if(!MainActivity.Day){
            title.setBackgroundColor(Color.parseColor("#5e5e5e"));
            title.setTextColor(Color.WHITE);
            collection.setBackgroundColor(Color.parseColor("#5e5e5e"));
            intern.setBackgroundColor(Color.WHITE);
        }
        initNews();
    }


    private void initNews() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                newsList.clear();
                newsList.addAll(helper.getCollection());
                if (newsList.size() == 0)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "收藏夹为空！", Toast.LENGTH_SHORT).show();
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
        collection = findViewById(R.id.listview_collection);
        adapter = new NewsAdapter(this, "收藏", newsList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        collection.setAdapter(adapter);
        collection.setLayoutManager(linearLayoutManager);

        title = findViewById(R.id.title);
        intern = findViewById(R.id.intern);
    }
}
