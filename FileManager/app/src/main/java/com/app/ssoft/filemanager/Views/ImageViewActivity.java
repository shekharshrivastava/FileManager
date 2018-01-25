package com.app.ssoft.filemanager.Views;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.app.ssoft.filemanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ImageViewActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ImageView imageViewer;
    private ViewPager mPager;
    public static int imagePos = 0;
    private ArrayList<String> runningactivities;
    private int imageSelectedPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        runningactivities = new ArrayList<String>();
        ActivityManager activityManager = (ActivityManager)getBaseContext().getSystemService (Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (int i1 = 0; i1 < services.size(); i1++) {
            runningactivities.add(0,services.get(i1).topActivity.toString());
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Intent imageIntent = getIntent();
        ArrayList<String> imageArray = imageIntent.getStringArrayListExtra("imgPath");
        String imageFile = imageIntent.getStringExtra("imgFile");
        String imageName = imageIntent.getStringExtra("imageName");
         imageSelectedPos = imageIntent.getIntExtra("position", 0);
         imagePos = 0;
        File imgFile = new File(imageFile);

        toolbar.setTitle(imageName);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOnPageChangeListener(this);
     /*   CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        mPager.setAdapter(new MyAdapter(ImageViewActivity.this, imageArray, imageSelectedPos));
        indicator.setViewPager(mPager);*/


        /*if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView myImage = findViewById(R.id.imageViewer);
            myImage.setImageBitmap(myBitmap);

        }*/
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
       /* if(runningactivities.contains("ComponentInfo{com.app.ssoft.filemanager/com.app.ssoft.filemanager.Views.ImageViewActivity}")==true) {

            imagePos = position;
        }*/
    }

    @Override
    public void onPageSelected(int position) {
        position = imageSelectedPos;

        if (imageSelectedPos > position) {
            if(position != 0) {//User Move to left}
                imagePos = (position - 1);
                imageSelectedPos = imagePos;
            }else{
                imagePos = 0;
            }
        } else if (imageSelectedPos < position) {
                imagePos = (position+1);
                imageSelectedPos = imagePos;

            /*if (runningactivities.contains("ComponentInfo{com.app.ssoft.filemanager/com.app.ssoft.filemanager.Views.ImageViewActivity}") == true) {

                imagePos = position;
            }*/
        }else{

            imagePos= position;
            imageSelectedPos = imagePos;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
