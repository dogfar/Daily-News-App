package com.example.news.Fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.news.Activity.MainActivity;
import com.example.news.Adapter.DragAdapter;
import com.example.news.Adapter.OtherAdapter;
import com.example.news.Drag.ChannelItem;
import com.example.news.Drag.DragGrid;
import com.example.news.Drag.OtherGridView;
import com.example.news.R;

import java.util.ArrayList;
import java.util.List;

public class CategorySettingFragment extends Fragment {
    final String TAG = "CategoryActivity";

    String[] total_type= {"推荐", "科技", "娱乐", "军事", "教育", "文化", "健康", "财经", "体育", "汽车", "社会"};

    private List<String> usage_list = new ArrayList<>();
    private Button finish_button;
    private DragGrid userGridView;
    private OtherGridView otherGridView;
    DragAdapter userAdapter;
    OtherAdapter otherAdapter;
    ArrayList<ChannelItem> otherChannelList = new ArrayList<ChannelItem>();
    ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();
    boolean isMove = false;

    private View view;
    private TextView operator;
    private TextView tvZhuan;

    private boolean isModify = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.subscribe_category, container, false);
        usage_list.clear();
        usage_list.addAll(((MainActivity)(getActivity())).getVisibleCategory());
        initViews();
        initDatas();
        return view;
    }

    private void initDatas() {
        Log.d(TAG, "initDatas: ");
        for (int i = 0; i < usage_list.size();i ++){
            ChannelItem item1 = new ChannelItem();
            item1.setName(usage_list.get(i));
            userChannelList.add(item1);
        }
        for (int i = 0; i < total_type.length; i++){
            if(!(usage_list.contains(total_type[i]))){
                ChannelItem item1 = new ChannelItem();
                item1.setName(total_type[i]);
                otherChannelList.add(item1);
            }
        }

        if (otherChannelList.size() == 0) {
            tvZhuan.setVisibility(View.VISIBLE);
        } else {
            tvZhuan.setVisibility(View.GONE);
        }

        userAdapter = new DragAdapter(getContext(), userChannelList);
        userGridView.setAdapter(userAdapter);


        otherAdapter = new OtherAdapter(getContext(), otherChannelList);
        otherGridView.setAdapter(this.otherAdapter);
        //设置GRIDVIEW的ITEM的点击监听
        otherGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long l) {
                if (isMove) {
                    return;
                }
                final ImageView moveImageView = getView(view);
                if (moveImageView != null) {
                    TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                    final int[] startLocation = new int[2];
                    newTextView.getLocationInWindow(startLocation);
                    final ChannelItem channel = ((OtherAdapter) parent.getAdapter()).getItem(position);
                    userAdapter.setVisible(false);
                    //添加到最后一个
                    userAdapter.addItem(channel);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            try {
                                int[] endLocation = new int[2];
                                //获取终点的坐标
                                userGridView.getChildAt(userGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                MoveAnim(moveImageView, startLocation, endLocation, channel, otherGridView);
                                otherAdapter.setRemove(position);
                            } catch (Exception localException) {
                            }
                        }
                    }, 50L);

                }
                userAdapter.notifyDataSetChanged();

                //由于此时列表还没有更新,所以在size==1的时候进行判断
                if (otherChannelList.size() == 1) {
                    tvZhuan.setVisibility(View.VISIBLE);
                }
            }
        });
        userGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long l) {
                if (isMove) {
                    return;
                }
                if (!isModify) {
                    return;
                }
                if (position != 0) {
                    final ImageView moveImageView = getView(view);
                    if (moveImageView != null) {
                        TextView newTextView = (TextView) view.findViewById(R.id.text_item);
                        final int[] startLocation = new int[2];
                        newTextView.getLocationInWindow(startLocation);
                        final ChannelItem channel = ((DragAdapter) parent.getAdapter()).getItem(position);//获取点击的频道内容
                        otherAdapter.setVisible(false);
                        //添加到最后一个
                        otherAdapter.addItem(channel);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                try {
                                    int[] endLocation = new int[2];
                                    //获取终点的坐标
                                    otherGridView.getChildAt(otherGridView.getLastVisiblePosition()).getLocationInWindow(endLocation);
                                    MoveAnim(moveImageView, startLocation, endLocation, channel, userGridView);
                                    userAdapter.setRemove(position);
                                } catch (Exception localException) {
                                }
                            }
                        }, 50L);
                    }
                }
                //由于此时列表还没有更新,所以在size==1的时候进行判断
                if (otherChannelList.size() == 1) {
                    tvZhuan.setVisibility(View.GONE);
                }
            }
        });
        operator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isModify) {
                    isModify = true;
                    userGridView.setIsMovelf(isModify);
                    userAdapter.setShowFlag(true);
                    operator.setText("完成");
                } else {
                    isModify = false;
                    userGridView.setIsMovelf(isModify);
                    userAdapter.setShowFlag(false);
                    operator.setText("编辑");
                }

                userAdapter.notifyDataSetChanged();
            }
        });
        finish_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usage_list.clear();
                for(int i=0;i<userChannelList.size();i++){
                    String name=userChannelList.get(i).getName();
                    usage_list.add(name);
                }
                ((MainActivity)(getActivity())).setVisibleCategory(new ArrayList<>(usage_list));
            }
        });

    }


    private void initViews() {
        operator = view.findViewById(R.id.operator);
        userGridView = view.findViewById(R.id.userGridView);
        otherGridView =  view.findViewById(R.id.otherGridView);
        tvZhuan = view.findViewById(R.id.tv_zhuan);
        finish_button = view.findViewById(R.id.finish_button);
    }


    /**
     * 获取点击的Item的对应View，
     *
     * @param view
     * @return
     */
    private ImageView getView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(getContext());
        iv.setImageBitmap(cache);
        return iv;
    }


    /**
     * 点击ITEM移动动画
     *
     * @param moveView
     * @param startLocation
     * @param endLocation
     * @param moveChannel
     * @param clickGridView
     */
    private void MoveAnim(View moveView, int[] startLocation, int[] endLocation, final ChannelItem moveChannel,
                          final GridView clickGridView) {
        int[] initLocation = new int[2];
        //获取传递过来的VIEW的坐标
        moveView.getLocationInWindow(initLocation);
        //得到要移动的VIEW,并放入对应的容器中
        final ViewGroup moveViewGroup = getMoveViewGroup();
        final View mMoveView = getMoveView(moveViewGroup, moveView, initLocation);
        //创建移动动画
        TranslateAnimation moveAnimation = new TranslateAnimation(
                startLocation[0], endLocation[0], startLocation[1],
                endLocation[1]);
        moveAnimation.setDuration(300L);//动画时间
        //动画配置
        AnimationSet moveAnimationSet = new AnimationSet(true);
        moveAnimationSet.setFillAfter(false);//动画效果执行完毕后，View对象不保留在终止的位置
        moveAnimationSet.addAnimation(moveAnimation);
        mMoveView.startAnimation(moveAnimationSet);
        moveAnimationSet.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                isMove = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                moveViewGroup.removeView(mMoveView);
                // instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
                if (clickGridView instanceof DragGrid) {
                    otherAdapter.setVisible(true);
                    otherAdapter.notifyDataSetChanged();
                    userAdapter.remove();
                } else {
                    userAdapter.setVisible(true);
                    userAdapter.notifyDataSetChanged();
                    otherAdapter.remove();
                }
                isMove = false;
            }
        });
    }

    /**
     * 创建移动的ITEM对应的ViewGroup布局容器
     */
    private ViewGroup getMoveViewGroup() {
        ViewGroup moveViewGroup = (ViewGroup) getActivity().getWindow().getDecorView();
        LinearLayout moveLinearLayout = new LinearLayout(getContext());
        moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        moveViewGroup.addView(moveLinearLayout);
        return moveLinearLayout;
    }

    /**
     * 获取移动的VIEW，放入对应ViewGroup布局容器
     *
     * @param viewGroup
     * @param view
     * @param initLocation
     * @return
     */
    private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
        int x = initLocation[0];
        int y = initLocation[1];
        viewGroup.addView(view);
        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mLayoutParams.leftMargin = x;
        mLayoutParams.topMargin = y;
        view.setLayoutParams(mLayoutParams);
        return view;
    }
}