package com.example.news.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.news.Adapter.NewsAdapter;
import com.example.news.Bean.News;
import com.example.news.R;
import com.example.news.Utils.HttpUtils;
import com.example.news.Utils.MyBitmapUtils;
import com.example.news.Utils.NewsManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainContentFragment extends Fragment {
    private String TAG = "MainContentFragment";
    private boolean initialized = false;
    private View view;
    private String category;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView newsView;
    private NewsAdapter newsAdapter;
    private List<News> newsList = new ArrayList<>();

    private Thread initThread;

    private MyBitmapUtils myBitmapUtils;

    public MainContentFragment() {}

    public MainContentFragment(String _category) {
        Log.d(TAG, "MainContentFragment: " + _category);
        category = _category;
        TAG += "_" + category;
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
        else {
//            initThread.setPriority(10);
            if (category.equals("推荐"))
                initRecommendation();
            else
                initNews();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        view = inflater.inflate(R.layout.fragement_main_content, container, false);

        newsView = view.findViewById(R.id.news_list_view);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        newsAdapter = new NewsAdapter(getContext(), category, newsList);
        newsView.setAdapter(newsAdapter);
        newsView.setLayoutManager(linearLayoutManager);
        if (category.equals("推荐")) {
            Log.d(TAG, "onCreateView: 推荐");
            newsView.addOnScrollListener(new EndLessOnScrollListener(linearLayoutManager) {
                @Override
                public void onLoad() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "onLoad: ");
                            List<News> loadList = new ArrayList<>();
                            int t = 0;
                            while (loadList.size() < 3 && t ++ < 3)
                                loadList.addAll(NewsManager.getInstance(getContext()).getLoadNews(
                                        3 - loadList.size(), "", ""));

                            synchronized (newsList) {
                                newsList.addAll(loadList);
                            }

                            for (int i = 0; i < loadList.size(); i++)
                                Log.d(TAG, "onLoad: " + i + " " + loadList.get(i).getNews_title());
                            newsList.addAll(loadList);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "onLoad: " + category + " notifyDataSetChanged");
                                    newsAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                }
            });
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    initRecommendation();
                }
            });
        }
        else {
            Log.d(TAG, "onCreateView: not 推荐");
            newsView.addOnScrollListener(new EndLessOnScrollListener(linearLayoutManager) {
                @Override
                public void onLoad() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "onLoad: ");
                            List<News> loadList = new ArrayList<>();
                            int t = 0;
                            while (loadList.size() < 3 && t ++ < 3)
                                loadList.addAll(NewsManager.getInstance(getContext()).getLoadNews(
                                        3 - loadList.size(), "", category));

                            synchronized (newsList) {
                                newsList.addAll(loadList);
                            }

                            for (int i = 0; i < loadList.size(); i++)
                                Log.d(TAG, "onLoad: " + i + " " + loadList.get(i).getNews_title());
                            newsList.addAll(loadList);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "onLoad: " + category + " notifyDataSetChanged");
                                    newsAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }).start();
                }
            });
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<News> refreshList = new ArrayList<News>();
//                            while (refreshList.size() < 1)
                            refreshList.addAll(NewsManager.getInstance(getContext()).getFreshNews(
                                    1, "", category));
                            Log.d(TAG, "onRefresh: " + refreshList);

                            synchronized (newsList) {
                                for (News news : refreshList)
                                    newsList.add(0, news);
                            }

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "onRefresh: " + category + " notifyDataSetChanged");
                                    newsAdapter.notifyDataSetChanged();
                                }
                            });
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }).start();
                }
            });
        }

        if (initialized == true) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "onLoad: " + category + " notifyDataSetChanged");
                    if (newsAdapter != null)
                        newsAdapter.notifyDataSetChanged();
                }
            });
            initialized = false;
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        initThread.setPriority(5);
    }

    private void initNews() {
        initThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "initNews> run: " + "start!");
                List<News> loadNewsList = new ArrayList<>();
                for (int t = 0; t < 5 && loadNewsList.size() < 10; t++)
                    if (category != "推荐" && category != "收藏")
                        loadNewsList.addAll(NewsManager.getInstance(getContext()).getLoadNews(
                                10 - loadNewsList.size(), "", category));
                    else
                        loadNewsList.addAll(NewsManager.getInstance(getContext()).getLoadNews(
                                10 - loadNewsList.size(), "", ""));
                synchronized(newsList) {
                    newsList.addAll(loadNewsList);
                }
                Log.d(TAG, "initNews> run: " + "finish:");
                Log.d(TAG, "initNews: loadNewsList Size: " + loadNewsList.size());
                Log.d(TAG, "initNews: newsList Size: " + newsList.size());
                Log.d(TAG, "initNews: newsAdapter Item: " + newsAdapter.getItemCount());
                for (int i = 0; i < loadNewsList.size(); i++) {
                    Log.d(TAG, "news: " + loadNewsList.get(i).getNews_title());
                }
                initialized = true;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "initNews: " + category + " notifyDataSetChanged");
                        if (newsAdapter != null)
                            newsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        try {
            initThread.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initRecommendation() {
        initThread = new Thread(new Runnable() {
            @Override
            public void run() {
                List<Pair<String, Float>> pairList = NewsManager.getInstance(getContext()).getKeyWords();
                pairList.sort(new Comparator<Pair<String, Float>>() {
                    @Override
                    public int compare(Pair<String, Float> a, Pair<String, Float> b) {
                        if (b.second - a.second > 0)
                            return 1;
                        if (b.second - a.second < 0)
                            return -1;
                        return 0;
                    }
                });

                newsList.clear();
                Log.d(TAG, "initRecommendation: start ");
                Log.d(TAG, "initRecommendation: get key words: " + pairList.toArray());
                if (pairList.size() == 0) {
                    initNews();
                    return;
                }

                final int key_cnt = 4;
                final int single_key_cnt = 3;
                final int single_time = 2;
                synchronized (newsList) {
                    for (int i = 0; i < (pairList.size() > key_cnt? key_cnt : pairList.size()); i++) {
                        int count = 0;
                        while (newsList.size() < single_key_cnt * i && count ++ < single_time) {
                            Log.d(TAG, "initRecommendation: run: " + pairList.get(i).first);
                            List<News> loadNewsList = NewsManager.getInstance(getContext()).getLoadNews(
                                    single_key_cnt * i - newsList.size(), pairList.get(i).first, "");
                            for (News news : loadNewsList) {
                                if (NewsManager.getInstance(getContext()).isVisited(news.getNews_url()))
                                    continue;
                                boolean flag = true;
                                for (News oldNews : newsList)
                                    if (news.getNews_url().equals(oldNews.getNews_url())
                                            || news.getNews_title().equals(oldNews.getNews_title())) {
                                        flag = false;
                                        break;
                                    }
                                if (flag)
                                    newsList.add(news);
                            }
                        }
                    }
                    Log.d(TAG, "initRecommendation: after load " + newsList.size());
                    if (newsList.size() < 10) {
                        newsList.addAll(NewsManager.getInstance(getContext()).getLoadNews(
                                10 - newsList.size(), "", ""));
                    }
                }

                swipeRefreshLayout.setRefreshing(false);
                initialized = true;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "onLoad: " + category + " notifyDataSetChanged");
                        newsAdapter.notifyDataSetChanged();
                    }
                });

            }
        });
        try {
            initThread.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyAdapter() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "onLoad: " + category + " notifyDataSetChanged");
                newsAdapter.notifyDataSetChanged();
            }
        });
    }
}
