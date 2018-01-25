package com.app.ssoft.filemanager.Views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.File;

import at.grabner.circleprogress.CircleProgressView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener,View.OnClickListener {

    private TextView internalStorageSpace;
    private CircleProgressView externalProgressBar;
    private RelativeLayout externalStorageLayout;
    private TextView externalStorageSpace;
    private float percentage;
    private CircleProgressView internalProgressBar;
    private GridView quickLinksGV;
    private LinearLayout internalStorageLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        String[] web = {
                "Documents",
                "App",
                "Images",
                "Audio",
                "Video",
                "Download",

        };
        int[] imageId = {
                R.drawable.doc_folder,
                R.drawable.app_folder,
                R.drawable.picture_folder,
                R.drawable.music_folder,
                R.drawable.movie_folder,
                R.drawable.download_folder,


        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        internalStorageLinearLayout = findViewById(R.id.internalStorageLinearLayout);
        internalStorageLinearLayout.setOnClickListener(this);
        internalStorageSpace = findViewById(R.id.internalStorageSpace);
        internalProgressBar = findViewById(R.id.internal_progress_bar);

        externalProgressBar = findViewById(R.id.external_progress_bar);
        externalStorageLayout = findViewById(R.id.externalStorageLayout);
        externalStorageSpace = findViewById(R.id.externalStorageSpace);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (externalMemoryAvailable()) {
            externalStorageLayout.setVisibility(View.VISIBLE);
            externalStorageSpace.setText(getAvailableExternalMemorySize() + " / " + getTotalExternalMemorySize());
            externalProgressBar.setValueAnimated(calculatePercentage(getTotalExternalMemorySize(), getAvailableExternalMemorySize()));

        } else {
            externalStorageLayout.setVisibility(View.GONE);
        }

        internalStorageSpace.setText(getAvailableInternalMemorySize() + " / " + getTotalInternalMemorySize());
        internalProgressBar.setValueAnimated(calculatePercentage(getTotalInternalMemorySize(), getAvailableInternalMemorySize()));

        quickLinksGV = findViewById(R.id.quickLinksGV);
        CustomQLGridAdapter adapter = new CustomQLGridAdapter(MainActivity.this, web, imageId);
        quickLinksGV.setAdapter(adapter);
        quickLinksGV.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return Utils.bytesToHuman(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return Utils.bytesToHuman(totalBlocks * blockSize);
    }

    public static boolean externalMemoryAvailable() {
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        Boolean isSDSupportedDevice = Environment.isExternalStorageRemovable();

        if (isSDSupportedDevice && isSDPresent) {
            return true;
        } else {
            return false;
        }
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return Utils.bytesToHuman(availableBlocks * blockSize);
        } else {
            return "";
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return Utils.bytesToHuman(totalBlocks * blockSize);
        } else {
            return "";
        }
    }

    public float calculatePercentage(String totalSpace, String avblSpace) {
        if (totalSpace != null && avblSpace != null) {
            String[] concatTotalSpace = totalSpace.split(" ");
            String[] concatAvblSpace = avblSpace.split(" ");

            double spaceAvbl = Double.parseDouble(concatAvblSpace[0]);
            double totalSpcAvbl = Double.parseDouble(concatTotalSpace[0]);
            double usedSpace = totalSpcAvbl - spaceAvbl;
            double res = (usedSpace / totalSpcAvbl) * 100f;
            percentage = (float) (res);
        } else {
            percentage = 0f;
        }
        return percentage;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.internalStorageLinearLayout:
                Intent intent = new Intent(this,InternalExplorerActivity.class);
                startActivity(intent);

        }

    }
}
