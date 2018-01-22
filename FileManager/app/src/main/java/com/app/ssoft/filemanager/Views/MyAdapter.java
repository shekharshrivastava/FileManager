package com.app.ssoft.filemanager.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Shekahar.Shrivastava on 22-Jan-18.
 */

public class MyAdapter extends PagerAdapter {

    private final int pos;
    private ArrayList<String> images;
    private LayoutInflater inflater;
    private Context context;

    public MyAdapter(Context context, ArrayList<String> images, int position) {
        this.context = context;
        this.images = images;
        this.pos = position;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        if (ImageViewActivity.imagePos != 0) {
            position = ImageViewActivity.imagePos;
        } else {
//            position = pos;
        }
        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        ImageView myImage = (ImageView) myImageLayout
                .findViewById(R.id.image);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Utils.setFileImageType(context, new File(images.get(position + 1))).compress(Bitmap.CompressFormat.PNG, 100, stream);
        Glide.with(context)
                .load(stream.toByteArray())
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.doc_folder)
                .into(myImage);
        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
