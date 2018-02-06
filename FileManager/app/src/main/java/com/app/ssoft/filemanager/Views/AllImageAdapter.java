package com.app.ssoft.filemanager.Views;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.app.ssoft.filemanager.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by Shekahar.Shrivastava on 02-Feb-18.
 */

public class AllImageAdapter extends BaseAdapter {

    Context c;
    Cursor cur;
    ImageView imageView;
    int columnIndex;
    public AllImageAdapter(Context c, Cursor cur, int columnIndex)
    {
        this.c = c;
        this.cur = cur;
        this.columnIndex = columnIndex;
    }

    @Override
    public int getCount() {
        return cur.getCount();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Set The Image in Image View.
        imageView = new ImageView(c);
        cur.moveToPosition(position);
        int imageID = cur.getInt(columnIndex);

        Glide.with(c)
                .load(Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.picture_folder)
                .error(R.drawable.doc_folder)
                .into(imageView);
       /* imageView.setImageURI(Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + imageID));

        imageView.setLayoutParams(new GridView.LayoutParams(330, 330));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(4, 4, 4, 4);*/
        //convertView.setTag(imageView);
        return convertView;
    }

}