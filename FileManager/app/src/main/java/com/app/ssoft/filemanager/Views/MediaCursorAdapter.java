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
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;
import com.app.ssoft.filemanager.Utils.VideoThumbLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pkmmte.view.CircularImageView;

import java.io.File;

/**
 * Created by Shekahar.Shrivastava on 06-Feb-18.
 */

public class MediaCursorAdapter extends CursorAdapter {
    private final int chooserFlags;
    private final VideoThumbLoader mVideoThumbLoader;
    private LayoutInflater cursorInflater;

    // Default constructor
    public MediaCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        chooserFlags = flags;
        cursorInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
         mVideoThumbLoader = new VideoThumbLoader();

    }

    public void bindView(View view, Context context, final Cursor cursor) {
        Bitmap bitmap = null;
        final CircularImageView imageView = (CircularImageView) view.findViewById(R.id.lr_ivFileIcon);
        TextView textViewDate = (TextView) view.findViewById(R.id.lr_tvdate);
        TextView textViewTitle = (TextView) view.findViewById(R.id.lr_tvFileName);
        if (chooserFlags == 3) {


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
                        .placeholder(R.drawable.music_default)
                        .error(R.drawable.music_default)
                        .into(imageView);

            } catch (Exception exception) {
                exception.printStackTrace();
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.icon_music_1);

            }

            textViewTitle.setText(title);
            textViewDate.setText(Utils.bytesToHuman(size) + "   " + date);


        } else if (chooserFlags == 4) {
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
            int duration = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
            int date = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));
            int size = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));

            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
            String thumbnailData = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));

            textViewTitle.setText(title);
            textViewDate.setText(Utils.bytesToHuman(size) + "   " + date);

            File filePath = new File(thumbnailData);

            imageView.setTag(filePath.getAbsolutePath());// binding imageview
            imageView.setImageResource(R.drawable.video_default); //default image
            mVideoThumbLoader.showThumbByAsynctack(filePath.getAbsolutePath(), imageView);

        }else if (chooserFlags == 2){
            final ImageView gridIV = (ImageView) view.findViewById(R.id.grid_image);
            TextView imageTitle = (TextView) view.findViewById(R.id.grid_text);
            int id = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID));
            String title = cursor.getString(cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.TITLE));
            int date = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
            int size = cursor.getInt(cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
            imageTitle.setText(title);
            Glide.with(context)
                    .load(Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, "" + id))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.picture_folder)
                    .error(R.drawable.doc_folder)
                    .into(gridIV);

        }
    }

    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // R.layout.list_row is your xml layout for each row
        if(chooserFlags != 2) {
            return cursorInflater.inflate(R.layout.music_row_layout, parent, false);
        }
        return cursorInflater.inflate(R.layout.quicklink_layout, parent, false);
    }


}
