package com.app.ssoft.filemanager.Utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.widget.Toast;

import com.app.ssoft.filemanager.Views.InternalExplorerActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.io.FileUtils.copyDirectory;

/**
 * Created by Shekahar.Shrivastava on 12-Jan-18.
 */

public class Utils {
    private static Bitmap thumbnailDrawable;
    private static InputStream inputStream;
    private static File cutFile;
    private static File[] items;
    private static boolean isDeleted = false;
    private static String ext;


    public static String floatForm(double d) {
        return new DecimalFormat("#.##").format(d);
    }


    public static String bytesToHuman(long size) {
        long Kb = 1 * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size < Kb) return floatForm(size) + " byte";
        if (size >= Kb && size < Mb) return floatForm((double) size / Kb) + " KB";
        if (size >= Mb && size < Gb) return floatForm((double) size / Mb) + " MB";
        if (size >= Gb && size < Tb) return floatForm((double) size / Gb) + " GB";
        if (size >= Tb && size < Pb) return floatForm((double) size / Tb) + " TB";
        if (size >= Pb && size < Eb) return floatForm((double) size / Pb) + " PB";
        if (size >= Eb) return floatForm((double) size / Eb) + " EB";

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

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.MediaColumns._ID}, MediaStore.MediaColumns.DATA + "=?", new String[]{path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }

        ca.close();
        return null;

    }

    public static Bitmap setFileImageType(Context m_context, File m_file) {
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

    @SuppressLint("RestrictedApi")
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }

    public static void shareImage(Context context, String filePath) {
        Intent share = new Intent(Intent.ACTION_SEND);

        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
        share.setType("image/*");

        // Make sure you put example png image named myImage.png in your
        // directory

        File imageFileToShare = new File(filePath);

        Uri uri = Uri.fromFile(imageFileToShare);
        share.putExtra(Intent.EXTRA_STREAM, uri);

        context.startActivity(Intent.createChooser(share, "Share Image!"));
    }

    public static boolean delete(File file) {
        if (file.isDirectory()) {
            String[] children = file.list();
            if (children.length > 0) {
                for (int i = 0; i < children.length; i++) {
                    new File(file, children[i]).delete();
                    isDeleted = file.delete();
                }
            } else isDeleted = file.delete();
        } else {
            isDeleted = file.delete();
        }
        return isDeleted;
    }

    public static void deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
    }

    public static String getFormattedDate(String currentDate) throws ParseException {
        if (currentDate != null) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd hh:mm:ss");
            Date newDate = format.parse(currentDate);
            format = new SimpleDateFormat("MMMM dd, yyyy -EEEE hh:mm a");
            String date = format.format(newDate);
            return date;
        }
        return null;
    }

    public static void copyFile(Context context, File src) {
        try {
            if (!src.isDirectory()) {
                inputStream = new FileInputStream(src);
                InternalExplorerActivity.isCutOrCopied = true;
            } else {
                inputStream = null;
                items = src.listFiles();
                InternalExplorerActivity.isCutOrCopied = true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error copying folder", Toast.LENGTH_SHORT).show();
            InternalExplorerActivity.isCutOrCopied = false;
        }
    }

    public static void cutFile(File src) {
        cutFile = src;
        InternalExplorerActivity.isCutOrCopied = true;
    }

    public static boolean paste(Context context, File destination) throws IOException {
        boolean isPasted = false;
        if (InternalExplorerActivity.actionID != 0) {
            if (InternalExplorerActivity.actionID == 1) {
                if (inputStream != null) {
                    try {

                        OutputStream outputStream = new FileOutputStream(destination);
                        byte[] buf = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buf)) > 0) {
                            outputStream.write(buf, 0, length);
                        }
                        isPasted = true;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        isPasted = false;
                    }
                } else {
                    if (!destination.exists()) {
                        destination.mkdirs();
                    }
                    if (items != null && items.length > 0) {
                        for (File anItem : items) {
                            if (anItem.isDirectory()) {
                                // create the directory in the destination
                                File newDir = new File(destination, anItem.getName());
                                newDir.mkdir();
                                // copy the directory (recursive call)
                                copyDirectory(anItem, newDir);
                                isPasted = true;
                            } else {
                                File destFile = new File(destination, anItem.getName());
                                copySingleFile(anItem, destFile);
                            }
                        }

                  /*  Toast.makeText(context,"Error performing action",Toast.LENGTH_SHORT).show();
                    isPasted = false;*/
                    } else {
                        Toast.makeText(context, "Error performing action", Toast.LENGTH_SHORT).show();
                        isPasted = false;
                    }
                }
            } else if (InternalExplorerActivity.actionID == 2) {
                cutFile.renameTo(destination);
                isPasted = true;
            }
        } else {
            isPasted = false;
        }
        return isPasted;
    }

    private static void copySingleFile(File sourceFile, File destFile)
            throws IOException {
        System.out.println("COPY FILE: " + sourceFile.getAbsolutePath()
                + " TO: " + destFile.getAbsolutePath());
        try {
            if (!destFile.exists()) {
                destFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        try {
            sourceChannel = new FileInputStream(sourceFile).getChannel();
            destChannel = new FileOutputStream(destFile).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (sourceChannel != null) {
                sourceChannel.close();
            }
            if (destChannel != null) {
                destChannel.close();
            }
        }
    }

    public static String renameFile(File file, String newFileName, String originalFileName) {
        boolean isRenamed = false;
        String renamedFile = "";
        String filePath = getParentPath(file);
        if (!file.isDirectory()) {
            ext = (file.getAbsolutePath()).substring((file.getAbsolutePath()).lastIndexOf("."));
            File originalFile = new File(file.getAbsolutePath());
            if(newFileName.lastIndexOf(".") !=-1) {
               String  newFileExt = (newFileName.substring(newFileName.lastIndexOf(".")));
                if(newFileExt.equals(ext)){
                    ext = "";
                }
            }

            File newFile = new File(filePath + "/" + newFileName + ext);
            isRenamed = originalFile.renameTo(newFile);
            if (isRenamed) {
                renamedFile = newFile.getAbsolutePath();
            } else {
                renamedFile = "";
            }
        } else {
            File originalFile = new File(file.getAbsolutePath());
            File newFile = new File(filePath + "/" + newFileName);
            isRenamed = originalFile.renameTo(newFile);
            if (isRenamed) {
                renamedFile = newFile.getAbsolutePath();
            } else {
                renamedFile = "";
            }
        }

        return renamedFile;
    }
 /*   public static void  showChangeLangDialog(Context context ,String title , String positiveButtonText) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_folder_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                createFolder(edt.getText().toString());
                //do something with edt.getText().toString();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }*/

    public static String getParentPath(File file) {
        String filePath;
        String s = file.getAbsolutePath();
        int pos = s.lastIndexOf("/");
        if (pos == -1) {
            filePath = s;
        } else {
            filePath = s.substring(0, pos);
        }
        return filePath;
    }
}
