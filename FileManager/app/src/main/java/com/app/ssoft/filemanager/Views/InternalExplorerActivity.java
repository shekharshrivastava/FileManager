package com.app.ssoft.filemanager.Views;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;

import com.app.ssoft.filemanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class InternalExplorerActivity extends AppCompatActivity {
    private String m_root = Environment.getExternalStorageDirectory().getPath();
    private Toolbar toolbar;
    private FragmentManager fragmentManager = null;
    private FragmentTransaction fragmentTransaction = null;
    private ArrayList<String> m_item;
    private ArrayList<String> m_path;
    private ArrayList<String> m_files;
    private ArrayList<String> m_filesPath;
    private String m_curDir;
    private AdapterView m_RootList;
    private ListAdapter m_listAdapter;
    private ListView rl_lvListRoot;
    private String internalStorageRoot;
    private boolean m_isRoot;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_explorer);

        internalStorageRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rl_lvListRoot = findViewById(R.id.rl_lvListRoot);
        getDirFromRoot(internalStorageRoot);

    /*    fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        mDirectoryFragment = new DirectoryFragment();
        mDirectoryFragment.setDelegate(new DirectoryFragment.DocumentSelectActivityDelegate() {

            @Override
            public void startDocumentSelectActivity() {

            }

            @Override
            public void didSelectFiles(DirectoryFragment activity,
                                       ArrayList<String> files) {
                mDirectoryFragment.showErrorBox(files.get(0).toString());
            }

            @Override
            public void updateToolBarName(String name) {
                toolbar.setTitle(name);

            }
        });
        fragmentTransaction.add(R.id.fragment_container, mDirectoryFragment, "" + mDirectoryFragment.toString());
        fragmentTransaction.commit();
*/
    }

    /* @Override
     protected void onDestroy() {
         mDirectoryFragment.onFragmentDestroy();
         super.onDestroy();
     }

     @Override
     public void onBackPressed() {
         if (mDirectoryFragment.onBackPressed_()) {
             super.onBackPressed();
         }
     }*/
///get directories and files from selected path
    public void getDirFromRoot(String p_rootPath) {
        m_item = new ArrayList<String>();
        m_isRoot = true;
        m_path = new ArrayList<String>();
        m_files = new ArrayList<String>();
        m_filesPath = new ArrayList<String>();
        File m_file = new File(p_rootPath);
        File[] m_filesArray = m_file.listFiles();
        if (!p_rootPath.equals(m_root)) {
            m_item.add("../");
            m_path.add(m_file.getParent());
            m_isRoot = false;
        }
        m_curDir = p_rootPath;
        //sorting file list in alphabetical order
        Arrays.sort(m_filesArray);
        for (int i = 0; i < m_filesArray.length; i++) {
            File file = m_filesArray[i];
            if (file.isDirectory()) {
                m_item.add(file.getName());
                m_path.add(file.getPath());
            } else {
                m_files.add(file.getName());
                m_filesPath.add(file.getPath());
            }
        }
        for (String m_AddFile : m_files) {
            m_item.add(m_AddFile);
        }
        for (String m_AddPath : m_filesPath) {
            m_path.add(m_AddPath);
        }
        m_listAdapter = new ListAdapter(this, m_item, m_path, m_isRoot);
        rl_lvListRoot.setAdapter(m_listAdapter);
        rl_lvListRoot.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                File m_isFile = new File(m_path.get(position));
                if (m_isFile.isDirectory()) {
                    getDirFromRoot(m_isFile.toString());
                } else {
                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String extension = m_isFile.getAbsolutePath().substring(m_isFile.getAbsolutePath().lastIndexOf("."));
                    String type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                    if (type == null)
                        type = "*//*";
                    if (type == "image/jpeg") {
                        Intent intent = new Intent(InternalExplorerActivity.this, ImageFullScreenActivity.class);
                        intent.putExtra("imgPath", m_path);
                        intent.putExtra("position", position);
                        intent.putExtra("imgFile", m_isFile.getAbsolutePath());
                        intent.putExtra("imageName", m_item.get(position));
                        startActivity(intent);
                    } else {
                        Uri uri = FileProvider.getUriForFile(InternalExplorerActivity.this, getApplicationContext().getPackageName(), m_isFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, type);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    }

                }
            }


        });
    }


    @Override
    public void onBackPressed() {
        File m_isFile = new File(m_path.get(0));
        if (m_isFile.isDirectory() && !m_isRoot) {
            getDirFromRoot(m_isFile.toString());
        } else {
            finish();
        }
    }
}
