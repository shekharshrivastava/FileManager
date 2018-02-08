package com.app.ssoft.filemanager.Views;

import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

public class InfoActivity extends AppCompatActivity {

    private TextView picDateTV;
    private TextView picTimeTV;
    private TextView pathTv;
    private TextView useCameraTV;
    private TextView dimensionTV;
    private TextView apertureTv;
    private TextView isoTV;
    private TextView fileSizeTV;
    private TextView cameraMP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getSupportActionBar().setTitle("Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        picDateTV = findViewById(R.id.picDateTV);
        picTimeTV = findViewById(R.id.picTimeTV);
        pathTv = findViewById(R.id.pathTv);
        useCameraTV = findViewById(R.id.useCameraTV);
        dimensionTV = findViewById(R.id.dimensionTV);
        apertureTv = findViewById(R.id.aperture);
        isoTV = findViewById(R.id.isoTV);
        fileSizeTV = findViewById(R.id.fileSize);
        cameraMP = findViewById(R.id.cameraMP);

        Intent intent = getIntent();
        String filename = intent.getStringExtra("imageFile");
        pathTv.setText(filename);
        File imageFile = new File(filename);
        long fileLength = imageFile.length();
        float fileSize = (fileLength / (1024 * 1024));
        fileSizeTV.setHint("" + fileSize + "MB");


        try {
            ExifInterface exif = new ExifInterface(filename);
            ShowExif(exif);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, "Error!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void ShowExif(ExifInterface exif) {
        String dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME);
        try {
            String formattedDate = Utils.getFormattedDate(dateTime);
            if (formattedDate != null) {
                String[] parts = formattedDate.split("-");
                String date = parts[0]; // 004
                String time = parts[1]; // 034556
                picDateTV.setText(date);
                picTimeTV.setText(time);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String myAttribute = "Exif information ---\n";
        String aperture = exif.getAttribute(ExifInterface.TAG_APERTURE);
        String iso = exif.getAttribute(ExifInterface.TAG_ISO);
        apertureTv.setHint("f/" + aperture);
        isoTV.setHint("ISO" + iso);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
        myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);
        String length = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        String width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        dimensionTV.setText(length + " x " + width);
        int lengthValue = Integer.parseInt(length);
        int widthValue = Integer.parseInt(width);

        double mpValue = ((lengthValue*widthValue)/1024000);
        int roundUpValue = (int) Math.round(mpValue);
        cameraMP.setHint("" + roundUpValue + "MP");
        String mobileMake = exif.getAttribute(ExifInterface.TAG_MAKE);
        String mobileModel = exif.getAttribute(ExifInterface.TAG_MODEL);
        useCameraTV.setText(mobileMake + " " + mobileModel);

        myAttribute += getTagString(ExifInterface.TAG_WHITE_BALANCE, exif);
    }

    private String getTagString(String tag, ExifInterface exif) {
        return (tag + " : " + exif.getAttribute(tag) + "\n");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
