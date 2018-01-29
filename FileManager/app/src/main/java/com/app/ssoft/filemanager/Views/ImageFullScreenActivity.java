package com.app.ssoft.filemanager.Views;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;

public class ImageFullScreenActivity extends AppCompatActivity {

    private ImageView imageView;
    private String imageFile;
    private File image;
    public  boolean isDeleted = false;
    private String imagePath;
    private Bitmap bitmap;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = findViewById(R.id.imageView);
        imageView.setOnTouchListener(new ImageMatrixTouchHandler(this));
         intent = getIntent();
        imageFile = intent.getStringExtra("imgFile");
        String imageName = intent.getStringExtra("imageName");
        getSupportActionBar().setTitle(imageName);

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

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info_menu, menu);
        return true;
    }


  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.use_as:
                setPhoneWallpaper(bitmap);

        }
    }*/

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {


        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_share:
                    Utils.shareImage(ImageFullScreenActivity.this, imageFile);
                    return true;
                case R.id.navigation_delete:
                    isDeleted = Utils.delete(image);
                    if (isDeleted) {
                        intent.putExtra("isDelete" ,isDeleted);
                        intent.putExtra("deletedFile",imageFile);
                        setResult(1,intent);
                        Toast.makeText(ImageFullScreenActivity.this, "File deleted successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ImageFullScreenActivity.this, "Error deleting file", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                case R.id.navigation_info:
                    Intent intent = new Intent(ImageFullScreenActivity.this, InfoActivity.class);
                    intent.putExtra("imageFile", imageFile);
                    intent.putExtra("imagePath", imagePath);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    public void setPhoneWallpaper() {
        WallpaperManager myWallpaperManager = WallpaperManager
                .getInstance(ImageFullScreenActivity.this);

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(imageFile, options);
            // Change the current system wallpaper
            myWallpaperManager.setBitmap(bitmap);

            // Show a toast message on successful change


        } catch (IOException e) {
            // TODO Auto-generated catch block
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.use_as:
                new SetwallpaperAsyncTask().execute();
                break;
            case R.id.print:
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    bitmap = BitmapFactory.decodeFile(imageFile, options);
                    // Change the current system wallpaper
                    doPhotoPrint(bitmap);
                    // Show a toast message on successful change

        }
        return true;
    }

    public class SetwallpaperAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... strings) {
            setPhoneWallpaper();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(ImageFullScreenActivity.this,
                    "Wallpaper successfully changed", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void doPhotoPrint(Bitmap bitmap) {
        PrintHelper photoPrinter = new PrintHelper(this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);

        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }
}

