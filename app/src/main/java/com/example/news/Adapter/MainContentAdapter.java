package com.example.news.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.news.Fragment.MainContentFragment;

import java.util.List;

/**
 * Created by ts on 18-8-22.
 */

public class MainContentAdapter extends FragmentPagerAdapter {
    private List<MainContentFragment> fragmentList;
    private List<String> titleList;

    public MainContentAdapter(FragmentManager fm, List<MainContentFragment> fragmentList, List<String> titleList) {
        super(fm);
        this.fragmentList = fragmentList;
        this.titleList = titleList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position % titleList.size());
    }

}
