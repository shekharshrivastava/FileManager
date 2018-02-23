package com.app.ssoft.filemanager.Views;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.tuyenmonkey.mkloader.MKLoader;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DownloadFilterActivity extends AppCompatActivity implements AbsListView.MultiChoiceModeListener, AdapterView.OnItemClickListener {

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
    private ListAdapter m_listAdapter;
    private TextView noMediaText;
    private ArrayList<String> toShare;
    private int selectedPosition;
    private ActionMode mMode;
    private MKLoader loadingIndicator;
    private File sharedFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_filter);
        loadingIndicator = findViewById(R.id.loading_indicator);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Downloads");
        noMediaText = findViewById(R.id.noMediaText);
        internalStorageRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        rl_lvListRoot = findViewById(R.id.rl_lvListRoot);
        rl_lvListRoot.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        rl_lvListRoot.setMultiChoiceModeListener(this);
        new displayDownloadList().execute(internalStorageRoot);
//        getDirFromRoot(internalStorageRoot);
    }

    public void getDirFromRoot(String p_rootPath) {
//        getSupportActionBar().setSubtitle(p_rootPath);
        m_item = new ArrayList<String>();
        m_isRoot = true;
        toShare = new ArrayList<>();
        m_hiddenFilesNames = new ArrayList<String>();
        m_hiddenPaths = new ArrayList<String>();
        m_path = new ArrayList<String>();
        m_files = new ArrayList<String>();
        m_filesPath = new ArrayList<String>();
        rootPath = p_rootPath;
        File m_file = new File(p_rootPath);
        File[] m_filesArray = m_file.listFiles();
        m_curDir = p_rootPath;
        //sorting file list in alphabetical order
//        Arrays.sort(m_filesArray);
        Arrays.sort(m_filesArray, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

        for (int i = 0; i < m_filesArray.length; i++) {
            File file = m_filesArray[i];
            if (file.isDirectory()) {
                Arrays.sort(m_filesArray);
                // if dont want to show hidden file
                if (!file.getName().startsWith(".")) {
                    m_item.add(file.getName());
                    m_path.add(file.getPath());
                } else {
                    m_hiddenFilesNames.add(file.getName());
                    m_hiddenPaths.add(file.getPath());
                }
           /*     if (isShowingHiddenFolder) {
                    if (m_item.containsAll(m_hiddenFilesNames) &&
                            m_path.containsAll(m_hiddenPaths)) {
                        m_item.removeAll(m_hiddenFilesNames);
                        m_path.removeAll(m_hiddenPaths);
                        m_item.addAll(m_hiddenFilesNames);
                        m_path.addAll(m_hiddenPaths);
                    }
                } else {
                    if (m_item.containsAll(m_hiddenFilesNames) &&
                            m_path.containsAll(m_hiddenPaths)) {
                        m_item.removeAll(m_hiddenFilesNames);
                        m_path.removeAll(m_hiddenPaths);
                    }
                }*/

            } else {
                if (!file.getName().startsWith(".")) {
                    m_files.add(file.getName());
                    m_filesPath.add(file.getPath());
                } else {
                    m_hiddenFilesNames.add(file.getName());
                    m_hiddenPaths.add(file.getPath());
                }
               /* if (isShowingHiddenFolder) {
                    m_item.addAll(m_hiddenFilesNames);
                    m_path.addAll(m_hiddenPaths);
                } else {
                    if (m_item.containsAll(m_hiddenFilesNames) &&
                            m_path.containsAll(m_hiddenPaths)) {
                        m_item.removeAll(m_hiddenFilesNames);
                        m_path.removeAll(m_hiddenPaths);
                    }
                }
            }*/

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
            rl_lvListRoot.setVisibility(View.GONE);
        } else {
            noMediaText.setVisibility(View.GONE);
            rl_lvListRoot.setVisibility(View.VISIBLE);
        }

     /*   m_listAdapter = new ListAdapter(this, m_item, m_path, m_isRoot);
        rl_lvListRoot.setAdapter(m_listAdapter);*/
        /*rl_lvListRoot.setOnItemClickListener(new AdapterView.OnItemClickListener()

        {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                File m_isFile = new File(m_path.get(position));
                if (m_isFile.isDirectory()) {
                    getDirFromRoot(m_isFile.toString());
                } else {
                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String extension = m_isFile.getAbsolutePath().substring(m_isFile.getAbsolutePath().lastIndexOf("."));
                    if (extension.equals(".JPG")) {
                        extension = ".jpeg";
                    }
                    String type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                    if (type == null)
                        type = "*//**//*";
                    if (type == "image/jpeg" || type == "image/png") {
                        Intent intent = new Intent(DownloadFilterActivity.this, ImageFullScreenActivity.class);
                        intent.putExtra("imgPath", m_path);
                        intent.putExtra("position", position);
                        intent.putExtra("imgFile", m_isFile.getAbsolutePath());
                        intent.putExtra("imageName", m_item.get(position));
                        startActivityForResult(intent, RESULT_DELETED);
                    } else {
                        if (type != "*//**//*") {
                            Uri uri = FileProvider.getUriForFile(DownloadFilterActivity.this, getApplicationContext().getPackageName(), m_isFile);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, type);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        } else {
                            Toast.makeText(DownloadFilterActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }


        });*/
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
            toShare.add((String) m_path.get(position));
        } else {
            toShare.remove((String) m_path.get(position));
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
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
                List<File> fileSharingList = new ArrayList<>();
                for (String shareFiles : toShare) {
                    File fileList = new File(shareFiles);
                    fileSharingList.add(fileList);
                }
                for (File allFiles : fileSharingList) {
                    if (allFiles.isDirectory()) {
                        Toast.makeText(DownloadFilterActivity.this, "Please select only files for sharing", Toast.LENGTH_SHORT).show();
                        return false;
                    } else {
                        Utils.shareMultipleFiles(DownloadFilterActivity.this, toShare);
                        mode.finish();
                    }
                }


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
        rl_lvListRoot.setOnItemClickListener(DownloadFilterActivity.this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        rl_lvListRoot.setItemChecked(position, false);
        if (mMode != null) {
            mMode.finish();
        } else {
            File m_isFile = new File(m_path.get(position));
            if (m_isFile.isDirectory()) {
//                getDirFromRoot(m_isFile.toString());
                new displayDownloadList().execute(m_isFile.toString());
            } else {
                MimeTypeMap map = MimeTypeMap.getSingleton();
                String extension = m_isFile.getAbsolutePath().substring(m_isFile.getAbsolutePath().lastIndexOf("."));

                String type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                if (type == null)
                    type = "*//*";
                if (type != "*//*") {
                    Uri uri = FileProvider.getUriForFile(DownloadFilterActivity.this, getApplicationContext().getPackageName(), m_isFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, type);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Toast.makeText(DownloadFilterActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public class displayDownloadList extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            getDirFromRoot(strings[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            loadingIndicator.setVisibility(View.VISIBLE);
            rl_lvListRoot.setOnItemClickListener(null);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadingIndicator.setVisibility(View.GONE);
            m_listAdapter = new ListAdapter(DownloadFilterActivity.this, m_item, m_path, m_isRoot);
            rl_lvListRoot.setAdapter(m_listAdapter);
            rl_lvListRoot.setOnItemClickListener(DownloadFilterActivity.this);
            super.onPostExecute(aVoid);
        }
    }
}
