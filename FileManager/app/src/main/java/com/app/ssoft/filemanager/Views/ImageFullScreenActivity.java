package com.app.ssoft.filemanager.Views;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.print.PrintHelper;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ImageFullScreenActivity extends AppCompatActivity {

    private String imageFile;
    private File image;
    public boolean isDeleted = false;
    private String imagePath;
    private Bitmap bitmap;
    private Intent intent;
    private ArrayList<String> imageArray;
    private int imageSelectedPos;
    private ViewPager viewPager;
    private ImageView imageView;
    private BottomNavigationView navigation;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_full_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setShowHideAnimationEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));
        imageView = findViewById(R.id.imageView);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        intent = getIntent();
        if (getIntent().getData() != null) {
            viewPager.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageFile = getIntent().getData().getPath();
            image = new File(imageFile);
            Glide.with(this)
                    .load(image)
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.doc_folder)
                    .into(imageView);
        } else {
            viewPager.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            imageArray = intent.getStringArrayListExtra("imgPath");
            imageSelectedPos = intent.getIntExtra("position", 0);
            imageFile = intent.getStringExtra("imgFile");
            String imageName = intent.getStringExtra("imageName");
            image = new File(imageFile);
            getSupportActionBar().setTitle(imageName);


            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, imageArray, imageSelectedPos);
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setCurrentItem(imageSelectedPos);
            viewPager.getCurrentItem();

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    imageSelectedPos = position;
                    image = new File(imageArray.get(position));
                    getSupportActionBar().setTitle(image.getName());
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        }


       navigation = (BottomNavigationView) findViewById(R.id.navigation);
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
                    Utils.shareImage(ImageFullScreenActivity.this, image.getAbsolutePath());
                    return true;
                case R.id.navigation_delete:
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                            ImageFullScreenActivity.this);
                    alertDialog.setTitle("Alert");
                    alertDialog.setMessage("Are you sure you want to delete this file ?");
                    alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            isDeleted = Utils.delete(image);
                            if (isDeleted) {
                                intent.putExtra("isDelete", isDeleted);
                                intent.putExtra("deletedFile", image.getAbsolutePath());
                                setResult(1, intent);
                                Toast.makeText(ImageFullScreenActivity.this, "File deleted successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(ImageFullScreenActivity.this, "Error deleting file", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();


                    return true;
                case R.id.navigation_info:
                    Intent intent = new Intent(ImageFullScreenActivity.this, InfoActivity.class);
                    intent.putExtra("imageFile", image.getAbsolutePath());
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
            bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), options);
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
            case android.R.id.home:
                finish();
                break;
            case R.id.use_as:
                new SetwallpaperAsyncTask().execute();
                break;
            case R.id.print:
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), options);
                // Change the current system wallpaper
                doPhotoPrint(bitmap);
                // Show a toast message on successful change
                break;
            case R.id.openWith:
                openWIth();
                break;

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

    private void openWIth() {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String extension = image.getAbsolutePath().substring(image.getAbsolutePath().lastIndexOf("."));

        String type = map.getMimeTypeFromExtension(extension.replace(".", ""));

        if (type == null)
            type = "*//*";

        if (!Objects.equals(type, "*//*")) {
            Uri uri = FileProvider.getUriForFile(ImageFullScreenActivity.this, getApplicationContext().getPackageName(), image);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, type);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            Toast.makeText(ImageFullScreenActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
        }
    }

}

