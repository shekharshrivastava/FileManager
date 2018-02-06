package com.app.ssoft.filemanager.Views;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pkmmte.view.CircularImageView;

/**
 * Created by Shekahar.Shrivastava on 06-Feb-18.
 */

public class MusicCursorAdapter extends CursorAdapter {
    private LayoutInflater cursorInflater;

    // Default constructor
    public MusicCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

    }

    public void bindView(View view, Context context, Cursor cursor) {
        Bitmap bitmap = null;
        CircularImageView imageView = (CircularImageView) view.findViewById(R.id.lr_ivFileIcon);
        TextView textViewDate = (TextView) view.findViewById(R.id.lr_tvdate);
        TextView textViewTitle = (TextView) view.findViewById(R.id.lr_tvFileName);

        Long albumId = cursor.getLong(cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID));

        int duration = cursor.getInt(cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
        int date = cursor.getInt(cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED));
        int size = cursor.getInt(cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));


        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));

        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

        try {
            Glide.with(context)
                    .load(albumArtUri)
                    .asBitmap().fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.icon_music_1)
                    .error(R.drawable.icon_music_1)
                    .into(imageView);

        } catch (Exception exception) {
            exception.printStackTrace();
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.icon_music_1);

        }

        textViewTitle.setText(title);
        textViewDate.setText(Utils.bytesToHuman(size) + "   " + date);


    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        return cursorInflater.inflate(R.layout.music_row_layout, parent, false);
    }
}
