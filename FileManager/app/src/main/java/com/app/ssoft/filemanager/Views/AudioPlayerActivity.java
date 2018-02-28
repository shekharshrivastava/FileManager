package com.app.ssoft.filemanager.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.app.ssoft.filemanager.R;
import com.example.jean.jcplayer.JcAudio;
import com.example.jean.jcplayer.JcPlayerView;

import java.util.ArrayList;

public class AudioPlayerActivity extends AppCompatActivity {

    private JcPlayerView jcplayerView;
    private String path;
    private String title;
    private ArrayList<JcAudio> jcAudios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        title = intent.getStringExtra("title");
        getSupportActionBar().setTitle(title);
        jcplayerView = (JcPlayerView) findViewById(R.id.jcplayer);
        jcAudios = new ArrayList<>();
        jcAudios.add(JcAudio.createFromFilePath(title,path ));
        jcplayerView.initPlaylist(jcAudios);

    }

    @Override
    protected void onStop() {
        jcplayerView.createNotification();
        super.onStop();

    }
}
