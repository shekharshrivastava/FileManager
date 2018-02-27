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
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;

import java.io.File;
import java.util.ArrayList;

public class MediaFilterActivity extends AppCompatActivity implements AbsListView.MultiChoiceModeListener, AdapterView.OnItemClickListener {
    private ListView rl_lvListRoot;
    private Cursor cursor;
    private MediaCursorAdapter musicAdapter;
    private TextView noMediaText;
    private ArrayList<String> toShare;
    private int selectedPosition;
    private ArrayList<String> mediaList;
    private ActionMode mMode;
    private int nr = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_filter);
        rl_lvListRoot = findViewById(R.id.rl_lvListRoot);
        rl_lvListRoot.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        rl_lvListRoot.setMultiChoiceModeListener(this);
        rl_lvListRoot.setOnItemClickListener(this);

        toShare = new ArrayList<>();
        noMediaText = findViewById(R.id.noMediaText);
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
        mediaList = new ArrayList<>();

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
        String orderBy = MediaStore.Audio.Media.DATE_ADDED + " DESC ";
        cursor = contentResolver.query(
                uri, // Uri
                mProjection, // Projection
                null, // Selection
                null, // Selection args
                orderBy // Sor order
        );
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            mediaList.add(cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
        }
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                musicAdapter = new MediaCursorAdapter(MediaFilterActivity.this, cursor, 4);
                rl_lvListRoot.setAdapter(musicAdapter);
                if (cursor.getCount() == 0) {
                    noMediaText.setVisibility(View.VISIBLE);
                    rl_lvListRoot.setVisibility(View.GONE);
                } else {
                    noMediaText.setVisibility(View.GONE);
                    rl_lvListRoot.setVisibility(View.VISIBLE);
                }
            }

        });
    }

    protected void getMediaFileList() {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        mediaList = new ArrayList<>();
        String[] mProjection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATE_ADDED,
                MediaStore.Audio.Media.SIZE
        };
        String orderBy = MediaStore.Audio.Media.DATE_ADDED + " DESC ";
        cursor = contentResolver.query(
                uri, // Uri
                mProjection, // Projection
                null, // Selection
                null, // Selection args
                orderBy // Sor order
        );
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            mediaList.add(cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
        }
        // Loop through the musics
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                musicAdapter = new MediaCursorAdapter(MediaFilterActivity.this, cursor, 3);
                rl_lvListRoot.setAdapter(musicAdapter);
                if (cursor.getCount() == 0) {
                    noMediaText.setVisibility(View.VISIBLE);
                    rl_lvListRoot.setVisibility(View.GONE);
                } else {
                    noMediaText.setVisibility(View.GONE);
                    rl_lvListRoot.setVisibility(View.VISIBLE);
                }
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

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            nr++;
            toShare.add(mediaList.get(position));
        } else {
            nr--;
            toShare.remove(mediaList.get(position));
        }
        mode.setTitle(nr + " selected");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        nr = 0;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cab_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_share:
                nr = 0;
                Utils.shareMultipleFiles(MediaFilterActivity.this, toShare);
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mMode = null;
        if (rl_lvListRoot.isItemChecked(selectedPosition)) {
            rl_lvListRoot.setItemChecked(selectedPosition, false);
            selectedPosition = -1;
        }
        rl_lvListRoot.setOnItemClickListener(MediaFilterActivity.this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        rl_lvListRoot.setItemChecked(position, false);
        if (mMode != null) {
            mMode.finish();
        } else {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            MimeTypeMap map = MimeTypeMap.getSingleton();
            String extension = path.substring(path.lastIndexOf("."));
            String type = map.getMimeTypeFromExtension(extension.replace(".", ""));
            File m_musicFile = new File(path);
            if (type == "video/mp4") {
                Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.putExtra("path", path);
                intent.putExtra("title", title);
                startActivity(intent);
            } else if (type != "*//*") {
                Uri uri = FileProvider.getUriForFile(MediaFilterActivity.this, getApplicationContext().getPackageName(), m_musicFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, type);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
//                Toast.makeText(MediaFilterActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MediaFilterActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
