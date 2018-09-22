package com.potholecop.androidapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.potholecop.androidapp.R;
import com.potholecop.androidapp.adapter.FeedGridAdapter;
import com.potholecop.androidapp.pojo.PotholeData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FeedFragment extends Fragment {

    @BindView(R.id.grid)
    GridView grid;
    Unbinder unbinder;

    private Context context;
    private ArrayList<PotholeData> potholeDataArrayList;
    private FeedGridAdapter feedGridAdapter;

    public static FeedFragment newInstance() {

        FeedFragment fragment = new FeedFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        unbinder = ButterKnife.bind(this, view);

        context = view.getContext();
        potholeDataArrayList = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            potholeDataArrayList.add(new PotholeData());
        feedGridAdapter = new FeedGridAdapter(context, potholeDataArrayList);
        grid.setAdapter(feedGridAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
