package com.example.news.Fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.news.Activity.MainActivity;
import com.example.news.Adapter.NewsAdapter;
import com.example.news.Bean.News;
import com.example.news.R;
import com.example.news.Utils.HttpUtils;
import com.example.news.Utils.MyBitmapUtils;
import com.example.news.Utils.NewsManager;

import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment{
    private String TAG = "SearchResultFragment";
    private View view;
    private String keyword;
    private RecyclerView newsView;
    private NewsAdapter newsAdapter;
    private List<News> newsList = new ArrayList<>();
    private int cnt=0;
    private boolean ifhave=false;
    private Thread initThread;
    private MyBitmapUtils myBitmapUtils;

    public SearchResultFragment(String _keyword) {
        Log.d(TAG, "SearchContentFragment: " + _keyword);
        keyword = _keyword;
        TAG += "_" + keyword;
    }

    public abstract class EndLessOnScrollListener extends RecyclerView.OnScrollListener {
        private LinearLayoutManager mLinearLayoutManager;
        private int totalItemCount;
        private int previousTotal = 0;
        private int visibleItemCount;
        private int firstVisibleItem;
        private boolean loading = true;

        public EndLessOnScrollListener(LinearLayoutManager linearLayoutManager) {
            this.mLinearLayoutManager = linearLayoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = mLinearLayoutManager.getItemCount();
            firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
            if(loading){
                if(totalItemCount > previousTotal){
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            Log.d(TAG, "onScrolled: firstVisibleItem:" + firstVisibleItem +
                    " visibleItemCount:" + visibleItemCount + " totalItemCount: " + totalItemCount + " loading: " + loading);
            if (! loading && totalItemCount - visibleItemCount <= firstVisibleItem){
                onLoad();
                loading = true;
            }
        }

        public abstract void onLoad();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myBitmapUtils = new MyBitmapUtils(getContext());
        if (!HttpUtils.isNetworkAvalible(getContext()))
            Toast.makeText(getContext(),"当前没有可以使用的网络，请检查网络设置！",Toast.LENGTH_SHORT).show();
        else
            initNews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragment_search_result, container, false);

        newsView = view.findViewById(R.id.result_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        newsAdapter = new NewsAdapter(getContext(), "NULL", newsList);
        newsView.setAdapter(newsAdapter);
        newsView.setLayoutManager(linearLayoutManager);
        newsView.addOnScrollListener(new EndLessOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoad() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onLoad: ");
                        List<News> loadList = new ArrayList<>();
                        for (int times = 0; loadList.size() < 3 && times < 5; times ++) {
                            loadList.addAll(NewsManager.getInstance(getContext()).getLoadNews(
                                    3 - loadList.size(), keyword, ""));
                        }

                        for (int i = 0; i < loadList.size(); i++)
                            Log.d(TAG, "onLoad: " + i + " " + loadList.get(i).getNews_title());

                        synchronized(newsList) {
                            for (int i = 0; i < loadList.size(); i++)
                                newsList.add(loadList.get(i));
                        }

                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "onLoad: " + keyword + " notifyDataSetChanged");
                                    newsAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
//        initThread.setPriority(10);
        if(!MainActivity.Day){
            newsView.setBackgroundColor(Color.parseColor("#5e5e5e"));
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        initThread.setPriority(5);
    }


    private void initNews() {
//        final Context context = getContext();
        initThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "initNews> run: " + "start!");
                List<News> loadNewsList = new ArrayList<>();

//                for (int times = 0; times < 10 && loadNewsList.size() < 10; times++)
                loadNewsList.addAll(NewsManager.getInstance(getContext()).getSearchNews(
                        10, keyword, ""));
                synchronized(newsList) {
                    newsList.addAll(loadNewsList);
                }

                Log.d(TAG, "initNews> run: " + "finish:");
                Log.d(TAG, "initNews: loadNewsList Size: " + loadNewsList.size());
                Log.d(TAG, "initNews: newsList Size: " + newsList.size());
                Log.d(TAG, "initNews: newsAdapter Item: " + newsAdapter.getItemCount());
                for (int i = 0; i < loadNewsList.size(); i++)
                    Log.d(TAG, "news: " + loadNewsList.get(i).getNews_title());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onLoad: " + keyword + " notifyDataSetChanged");
                        if (newsAdapter != null)
                            newsAdapter.notifyDataSetChanged();
                        if(newsList.size() == 0) {
                            Toast.makeText(getContext(),"没有搜索到任何结果！",Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        initThread.start();
    }
}

