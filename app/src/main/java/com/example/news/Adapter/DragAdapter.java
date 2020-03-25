package com.example.news.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.news.Drag.ChannelItem;
import com.example.news.R;

import java.util.List;

public class DragAdapter extends BaseAdapter {
    private boolean isItemShow = false;
    private Context context;
    private int holdPosition;
    private boolean isChanged = false;
    boolean isVisible = true;
    public List<ChannelItem> channelList;
    private TextView item_text;
    public int remove_position = -1;
    private boolean showAble = false;

    public DragAdapter(Context context, List<ChannelItem> channelList) {
        this.context = context;
        this.channelList = channelList;

    }

    public void setShowFlag(boolean able) {
        showAble = able;
    }

    @Override
    public int getCount() {
        return channelList == null ? 0 : channelList.size();
    }

    @Override
    public ChannelItem getItem(int position) {
        if (channelList != null && channelList.size() != 0) {
            return channelList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.subscribe_category_item, null);
        item_text = (TextView) view.findViewById(R.id.text_item);
        ImageView image = (ImageView) view.findViewById(R.id.icon_new);
        ChannelItem channel = getItem(position);
        item_text.setText(channel.getName());

        if (position == 0) {
            item_text.setTextColor(context.getResources().getColor(R.color.colorGray));
            item_text.setEnabled(false);
        }
        if (isChanged && (position == holdPosition) && !isItemShow) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
            isChanged = false;
        }
        if (!isVisible && (position == -1 + channelList.size())) {
            item_text.setText("");
            item_text.setSelected(true);
            item_text.setEnabled(true);
        }
        if (remove_position == position) {
            item_text.setText("");
        }

        if (showAble) {
            if (position == 0) {
                image.setVisibility(View.GONE);
            } else {
                image.setVisibility(View.VISIBLE);
            }
        } else {
            image.setVisibility(View.GONE);
        }

        return view;
    }

    public void addItem(ChannelItem channel) {
        channelList.add(channel);
        notifyDataSetChanged();
    }

    public void exchange(int dragPostion, int dropPostion) {
        holdPosition = dropPostion;
        ChannelItem dragItem = getItem(dragPostion);
        if (dragPostion < dropPostion) {
            channelList.add(dropPostion + 1, dragItem);
            channelList.remove(dragPostion);
        } else {
            channelList.add(dropPostion, dragItem);
            channelList.remove(dragPostion + 1);
        }

        isChanged = true;
        notifyDataSetChanged();
    }


    public void setRemove(int position) {
        remove_position = position;
        notifyDataSetChanged();
    }

    public void remove() {
        channelList.remove(remove_position);
        remove_position = -1;
        notifyDataSetChanged();
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void setShowDropItem(boolean show) {
        isItemShow = show;
    }
}
