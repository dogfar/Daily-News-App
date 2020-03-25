package com.example.news.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.news.Activity.MainActivity;
import com.example.news.Activity.SearchDemo;
import com.example.news.Adapter.FragmentAdapter;
import com.example.news.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    private final String TAG = "MainFragment";
    private View view;

    private TabLayout tabLayout;
    private ImageButton tabMore;
    private ViewPager viewPager;

    private List<String> allCategoryList;
    private List<String> titleList;
    private List<MainContentFragment> visibleFragmentList;
    private List<MainContentFragment> allFragmentList;

    private FragmentAdapter fragmentAdapter;
    static private MainFragment instance = null;

    private ImageButton search_button;
    private LinearLayout searchLayout;

    private MainFragment() {
        initFragmentList();
    }

    static public MainFragment getInstance(Context context, List<String> currentCategoryList) {
        if (instance == null)
            instance = new MainFragment();
        else {
            instance.setCategory(currentCategoryList);
//            instance.onDetach();
//            instance.onAttach(context);
        }
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_main, container, false);
        initView();

        fragmentAdapter = new FragmentAdapter(getActivity().getSupportFragmentManager(),
                visibleFragmentList, titleList);
        viewPager.setAdapter(fragmentAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)(getActivity())).openCategorySettingFragment();
            }
        });

        return view;
    }

    private void initView() {
        search_button = view.findViewById(R.id.search_button);
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchDemo.class));
            }
        });
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        tabMore = view.findViewById(R.id.more_tab);
        searchLayout = view.findViewById(R.id.search_layout);
    }

    private void initFragmentList() {
        allFragmentList = new ArrayList<>();
        allCategoryList = new ArrayList<>();

        allCategoryList.add("推荐");
        allCategoryList.add("娱乐");
        allCategoryList.add("军事");
        allCategoryList.add("教育");
        allCategoryList.add("文化");
        allCategoryList.add("健康");
        allCategoryList.add("财经");
        allCategoryList.add("体育");
        allCategoryList.add("汽车");
        allCategoryList.add("科技");
        allCategoryList.add("社会");
        for (String category : allCategoryList)
            allFragmentList.add(new MainContentFragment(category));

        titleList = new ArrayList<>();
        visibleFragmentList = new ArrayList<>();

        titleList.addAll(allCategoryList);
        visibleFragmentList.addAll(allFragmentList);
    }

    public void setCategory(List<String> currentCategoryList) {
        Log.d(TAG, "setCategory: " + currentCategoryList);
        titleList.clear();
        visibleFragmentList.clear();
        if (currentCategoryList == null) {
            titleList.addAll(allCategoryList);
            visibleFragmentList.addAll(allFragmentList);
        }
        else {
            for (String category : currentCategoryList) {
                titleList.add(category);
                for (int i = 0; i < allCategoryList.size(); i++)
                    if (allCategoryList.get(i) == category) {
                        visibleFragmentList.add(allFragmentList.get(i));
                        break;
                    }
            }
        }
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentAdapter.notifyDataSetChanged();
                    viewPager.setCurrentItem(0);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void notifyFragment(){
        if(!MainActivity.Day){
            tabLayout.setBackgroundColor(Color.parseColor("#5e5e5e"));
            tabLayout.setTabTextColors(Color.parseColor("#FFFFFF"), Color.parseColor("#0000FF"));
            tabMore.setBackgroundColor(Color.parseColor("#5e5e5e"));
            searchLayout.setBackgroundColor(Color.parseColor("#5e5e5e"));
        }
        else{
            tabLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            tabLayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#0000FF"));
            tabMore.setBackgroundColor(Color.parseColor("#ffffff"));
            searchLayout.setBackgroundColor(Color.parseColor("#0000FF"));
        }
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    fragmentAdapter.notifyDataSetChanged();
                    for (MainContentFragment fragment : visibleFragmentList)
                        fragment.notifyAdapter();
                    viewPager.setCurrentItem(0);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<String> getVisibleCategory() {
        return titleList;
    }

}
