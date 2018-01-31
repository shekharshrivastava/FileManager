package com.app.ssoft.filemanager.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class InternalExplorerActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {
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
    public ListAdapter m_listAdapter;
    public ListView rl_lvListRoot;
    private String internalStorageRoot;
    private boolean m_isRoot;
    private String rootPath;
    public static final int RESULT_DELETED = 1;
    public static String copiedFileName;
    public static int actionID = 0;
    public static boolean isCutOrCopied = false;
    private Menu menu;
    private boolean isRenameFile;
    private File selectedFile;
    private int position;
//    private File m_isFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_explorer);

        internalStorageRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rl_lvListRoot = findViewById(R.id.rl_lvListRoot);
//        rl_lvListRoot.setOnItemLongClickListener(this);
        registerForContextMenu(rl_lvListRoot);
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
        getSupportActionBar().setSubtitle(p_rootPath);
        m_item = new ArrayList<String>();
        m_isRoot = true;
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
                    if (extension.equals(".JPG")) {
                        extension = ".jpeg";
                    }
                    String type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                    if (type == null)
                        type = "*//*";
                    if (type == "image/jpeg") {
                        Intent intent = new Intent(InternalExplorerActivity.this, ImageFullScreenActivity.class);
                        intent.putExtra("imgPath", m_path);
                        intent.putExtra("position", position);
                        intent.putExtra("imgFile", m_isFile.getAbsolutePath());
                        intent.putExtra("imageName", m_item.get(position));
                        startActivityForResult(intent, RESULT_DELETED);
                    } else {
                        if (type != "*//*") {
                            Uri uri = FileProvider.getUriForFile(InternalExplorerActivity.this, getApplicationContext().getPackageName(), m_isFile);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, type);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        } else {
                            Toast.makeText(InternalExplorerActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
                        }
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(R.menu.explorer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.createFolder:
                isRenameFile = false;
                showChangeLangDialog(isRenameFile, "New Folder", "New", "Create");
                break;
            case R.id.paste:
                try {
                    File destinationFilePath = new File(rootPath + "/" + copiedFileName);
                    boolean isPasted = Utils.paste(this, destinationFilePath);
                    if (isPasted == true) {
                        isCutOrCopied = false;
                        menu.getItem(1).setVisible(false);
                        m_item.add(destinationFilePath.getName());
                        m_path.add(destinationFilePath.getPath());
                        m_listAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Error performing desired operation", Toast.LENGTH_SHORT).show();
                        menu.getItem(1).setVisible(false);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.view:
                break;
        }
        return true;
    }

    public void showChangeLangDialog(final boolean isRenameFile, final String editTextValue, String title, String positiveButtonText) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.create_folder_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
        edt.setText(editTextValue);

        dialogBuilder.setTitle(title);
        dialogBuilder.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!isRenameFile) {
                    createFolder(edt.getText().toString());
                } else {
                    if (selectedFile != null) {
                        String renamedFile = Utils.renameFile(selectedFile, edt.getText().toString(), editTextValue);
                        if (renamedFile != null && !renamedFile.isEmpty()) {
                            m_item.remove(selectedFile.getName());
                            m_path.remove(selectedFile.getPath());
                            m_item.add(edt.getText().toString());
                            m_path.add(renamedFile);
                            m_listAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(InternalExplorerActivity.this, "Error renaming file/folder", Toast.LENGTH_SHORT).show();

                        }
                    } else {
                        Toast.makeText(InternalExplorerActivity.this, "Error renaming file/folder", Toast.LENGTH_SHORT).show();
                    }
                }
                //do something with edt.getText().toString();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void createFolder(String folderName) {
        File newDir = new File(rootPath + "/" + folderName);
        if (!newDir.exists()) {
            newDir.mkdirs();
            m_item.add(newDir.getName());
            m_path.add(newDir.getPath());
            m_listAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(InternalExplorerActivity.this, "Folder already exist", Toast.LENGTH_SHORT).show();
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
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Cut");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Copy");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Rename");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        position = info.position;
        selectedFile = new File(m_path.get(info.position));
        copiedFileName = m_item.get(info.position);
        if (item.getTitle() == "Copy") {
            actionID = 1;
            if (selectedFile.exists()) {
                Utils.copyFile(this, selectedFile);
                if (isCutOrCopied) {
                    menu.getItem(1).setVisible(true);
                } else {
                    menu.getItem(1).setVisible(false);
                }

            }
        } else if (item.getTitle() == "Cut") {
            actionID = 2;
            if (selectedFile.exists()) {
                Utils.cutFile(selectedFile);
                if (isCutOrCopied) {
                    menu.getItem(1).setVisible(true);
                } else {
                    menu.getItem(1).setVisible(false);
                }
            }
        } else if (item.getTitle() == "Delete") {
            if (selectedFile.exists()) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        InternalExplorerActivity.this);
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Are you sure you want to delete this file ?");
                alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isDeleted = Utils.delete(selectedFile);
                        if (isDeleted == true) {
                            m_item.remove(selectedFile.getName());
                            m_path.remove(selectedFile.getPath());
                            m_listAdapter.notifyDataSetChanged();
                            Toast.makeText(InternalExplorerActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(InternalExplorerActivity.this, "Error deleting file/folder", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();

            }
        } else if (item.getTitle() == "Rename") {
            isRenameFile = true;
            showChangeLangDialog(isRenameFile, selectedFile.getName(), "Rename", "Rename");

        }
        return super.onContextItemSelected(item);

    }

 /*   @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        this.menu = menu;
        menu.getItem(1).setVisible(false);
        if (isCutOrCopied == true) {
            menu.getItem(1).setVisible(true);
        } else {
            menu.getItem(1).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }*/

}

