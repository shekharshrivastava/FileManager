package com.app.ssoft.filemanager.Views;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

import com.app.ssoft.filemanager.R;

public class ImageFilterActivity extends AppCompatActivity {

    private Cursor cursor;
    private int columnIndex;
    private GridView listView;
    private AllImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_filter);

        String[] projection = {MediaStore.Images.Thumbnails._ID};
        cursor = managedQuery(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Thumbnails._ID + "");
        columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);

        listView = (GridView) findViewById(R.id.GV_Images);
        AllImageAdapter adapter = new AllImageAdapter(ImageFilterActivity.this,cursor,columnIndex);
        listView.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }
}
