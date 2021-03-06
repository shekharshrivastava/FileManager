package com.app.ssoft.filemanager.Views;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;
import com.tuyenmonkey.mkloader.MKLoader;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ImageFilterActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener {
    private String m_root = Environment.getExternalStorageDirectory().getPath();
    private Cursor cursor;
    private int columnIndex;
    private GridView listView;
    private MediaCursorAdapter musicAdapter;
    private String internalStorageRoot;
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
    private MKLoader loadingIndicator;
    private ArrayList<String> toShare;
    private int selectedPosition;
    private ActionMode mMode;
    private int nr = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_filter);
        getSupportActionBar().setTitle("Images");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadingIndicator = findViewById(R.id.loading_indicator);
        toShare = new ArrayList<>();
        noMediaText = findViewById(R.id.noMediaText);
        listView = (GridView) findViewById(R.id.grid_view);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(this);
        internalStorageRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        new displayImageListTask().execute(internalStorageRoot);
//        getDirFromRoot(internalStorageRoot);

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
        if(m_filesArray!=null) {
            Arrays.sort(m_filesArray, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
        }
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
        } else {
            noMediaText.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
   /*     m_listAdapter = new PictureFIlterAdapter(this, m_item, m_path, m_isRoot);
        listView.setAdapter(m_listAdapter);*/
        /*listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                m_isFile = new File(m_path.get(position));
                if (m_isFile.isDirectory()) {
                    new displayImageListTask().execute(m_isFile.toString());
                } else {
                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String extension = m_isFile.getAbsolutePath().substring(m_isFile.getAbsolutePath().lastIndexOf("."));

                    String type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                    if (type == null)
                        type = "*//**//*";
                    if (type == "image/jpeg") {
                        Intent intent = new Intent(ImageFilterActivity.this, ImageFullScreenActivity.class);
                        intent.putExtra("imgPath", m_path);
                        intent.putExtra("position", position);
                        intent.putExtra("imgFile", m_isFile.getAbsolutePath());
                        intent.putExtra("imageName", m_item.get(position));
                        startActivityForResult(intent, RESULT_DELETED);
                    } else {
                        if (type != "*//**//*") {
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


        });*/
    }

    @Override
    public void onBackPressed() {
        if (m_path.size() > 0) {
            File m_isFile = new File(m_path.get(0));
            if (m_isFile.isDirectory() && !m_isRoot) {
                new displayImageListTask().execute(m_isFile.toString());
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        m_isFile = new File(m_path.get(position));
        if (m_isFile.isDirectory()) {
            new displayImageListTask().execute(m_isFile.toString());
        } else {
            MimeTypeMap map = MimeTypeMap.getSingleton();
            String extension = m_isFile.getAbsolutePath().substring(m_isFile.getAbsolutePath().lastIndexOf("."));

            String type = map.getMimeTypeFromExtension(extension.replace(".", ""));
            if (type == null)
                type = "*//*";
            if (Objects.equals(type, "image/jpeg")) {
                Intent intent = new Intent(ImageFilterActivity.this, ImageFullScreenActivity.class);
                intent.putExtra("imgPath", m_path);
                intent.putExtra("position", position);
                intent.putExtra("imgFile", m_isFile.getAbsolutePath());
                intent.putExtra("imageName", m_item.get(position));
                startActivityForResult(intent, RESULT_DELETED);
            } else {
                if (!Objects.equals(type, "*//*")) {
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

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            nr++;
            toShare.add((String) m_path.get(position));
        } else {
            nr--;
            toShare.remove((String) m_path.get(position));
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
                List<File> fileSharingList = new ArrayList<>();
                for (String shareFiles : toShare) {
                    File fileList = new File(shareFiles);
                    fileSharingList.add(fileList);
                }
                for (File allFiles : fileSharingList) {
                    if (allFiles.isDirectory()) {
                        Toast.makeText(ImageFilterActivity.this, "Please select only files for sharing", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                Utils.shareMultipleFiles(ImageFilterActivity.this, toShare);
                mode.finish();
                return true;
            default:
                return false;
        }
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mMode = null;
        if (listView.isItemChecked(selectedPosition)) {
            listView.setItemChecked(selectedPosition, false);
            selectedPosition = -1;
        }
        listView.setOnItemClickListener(ImageFilterActivity.this);
    }


    public class displayImageListTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            getDirFromRoot(strings[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            loadingIndicator.setVisibility(View.VISIBLE);
            listView.setOnItemClickListener(null);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadingIndicator.setVisibility(View.GONE);
            m_listAdapter = new PictureFIlterAdapter(ImageFilterActivity.this, m_item, m_path, m_isRoot);
            listView.setAdapter(m_listAdapter);
            listView.setOnItemClickListener(ImageFilterActivity.this);
            super.onPostExecute(aVoid);
        }
    }
}
