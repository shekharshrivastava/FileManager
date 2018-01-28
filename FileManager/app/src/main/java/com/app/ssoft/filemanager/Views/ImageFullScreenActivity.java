package com.app.ssoft.filemanager.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;

public class ImageFullScreenActivity extends AppCompatActivity {

    private ImageView imageView;
    private String imageFile;
    private File image;
    public boolean isDeleted = false;
    private String imagePath;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {



        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_share:
                    Utils.shareImage(ImageFullScreenActivity.this,imageFile);
                    return true;
                case R.id.navigation_delete:
                    isDeleted = Utils.delete(image);
                   if(isDeleted){
                       Toast.makeText(ImageFullScreenActivity.this,"File deleted successfully",Toast.LENGTH_SHORT).show();
                       finish();
                   }else{
                       Toast.makeText(ImageFullScreenActivity.this,"Error deleting file",Toast.LENGTH_SHORT).show();
                   }
                    return true;
                case R.id.navigation_info:
                    Intent intent = new Intent(ImageFullScreenActivity.this,InfoActivity.class);
                    intent.putExtra("imageFile",imageFile);
                    intent.putExtra("imagePath",imagePath);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        imageView = findViewById(R.id.imageView);
        imageView.setOnTouchListener( new ImageMatrixTouchHandler(this));
        Intent intent = getIntent();
         imageFile = intent.getStringExtra("imgFile");
         image = new File(imageFile);
        Glide.with(this)
                .load(image)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.doc_folder)
                .into(imageView);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        Utils.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
