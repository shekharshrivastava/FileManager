package com.app.ssoft.filemanager.Views;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.app.ssoft.filemanager.R;

import java.util.ArrayList;

public class ImagePreviewActivity extends AppCompatActivity  {

    private int imageSelectedPos;
    private ViewPager viewPager;
    public static  int imagePos;
    private int focusedPos;
    private ArrayList<String> imageArray;
    private String filePath;
    private String type;

    private String imageFile;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        Intent imageIntent = getIntent();
        imageArray = imageIntent.getStringArrayListExtra("imgPath");
        imageFile = imageIntent.getStringExtra("imgFile");
        String imageName = imageIntent.getStringExtra("imageName");
        imageSelectedPos = imageIntent.getIntExtra("position", 0);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, imageArray,imageSelectedPos );
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(imageSelectedPos);

    }


   /* private void updateSelectedPosition(int imageSelectedPos) {
        if (imageSelectedPos == -1) {
            return;
        }
        if (imagesList != null && imagesList.size() > 0) {
            filePath = imageArray.get(imageSelectedPos);
        }

    }

    private int getCategoryPos(String category) {
        imagePos=imagesList.indexOf(category);
        return imagePos;
    }*/
}

