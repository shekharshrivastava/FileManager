package com.app.ssoft.filemanager.Views;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;

import java.io.File;

public class MediaFilterActivity extends AppCompatActivity {
    private ListView rl_lvListRoot;
    private Cursor cursor;
    private MediaCursorAdapter musicAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_filter);
        rl_lvListRoot = findViewById(R.id.rl_lvListRoot);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        int chooserValue = intent.getIntExtra("chooserIntent", 0);
        if (chooserValue == 3) {
            getSupportActionBar().setTitle("Audio");
            getMediaFileList();
        } else if (chooserValue == 4) {
            getSupportActionBar().setTitle("Video");
            getVideoFilesList();
        }
    }

    protected void getVideoFilesList() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] mProjection = {
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Media.MINI_THUMB_MAGIC,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE
        };
        cursor = contentResolver.query(
                uri, // Uri
                mProjection, // Projection
                null, // Selection
                null, // Selection args
                null // Sor order
        );
        rl_lvListRoot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                MimeTypeMap map = MimeTypeMap.getSingleton();
                String extension = path.substring(path.lastIndexOf("."));
                String type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                File m_musicFile = new File(path);
                if (type != "*//*") {
                    Uri uri = FileProvider.getUriForFile(MediaFilterActivity.this, getApplicationContext().getPackageName(), m_musicFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, type);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Toast.makeText(MediaFilterActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
                }
            }
        });
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                musicAdapter = new MediaCursorAdapter(MediaFilterActivity.this, cursor, 4);
                rl_lvListRoot.setAdapter(musicAdapter);
            }

        });
    }

    protected void getMediaFileList() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] mProjection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE
        };
        cursor = contentResolver.query(
                uri, // Uri
                mProjection, // Projection
                null, // Selection
                null, // Selection args
                null // Sor order
        );

        // Loop through the musics
        rl_lvListRoot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                MimeTypeMap map = MimeTypeMap.getSingleton();
                String extension = path.substring(path.lastIndexOf("."));
                String type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                File m_musicFile = new File(path);
                if (type != "*//*") {
                    Uri uri = FileProvider.getUriForFile(MediaFilterActivity.this, getApplicationContext().getPackageName(), m_musicFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, type);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Toast.makeText(MediaFilterActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new Handler().post(new Runnable() {

            @Override
            public void run() {
                musicAdapter = new MediaCursorAdapter(MediaFilterActivity.this, cursor, 3);
                rl_lvListRoot.setAdapter(musicAdapter);
            }

        });
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
