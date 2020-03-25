package com.example.news.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.news.Fragment.CategorySettingFragment;
import com.example.news.Fragment.MainFragment;
import com.example.news.R;
import com.example.news.Utils.ApplicationUtil;
import com.example.news.Utils.MyDatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static boolean Day=true;
    final String TAG = "MainActivity";
    private MainFragment mainFragment;
    private CategorySettingFragment categorySettingFragment;
    private DayAndNight dan;
    private List<Fragment> fragmentList;

    private ImageView collectionButton;
    private ImageView ReadListButton;
    private ImageView NightButton;
    private TextView text_moon;
    private ImageView homeButton;
    private LinearLayout bottomLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragment();
        initView();

        ApplicationUtil.getInstance().addActivity(this);
    }

    private void initView() {
        final MainActivity pointer = this;
        text_moon = findViewById(R.id.text_night);
        NightButton = findViewById(R.id.img_moon);
        NightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment(dan);
            }
        });
        changeUI();
        collectionButton = findViewById(R.id.img_mine);
        collectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(pointer, CollectionActivity.class));
            }
        });
        ReadListButton = findViewById(R.id.img_setting);
        ReadListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(pointer, ReadActivity.class));
            }
        });
        homeButton = findViewById(R.id.img_main);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainFragment.notifyFragment();
                showFragment(mainFragment);
            }
        });
        bottomLayout = findViewById(R.id.bottom_layout);
    }

    private void initFragment() {
        fragmentList = new ArrayList<>();
        Log.d(TAG, "initFragment: start");

        mainFragment = MainFragment.getInstance(this, null);
        categorySettingFragment = new CategorySettingFragment();
        dan = new DayAndNight();

        addFragment(mainFragment);
        addFragment(categorySettingFragment);
        addFragment(dan);
        showFragment(mainFragment);
    }

    private void addFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (! fragment.isAdded()) {
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(R.id.main_content, fragment).commit();
            Log.d(TAG, "addFragment: start, with " + fragment.toString());
            fragmentList.add(fragment);
        }
    }

    private void showFragment(Fragment fragment) {
        Log.d(TAG, "showFragment: enter");

        for (Fragment frag : fragmentList) {
            if (frag != fragment) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(frag).commit();
            }
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.show(fragment).commit();
    }

    public List<String> getVisibleCategory() {
        return mainFragment.getVisibleCategory();
    }

    public void setVisibleCategory(List<String> currentCategoryList) {
        mainFragment.setCategory(currentCategoryList);
        showFragment(mainFragment);
    }
    public void openMainFragment(){
        Log.d(TAG, "openMainFragment: called");
        mainFragment.notifyFragment();
        showFragment(mainFragment);
    }


    public void openCategorySettingFragment() {
        showFragment(categorySettingFragment);
    }

    @Override
    protected void onDestroy() {
        MyDatabaseHelper.getInstance(null).clearKeyWords();
        super.onDestroy();
    }

    public void changeUI(){
        if(Day){
            NightButton.setImageResource(R.drawable.sun);
            text_moon.setText("日间");
        }
        else{
            NightButton.setImageResource(R.drawable.moon);
            text_moon.setText("夜间");
        }
//        mainFragment;
    }
}

