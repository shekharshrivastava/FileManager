package com.app.ssoft.filemanager.Views;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.ssoft.filemanager.R;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Shekahar.Shrivastava on 10-Apr-18.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private int pos = 0;
    private ArrayList<String> images;
    private Context context;
    private LayoutInflater layoutInflater;

    public ViewPagerAdapter(Context context, ArrayList<String> images, int position) {
        this.context = context;
        this.images = images;
        this.pos = position;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setOnTouchListener(new ImageMatrixTouchHandler(context));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        File file = new File(images.get(position));
        file.getName();
//        Utils.setFileImageType(context, new File(images.get(position))).compress(Bitmap.CompressFormat.JPEG, 100, stream);
        Glide.with(context)
                .load(file)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .error(R.drawable.placeholder)
                .into(imageView);
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }

}