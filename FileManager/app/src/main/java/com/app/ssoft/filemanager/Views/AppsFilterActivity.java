package com.app.ssoft.filemanager.Views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class AppsFilterActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_filter);
        getSupportActionBar().setTitle("App");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        internalStorageRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        rl_lvListRoot = findViewById(R.id.rl_lvListRoot);

        getDirFromRoot(internalStorageRoot);
    }

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
        m_curDir = p_rootPath;
        //sorting file list in alphabetical order
//        Arrays.sort(m_filesArray);
        Arrays.sort(m_filesArray, LastModifiedFileComparator.LASTMODIFIED_REVERSE);

        for (int i = 0; i < m_filesArray.length; i++) {
            File file = m_filesArray[i];
            if (!file.isDirectory()) {
                Arrays.sort(m_filesArray);
                if (file.getName().endsWith(".apk")) {
                    if (m_files.contains(file.getName()) && m_filesPath.contains(file.getPath())) {
                        m_files.remove(file.getName());
                        m_filesPath.remove(file.getPath());
                    }
                    m_files.add(file.getName());
                    m_filesPath.add(file.getPath());
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
                        if (type == "application/vnd.android.package-archive") {
                            PackageManager pm = getPackageManager();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Uri apkUri = FileProvider.getUriForFile(AppsFilterActivity.this, getApplicationContext().getPackageName(), m_isFile);
                                Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                                intent.setData(apkUri);
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(intent);
                            } else {
                                Uri apkUri = Uri.fromFile(m_isFile);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(apkUri, type);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        } else {
                            if (type != "*//*") {
                                Uri uri = FileProvider.getUriForFile(AppsFilterActivity.this, getApplicationContext().getPackageName(), m_isFile);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, type);
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(intent);
                            } else {
                                Toast.makeText(AppsFilterActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }


            });
        }
    }

