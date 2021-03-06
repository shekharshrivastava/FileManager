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
import java.util.Objects;

public class DocumentFIlterActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener {
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
    private int selectedPosition;
    private android.view.ActionMode cabMode;
    private ActionMode.Callback mCallback;
    private ActionMode mMode;
    private ArrayList<String> toShare;
    private MKLoader loadingIndicator;
    private int nr = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_filter);
/*      Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        getSupportActionBar().setTitle("Documents");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadingIndicator = findViewById(R.id.loading_indicator);
        toShare = new ArrayList<>();
        noMediaText = findViewById(R.id.noMediaText);
        internalStorageRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        rl_lvListRoot = findViewById(R.id.rl_lvListRoot);
        rl_lvListRoot.setOnItemClickListener(this);
        rl_lvListRoot.setOnItemLongClickListener(this);
        rl_lvListRoot.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        rl_lvListRoot.setMultiChoiceModeListener(this);

        new displayDocList().execute(internalStorageRoot);

//        getDirFromRoot(internalStorageRoot);

      /*  mCallback = new ActionMode.Callback() {
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
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mMode = null;
                rl_lvListRoot.setItemChecked(selectedPosition, false);
                selectedPosition = -1;
                rl_lvListRoot.setOnItemClickListener(DocumentFIlterActivity.this);
            }

        };*/
    }


    public void getDirFromRoot(String p_rootPath) {
        // getSupportActionBar().setSubtitle(p_rootPath);
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
                if (file.getName().endsWith(".pdf") || file.getName().endsWith(".doc") ||
                        file.getName().endsWith(".txt") || file.getName().endsWith(".xls")) {
                    if (m_files.contains(file.getName()) && m_filesPath.contains(file.getPath())) {
                        m_files.remove(file.getName());
                        m_filesPath.remove(file.getPath());
                    }
                    m_files.add(file.getName());
                    m_filesPath.add(file.getPath());
                }
            } else {
                File[] dirFiles = file.listFiles();
                for (File docFiles : dirFiles) {
                    if (!docFiles.isDirectory()) {
                        if (docFiles.getName().endsWith(".pdf") || docFiles.getName().endsWith(".doc") ||
                                docFiles.getName().endsWith(".txt") || docFiles.getName().endsWith(".xls")) {
                            if (m_files.contains(file.getName()) && m_filesPath.contains(file.getPath())) {
                                m_files.remove(docFiles.getName());
                                m_filesPath.remove(docFiles.getPath());
                            }
                            m_files.add(docFiles.getName());
                            m_filesPath.add(docFiles.getPath());
                        }
                    }/*else {
                        do {
                            File[] fileUnderDir = docFiles.listFiles();
                            for (File docFileSubDir : fileUnderDir) {
                                if (!docFileSubDir.isDirectory()) {
                                    if (docFiles.getName().endsWith(".pdf") || docFiles.getName().endsWith(".doc") ||
                                            docFiles.getName().endsWith(".txt") || docFiles.getName().endsWith(".xls")){
                                        if (m_files.contains(docFiles.getName()) && m_filesPath.contains(docFiles.getPath())) {
                                            m_files.remove(docFiles.getName());
                                            m_filesPath.remove(docFiles.getPath());
                                        }
                                        m_files.add(docFiles.getName());
                                        m_filesPath.add(docFiles.getPath());
                                    }
                                }
                            }
                        }

                        while (!docFiles.isDirectory());


                    }*/
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
            rl_lvListRoot.setVisibility(View.GONE);
        } else {
            noMediaText.setVisibility(View.GONE);
            rl_lvListRoot.setVisibility(View.VISIBLE);
        }
      /*  m_listAdapter = new ListAdapter(this, m_item, m_path, m_isRoot);
        rl_lvListRoot.setAdapter(m_listAdapter);
        rl_lvListRoot.setOnItemClickListener(this);*/
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
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (mMode != null) {
            rl_lvListRoot.setOnItemClickListener(null);
            return false;
        } else {
            mMode = startActionMode(mCallback);
            selectedPosition = position;
            rl_lvListRoot.setItemChecked(position, true);
            rl_lvListRoot.setOnItemClickListener(this);
        }
//        cabMode  = startActionMode();
        return true;

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
                new displayDocList().execute(internalStorageRoot);
            } else {
                MimeTypeMap map = MimeTypeMap.getSingleton();
                String extension = m_isFile.getAbsolutePath().substring(m_isFile.getAbsolutePath().lastIndexOf("."));

                String type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                if (type == null)
                    type = "*//*";
                if (!Objects.equals(type, "*//*")) {
                    Uri uri = FileProvider.getUriForFile(DocumentFIlterActivity.this, getApplicationContext().getPackageName(), m_isFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, type);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    Toast.makeText(DocumentFIlterActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        mMode = null;
        if (rl_lvListRoot.isItemChecked(selectedPosition)) {
            rl_lvListRoot.setItemChecked(selectedPosition, false);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
                Utils.shareMultipleFiles(DocumentFIlterActivity.this, toShare);
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
        rl_lvListRoot.setOnItemClickListener(DocumentFIlterActivity.this);
    }

    public class displayDocList extends AsyncTask<String, Void, Void> {

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
            m_listAdapter = new ListAdapter(DocumentFIlterActivity.this, m_item, m_path, m_isRoot);
            rl_lvListRoot.setAdapter(m_listAdapter);
            rl_lvListRoot.setOnItemClickListener(DocumentFIlterActivity.this);
            super.onPostExecute(aVoid);
        }
    }
}

