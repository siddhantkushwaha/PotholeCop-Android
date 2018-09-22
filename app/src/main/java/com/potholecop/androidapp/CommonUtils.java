package com.potholecop.androidapp;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

public class CommonUtils {

    public static void loadImage(Context context, String uri, RequestOptions requestOptions, ImageView imageView) {

        GlideApp.with(context.getApplicationContext())
                .load(uri)
                .apply(requestOptions)
                .into(imageView);
    }
}
