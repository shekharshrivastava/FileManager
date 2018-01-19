package com.app.ssoft.filemanager.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by Shekahar.Shrivastava on 12-Jan-18.
 */

public class Utils {
    private static Bitmap thumbnailDrawable;

    public static String floatForm (double d)
    {
        return new DecimalFormat("#.##").format(d);
    }


    public static String bytesToHuman (long size)
    {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return floatForm(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " KB";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " MB";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " GB";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " TB";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " PB";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " EB";

        return "???";
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null );
        }

        ca.close();
        return null;

    }
    public static Bitmap setFileImageType(Context m_context ,File m_file) {
        int m_lastIndex = m_file.getAbsolutePath().lastIndexOf(".");
        String m_filepath = m_file.getAbsolutePath();
        if (m_file.isDirectory())
            return null;
        else {
            if (m_filepath.substring(m_lastIndex).equalsIgnoreCase(".png")) {
                try {
                    thumbnailDrawable = Utils.getThumbnail(m_context.getContentResolver(), m_filepath);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return thumbnailDrawable;
            } else if (m_filepath.substring(m_lastIndex).equalsIgnoreCase(".jpg")) {

                try {
                    thumbnailDrawable = Utils.getThumbnail(m_context.getContentResolver(), m_filepath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return thumbnailDrawable;
            } else {
                return null;
            }
        }
    }
}
