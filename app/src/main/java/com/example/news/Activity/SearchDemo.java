package com.example.news.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.example.news.Fragment.SearchResultFragment;
import com.example.news.R;
import com.example.news.Search.ICallBack;
import com.example.news.Search.SearchView;
import com.example.news.Search.bCallBack;

public class SearchDemo extends AppCompatActivity {

    private SearchView searchView;
    private RelativeLayout rll;
    SearchResultFragment srf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        searchView = findViewById(R.id.search_view);
        rll = findViewById(R.id.search_layout);

        searchView.setOnClickSearch(new ICallBack() {
            @Override
            public void SearchAciton(String string) {
                Intent intent = new Intent(SearchDemo.this,ShowResultActivity.class);
                intent.putExtra("keyword",string);
                startActivity(intent);
            }
        });

        // 5. 设置点击返回按键后的操作（通过回调接口）
        searchView.setOnClickBack(new bCallBack() {
            @Override
            public void BackAction() {
                finish();
            }
        });

        if(!MainActivity.Day){
            searchView.setBackgroundColor(Color.parseColor("#5e5e5e"));
            rll.setBackgroundColor(Color.parseColor("#5e5e5e"));
        }

    }

}
