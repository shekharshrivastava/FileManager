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
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;

import java.io.File;

public class MusicFilterActivity extends AppCompatActivity {
    private ListView rl_lvListRoot;
    private Cursor cursor;
    private MusicCursorAdapter musicAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_filter);
        rl_lvListRoot = findViewById(R.id.rl_lvListRoot);
        getMediaFileList();
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
                    Uri uri = FileProvider.getUriForFile(MusicFilterActivity.this, getApplicationContext().getPackageName(), m_musicFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, type);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Toast.makeText(MusicFilterActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new Handler().post(new Runnable() {

            @Override
            public void run() {
                musicAdapter = new MusicCursorAdapter(MusicFilterActivity.this,cursor,0);
                rl_lvListRoot.setAdapter(musicAdapter);
            }

        });
    }

}
