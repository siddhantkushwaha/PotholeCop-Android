package com.potholecop.androidapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.potholecop.androidapp.R;
import com.potholecop.androidapp.pojo.PotholeData;

import java.util.ArrayList;

public class FeedGridAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<PotholeData> potholeDataArrayList;

    public FeedGridAdapter(Context context, ArrayList<PotholeData> potholeDataArrayList) {
        this.context = context;
        this.potholeDataArrayList = potholeDataArrayList;
    }

    @Override
    public int getCount() {
        return potholeDataArrayList.size();
    }

    @Override
    public PotholeData getItem(int position) {
        return potholeDataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.layout_grid_item, parent, false);


        return listItem;
    }
}
