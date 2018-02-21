package com.app.ssoft.filemanager.Views;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tuyenmonkey.mkloader.MKLoader;

import java.io.File;
import java.util.ArrayList;

public class StorageInfoActivity extends AppCompatActivity {

    private long imageFilesSize = 0;
    private long docFileSizeActual = 0;
    private PieChart pieChart;
    ArrayList<Entry> entries;
    private ArrayList<String> PieEntryLabels;
    private PieDataSet pieDataSet;
    private PieData pieData;
    private Cursor cursor;
    private float docFileSize;
    private float imageFileSize;
    private float audioFileSize;
    private float videoFileSize;
    private long actualAudioFileSize = 0;
    private long videoFileSizeActual = 0;
    private MKLoader loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_info);
        getSupportActionBar().setTitle("Storage Info");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadingIndicator = findViewById(R.id.loading_indicator);
        pieChart = (PieChart) findViewById(R.id.chart1);
        entries = new ArrayList<>();

        PieEntryLabels = new ArrayList<String>();

        new getFileSizeAsyncTask().execute();
/*

        new Handler().post(new Runnable() {

            @Override
            public void run() {
//                docFileSize = getDocumentsFileSize(p_rootPath);
                imageFileSize = getAllImageSize(p_rootPath);
                audioFileSize = getAudioFileSize();
                videoFileSize = getVideoFilesList();

                entries = new ArrayList<>();

                PieEntryLabels = new ArrayList<String>();

//                AddValuesToPIEENTRY();

                AddValuesToPieEntryLabels();


            }

        });
*/


    }

    public void AddValuesToPIEENTRY() {

//        entries.add(new BarEntry(docFileSizeActual, 0));
        entries.add(new BarEntry(imageFilesSize, 1));
        entries.add(new BarEntry(actualAudioFileSize, 2));
        entries.add(new BarEntry(videoFileSizeActual, 3));
//        entries.add(new BarEntry(7f, 4));


    }

    public void AddValuesToPieEntryLabels() {
        PieEntryLabels.add("Docs\n" + Utils.bytesToHuman(docFileSizeActual));
        PieEntryLabels.add("Images\n" + Utils.bytesToHuman(imageFilesSize));
        PieEntryLabels.add("Audio\n" + Utils.bytesToHuman(actualAudioFileSize));
        PieEntryLabels.add("Video\n" + Utils.bytesToHuman(videoFileSizeActual));

//        PieEntryLabels.add("App");


    }

    public float getDocumentsFileSize() {

        final String p_rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File m_file = new File(p_rootPath);
        File[] m_filesArray = m_file.listFiles();

        for (int i = 0; i < m_filesArray.length; i++) {
            File file = m_filesArray[i];

            if (!file.isDirectory()) {
                if (file.getName().endsWith(".pdf") || file.getName().endsWith(".doc") ||
                        file.getName().endsWith(".txt") || file.getName().endsWith(".xls")) {
                    docFileSizeActual = docFileSizeActual + file.length();
                }

            } else {
                File[] dirFiles = file.listFiles();
                for (File docFiles : dirFiles) {
                    if (docFiles.getName().endsWith(".pdf") || docFiles.getName().endsWith(".doc") ||
                            docFiles.getName().endsWith(".txt") || docFiles.getName().endsWith(".xls")) {
                        docFileSizeActual = docFileSizeActual + docFiles.length();
                    }
                }
            }
        }
        return Float.valueOf((Utils.bytesToHuman(docFileSizeActual)).substring(0, Utils.bytesToHuman(docFileSizeActual).indexOf(' ')));
    }

    public float getAllImageSize() {
        final String p_rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File m_file = new File(p_rootPath);
        File[] m_filesArray = m_file.listFiles();

        for (int i = 0; i < m_filesArray.length; i++) {
            File file = m_filesArray[i];

            if (!file.isDirectory()) {
                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg")) {
                    imageFilesSize = imageFilesSize + file.length();
                }

            } else {
                File[] dirFiles = file.listFiles();
                for (File imageFiles : dirFiles) {
                    if (!imageFiles.isDirectory()) {
                        if (imageFiles.getName().endsWith(".jpg") || imageFiles.getName().endsWith(".png") || imageFiles.getName().endsWith(".jpeg")) {
                            imageFilesSize = imageFilesSize + imageFiles.length();
                        }
                    } else {
                        do {
                            File[] fileUnderDir = imageFiles.listFiles();
                            for (File imageFileSubDir : fileUnderDir) {
                                if (!imageFileSubDir.isDirectory()) {
                                    if (imageFileSubDir.getName().endsWith(".jpg") || imageFileSubDir.getName().endsWith(".png") || imageFileSubDir.getName().endsWith(".jpeg")) {
                                        imageFilesSize = imageFilesSize + imageFileSubDir.length();
                                    }
                                }
                            }
                        }

                        while (!imageFiles.isDirectory());


                    }
                }
            }

        }
        return Float.valueOf((Utils.bytesToHuman(imageFilesSize)).substring(0, Utils.bytesToHuman(imageFilesSize).indexOf(' ')));
    }

    public float getAudioFileSize() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] mProjection = {
                MediaStore.Audio.Media.SIZE
        };
        cursor = contentResolver.query(
                uri, // Uri
                mProjection, // Projection
                null, // Selection
                null, // Selection args
                null // Sor order
        );
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here
            long size = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
            actualAudioFileSize = actualAudioFileSize + size;


        }
        return Float.valueOf((Utils.bytesToHuman(actualAudioFileSize)).substring(0, Utils.bytesToHuman(actualAudioFileSize).indexOf(' ')));
    }

    public float getVideoFilesList() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] mProjection = {
                MediaStore.Video.Media.SIZE
        };
        cursor = contentResolver.query(
                uri, // Uri
                mProjection, // Projection
                null, // Selection
                null, // Selection args
                null // Sor order
        );

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // do what you need with the cursor here
            long size = cursor.getLong(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));
            videoFileSizeActual = videoFileSizeActual + size;


        }

        return Float.valueOf((Utils.bytesToHuman(videoFileSizeActual)).substring(0, Utils.bytesToHuman(videoFileSizeActual).indexOf(' ')));
    }

    public class getFileSizeAsyncTask extends AsyncTask<String, Void, FileSize> {
        FileSize fileSize = new FileSize();

        @Override
        protected FileSize doInBackground(String... strings) {

            getDocumentsFileSize();
            getAllImageSize();
            getAudioFileSize();
            getVideoFilesList();

            fileSize.setDocFIleSize(docFileSizeActual);
            fileSize.setImageFilesSize(imageFilesSize);
            fileSize.setAudioFileSize(actualAudioFileSize);
            fileSize.setVideoFilesSize(videoFileSizeActual);

            return fileSize;
        }

        @Override
        protected void onPreExecute() {
            pieChart.setVisibility(View.GONE);
            loadingIndicator.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(FileSize aFloat) {
            loadingIndicator.setVisibility(View.GONE);
            pieChart.setVisibility(View.VISIBLE);
            entries.add(new BarEntry(aFloat.getDocFIleSize(), 0));
            entries.add(new BarEntry(aFloat.getImageFilesSize(), 1));
            entries.add(new BarEntry(aFloat.getAudioFileSize(), 2));
            entries.add(new BarEntry(aFloat.getVideoFilesSize(), 3));
            AddValuesToPieEntryLabels();
            pieDataSet = new PieDataSet(entries, "");
            pieDataSet.setDrawValues(false);
            pieData = new PieData(PieEntryLabels, pieDataSet);
            pieData.setDrawValues(false);

            pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            pieDataSet.setValueTextSize(12f);


            pieChart.setData(pieData);
            pieChart.setDescriptionTextSize(12f);
            Legend legend = pieChart.getLegend();
            legend.setTextSize(12f);
            legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);


            pieChart.animateY(3000);
            super.onPostExecute(aFloat);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}