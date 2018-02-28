package com.app.ssoft.filemanager.Views;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.app.ssoft.filemanager.R;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;
    private RelativeLayout container;
    private boolean isToolbarShowing = true;
    private int currentPosition;
    private String path;
    private String title;
    private Uri uri;
    private MediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        container = findViewById(R.id.container);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.yourTranslucentColor)));
        videoView = findViewById(R.id.videoView);
        Intent intent = getIntent();
        Uri externalLink = getIntent().getData();
        mediaController = new MediaController(this);
        mediaController.setAnchorView(container);
        mediaController.hide();
        if (externalLink != null) {
            uri = externalLink;
            isToolbarShowing = true;
        } else {
            path = intent.getStringExtra("path");
            title = intent.getStringExtra("title");
            getSupportActionBar().setTitle(title);
            uri = Uri.parse(path);
            isToolbarShowing = true;
       /* videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });*/
     /*   videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isToolbarShowing) {
                    getSupportActionBar().hide();
                    if(mediaController.isShowing()) {
                        mediaController.hide();
                    }
                    isToolbarShowing = false;
                } else {
                    getSupportActionBar().show();
                    if(!mediaController.isShowing()) {
                        mediaController.show();
                    }
                    isToolbarShowing = true;
                }
                return false;
            }
        });*/
        }
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentPosition = videoView.getCurrentPosition();
        videoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.seekTo(currentPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("current_pos", videoView.getCurrentPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int savedCurrentPos = savedInstanceState.getInt("current_pos");
            videoView.seekTo(savedCurrentPos);
            currentPosition = savedCurrentPos;
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);

    }
}

