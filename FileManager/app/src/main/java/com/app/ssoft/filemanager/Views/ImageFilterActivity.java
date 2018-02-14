package com.app.ssoft.filemanager.Views;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.etsy.android.grid.StaggeredGridView;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class ImageFilterActivity extends AppCompatActivity {
    private String m_root = Environment.getExternalStorageDirectory().getPath();
    private Cursor cursor;
    private int columnIndex;
    private StaggeredGridView listView;
    private MediaCursorAdapter musicAdapter;
    private String internalStorageRoot;
    private ListView rl_lvListRoot;
    private ArrayList<String> m_item;
    private boolean m_isRoot;
    private ArrayList<String> m_hiddenFilesNames;
    private ArrayList<String> m_hiddenPaths;
    private ArrayList<String> m_path;
    private ArrayList<String> m_files;
    private ArrayList<String> m_filesPath;
    private String rootPath;
    private String m_curDir;
    private PictureFIlterAdapter m_listAdapter;
    private File m_isFile;

    public static final int RESULT_DELETED = 1;
    private TextView noMediaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_filter);
        getSupportActionBar().setTitle("Images");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        noMediaText = findViewById(R.id.noMediaText);
        listView = (StaggeredGridView) findViewById(R.id.grid_view);
        internalStorageRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        getDirFromRoot(internalStorageRoot);

//        getImageList();
//        AllImageAdapter adapter = new AllImageAdapter(ImageFilterActivity.this,cursor,columnIndex);
        //adapter.notifyDataSetChanged();
    }

    /*  protected void getImageList() {
          ContentResolver contentResolver = getContentResolver();
          Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

          String[] mProjection = {
                  MediaStore.Images.Media.TITLE,
                  MediaStore.Images.Thumbnails._ID,
                  MediaStore.Images.Media.DATA,
                  MediaStore.Images.Media.DATE_ADDED,
                  MediaStore.Images.Media.SIZE
          };
          cursor = contentResolver.query(
                  uri, // Uri
                  mProjection, // Projection
                  null, // Selection
                  null, // Selection args
                  null // Sor order
          );
          new Handler().post(new Runnable() {

              @Override
              public void run() {
                  musicAdapter = new MediaCursorAdapter(ImageFilterActivity.this, cursor, 2);
                  listView.setAdapter(musicAdapter);
              }

          });
      }*/
    public void getDirFromRoot(String p_rootPath) {
//        getSupportActionBar().setSubtitle(p_rootPath);
        m_item = new ArrayList<String>();
        m_isRoot = true;
        m_hiddenFilesNames = new ArrayList<String>();
        m_hiddenPaths = new ArrayList<String>();
        m_path = new ArrayList<String>();
        m_files = new ArrayList<String>();
        m_filesPath = new ArrayList<String>();
        rootPath = p_rootPath;
        File m_file = new File(p_rootPath);
        File[] m_filesArray = m_file.listFiles();
        if (!p_rootPath.equals(m_root)) {
            m_item.add("../");
            m_path.add(m_file.getParent());
            m_isRoot = false;
        }
        m_curDir = p_rootPath;
        //sorting file list in alphabetical order
//        Arrays.sort(m_filesArray);
        Arrays.sort(m_filesArray, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

        for (int i = 0; i < m_filesArray.length; i++) {
            File file = m_filesArray[i];
            if (!file.isDirectory()) {
                Arrays.sort(m_filesArray);
                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg")) {
                    if (m_files.contains(file.getName()) && m_filesPath.contains(file.getPath())) {
                        m_files.remove(file.getName());
                        m_filesPath.remove(file.getPath());
                    }
                    m_files.add(file.getName());
                    m_filesPath.add(file.getPath());
                }
            } else {
                File[] dirFiles = file.listFiles();
                for (File imageFiles : dirFiles) {
                    if (!imageFiles.isDirectory()) {
                        if (imageFiles.getName().endsWith(".jpg") || imageFiles.getName().endsWith(".png") || imageFiles.getName().endsWith(".jpeg")) {
                            if (m_files.contains(file.getName()) && m_filesPath.contains(file.getPath())) {
                                m_files.remove(file.getName());
                                m_filesPath.remove(file.getPath());
                            }
                            m_files.add(file.getName());
                            m_filesPath.add(file.getPath());
                        }
                    } else {
                        do {
                            File[] fileUnderDir = imageFiles.listFiles();
                            for (File imageFileSubDir : fileUnderDir) {
                                if (!imageFileSubDir.isDirectory()) {
                                    if (imageFileSubDir.getName().endsWith(".jpg") || imageFileSubDir.getName().endsWith(".png") || imageFileSubDir.getName().endsWith(".jpeg")) {
                                        if (m_files.contains(file.getName()) && m_filesPath.contains(file.getPath())) {
                                            m_files.remove(file.getName());
                                            m_filesPath.remove(file.getPath());
                                        }
                                        m_files.add(file.getName());
                                        m_filesPath.add(file.getPath());
                                    }
                                }
                            }
                        }

                        while (!imageFiles.isDirectory());


                    }
                }
            }
        }
        for (String m_AddFile : m_files)

        {
            m_item.add(m_AddFile);
        }
        for (
                String m_AddPath : m_filesPath)

        {
            m_path.add(m_AddPath);
        }
        if (m_path.size() == 0 && m_item.size() == 0) {
            noMediaText.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else{
            noMediaText.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        m_listAdapter = new PictureFIlterAdapter(this, m_item, m_path, m_isRoot);
        listView.setAdapter(m_listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                m_isFile = new File(m_path.get(position));
                if (m_isFile.isDirectory()) {
                    getDirFromRoot(m_isFile.toString());
                } else {
                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String extension = m_isFile.getAbsolutePath().substring(m_isFile.getAbsolutePath().lastIndexOf("."));

                    String type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                    if (type == null)
                        type = "*//*";
                    if (type == "image/jpeg") {
                        Intent intent = new Intent(ImageFilterActivity.this, ImageFullScreenActivity.class);
                        intent.putExtra("imgPath", m_path);
                        intent.putExtra("position", position);
                        intent.putExtra("imgFile", m_isFile.getAbsolutePath());
                        intent.putExtra("imageName", m_item.get(position));
                        startActivityForResult(intent, RESULT_DELETED);
                    } else {
                        if (type != "*//*") {
                            Uri uri = FileProvider.getUriForFile(ImageFilterActivity.this, getApplicationContext().getPackageName(), m_isFile);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, type);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ImageFilterActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            }


        });
    }

    @Override
    public void onBackPressed() {
        if (m_path.size() > 0) {
            File m_isFile = new File(m_path.get(0));
            if (m_isFile.isDirectory() && !m_isRoot) {
                getDirFromRoot(m_isFile.toString());
            } else {
                finish();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_DELETED && resultCode == RESULT_DELETED) {
            File file = new File(data.getStringExtra("deletedFile"));
            m_item.remove(file.getName());
            m_path.remove(file.getPath());
            m_listAdapter.notifyDataSetChanged();

        }
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
