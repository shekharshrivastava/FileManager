package com.app.ssoft.filemanager.Views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.app.ssoft.filemanager.Model.PastedDetails;
import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Constants;
import com.app.ssoft.filemanager.Utils.Utils;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tuyenmonkey.mkloader.MKLoader;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class InternalExplorerActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, AbsListView.MultiChoiceModeListener {
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
    private boolean hideFolders = true;
    private ArrayList<String> m_hiddenFilesNames;
    private ArrayList<String> m_hiddenPaths;
    private boolean isShowingHiddenFolder;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private MKLoader loadingIndicator;
    private String type;
    private AdView mAdView;
    //    private File m_isFile;
    private ActionMode mMode;
    private ArrayList<String> selectedFiles;
    private int nr = 0;
    private int selectedPosition;
    private Menu cabMenu;
    private ArrayList<String> imagesList;
    private int imagePos;
    private boolean isLocked = false;
    private SharedPreferences pwdSharedPrefs;
    private SharedPreferences sharedPrefs;
    private Dialog enterPwdDialog;
    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private ActionMode actionMode;
    private SharedPreferences prefs;
    private TapTargetSequence sequence1;
    private CompressFiles mCompressFiles;
    private TextView mProgressView;
    private static final int BUFFER = 2048;
    private String zipFileName;
    private File m_isFile;
    private final static String ALGO_RANDOM_NUM_GENERATOR = "SHA1PRNG";
    private final static String ALGO_SECRET_KEY_GENERATOR = "AES";
    private final static int IV_LENGTH = 16;
    private File outFile_dec;
    private File outFile;
    private SecretKey key;
    private byte[] keyData;
    private SecretKeySpec key2;
    private byte[] iv;
    private IvParameterSpec paramSpec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internal_explorer);
        loadingIndicator = findViewById(R.id.loading_indicator);
        mAdView = findViewById(R.id.adView);
        mProgressView = findViewById(R.id.progress_text_view);
        AdRequest adRequest = new AdRequest.Builder()
//         Lenovo k8+
                .addTestDevice("7B739675F49D587BB5D2F85182CECA54").build();
//        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

//        adRequest.addTestDevice();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = getApplicationContext().getSharedPreferences("menuPref", 0);
        editor = pref.edit();
        internalStorageRoot = Environment.getExternalStorageDirectory().getAbsolutePath();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rl_lvListRoot = findViewById(R.id.rl_lvListRoot);
        rl_lvListRoot.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        rl_lvListRoot.setMultiChoiceModeListener(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPrefs = getSharedPreferences(Constants.SHARED_PREF_FOLDER_LOCK_MODE, MODE_PRIVATE);
        pwdSharedPrefs = getSharedPreferences(Constants.SHARED_PREF_SET_PASSWORD, MODE_PRIVATE);
        registerForContextMenu(rl_lvListRoot);
        new getAllFilesFromInternalStorageTask().execute(internalStorageRoot);
        try {
             key = KeyGenerator.getInstance(ALGO_SECRET_KEY_GENERATOR).generateKey();
           keyData = key.getEncoded();
             key2 = new SecretKeySpec(keyData, 0, keyData.length, ALGO_SECRET_KEY_GENERATOR);
          iv = new byte[IV_LENGTH];
            SecureRandom.getInstance(ALGO_RANDOM_NUM_GENERATOR).nextBytes(iv);
             paramSpec = new IvParameterSpec(iv);
        } catch (Exception ex) {

        }
    }

    ///get directories and files from selected path
    public void getDirFromRoot(final String p_rootPath) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getSupportActionBar().setSubtitle(p_rootPath);
            }
        });
        isShowingHiddenFolder = pref.getBoolean("isShowingHiddenFiles", false);
        m_item = new ArrayList<String>();
        selectedFiles = new ArrayList<>();
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
            if (file.isDirectory()) {
                Arrays.sort(m_filesArray);
                // if dont want to show hidden file
//                file.getName().startsWith(".")
                if (!file.isHidden()) {
                    m_item.remove(file.getName());
                    m_item.add(file.getName());

                    m_path.remove(file.getPath());
                    m_path.add(file.getPath());
                } else {
                    m_hiddenFilesNames.add(file.getName());
                    m_hiddenPaths.add(file.getPath());
                }
                if (isShowingHiddenFolder) {
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
                }

            } else {
//                file.getName().startsWith(".")
                if (!file.isHidden()) {
                    m_files.remove(file.getName());
                    m_files.add(file.getName());

                    m_filesPath.remove(file.getPath());
                    m_filesPath.add(file.getPath());
                } else {
                    m_hiddenFilesNames.add(file.getName());
                    m_hiddenPaths.add(file.getPath());
                }
                if (isShowingHiddenFolder) {
                    m_item.addAll(m_hiddenFilesNames);
                    m_path.addAll(m_hiddenPaths);
                } else {
                    if (m_item.containsAll(m_hiddenFilesNames) &&
                            m_path.containsAll(m_hiddenPaths)) {
                        m_item.removeAll(m_hiddenFilesNames);
                        m_path.removeAll(m_hiddenPaths);
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


    }


    @Override
    public void onBackPressed() {
        File m_isFile = new File(m_path.get(0));
        if (m_isFile.isDirectory() && !m_isRoot) {
            new getAllFilesFromInternalStorageTask().execute(m_isFile.toString());
//            getDirFromRoot(m_isFile.toString());
        } else {
            finish();
            editor.clear();
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
            case android.R.id.home:
                File m_isFile = new File(m_path.get(0));
                if (m_isFile.isDirectory() && !m_isRoot) {
                    new getAllFilesFromInternalStorageTask().execute(m_isFile.toString());
//                    getDirFromRoot(m_isFile.toString());
                } else {
                    finish();
                    editor.clear();
                }
                break;
            case R.id.createFolder:
                isRenameFile = false;
                showChangeLangDialog(isRenameFile, "New Folder", "New", "Create");
                break;
            case R.id.paste:
                try {
                    File destinationFilePath = new File(rootPath + "/");
                    ArrayList<PastedDetails> pastedDetailList = Utils.paste(this, destinationFilePath);
                    for (PastedDetails pasteDetail : pastedDetailList) {
                        if (pasteDetail.isPasted == true && pasteDetail.getDestinationFile() != null) {
                            isCutOrCopied = false;
                            menu.getItem(1).setVisible(false);
                            m_item.add(pasteDetail.getDestinationFile().getName());
                            m_path.add(pasteDetail.getDestinationFile().getPath());
                            selectedFiles = new ArrayList<>();
                            m_listAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(this, "Error performing desired operation", Toast.LENGTH_SHORT).show();
                            menu.getItem(1).setVisible(false);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error performing desired operation", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.view:
                break;

            case R.id.showFolders:
                showHiddenFolder();
                break;
            case R.id.hideFolders:
                hideHiddenFolders();
                break;
        }
        return true;
    }

    public void showChangeLangDialog(final boolean isRenameFile, final String editTextValue, String title, String positiveButtonText) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(InternalExplorerActivity.this, R.style.myDialogTheme));
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

    public void showRenameDialog(final boolean isRenameFile, final File selectedFile, final String editTextValue, String title, String positiveButtonText) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(InternalExplorerActivity.this, R.style.myDialogTheme));

//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
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


    /*    *//*   @Override
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
    public void showHiddenFolder() {
        if ((m_hiddenPaths != null && !m_hiddenPaths.isEmpty()) &&
                ((m_hiddenFilesNames != null && !m_hiddenFilesNames.isEmpty()))) {
            isShowingHiddenFolder = true;
            editor.putBoolean("isShowingHiddenFiles", true);

            if (m_item.containsAll(m_hiddenFilesNames) && m_path.containsAll(m_hiddenPaths)) {
                m_item.removeAll(m_hiddenFilesNames);
                m_path.removeAll(m_hiddenPaths);
            }
            m_item.addAll(m_hiddenFilesNames);
            m_path.addAll(m_hiddenPaths);
            m_listAdapter.notifyDataSetChanged();
            menu.getItem(4).setEnabled(true);
            menu.getItem(3).setEnabled(false);
            editor.commit();
        } else {
            Toast.makeText(this, "This folder does'nt contains any hidden files", Toast.LENGTH_SHORT).show();
        }
    }

    public void hideHiddenFolders() {
        if ((m_hiddenPaths != null && !m_hiddenPaths.isEmpty()) &&
                ((m_hiddenFilesNames != null && !m_hiddenFilesNames.isEmpty()))) {
            isShowingHiddenFolder = false;
            if (m_item.containsAll(m_hiddenFilesNames) && m_path.containsAll(m_hiddenPaths)) {
                editor.putBoolean("isShowingHiddenFiles", false);
                menu.getItem(4).setEnabled(false);
                menu.getItem(3).setEnabled(true);
                m_item.removeAll(m_hiddenFilesNames);
                m_path.removeAll(m_hiddenPaths);
                m_listAdapter.notifyDataSetChanged();
                editor.commit();
            }
        }
    }

    @Override
    protected void onDestroy() {
        editor.putBoolean("isShowingHiddenFiles", false);
        editor.commit();
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        imagesList = new ArrayList<>();
        m_isFile = new File(m_path.get(position));

        isLocked = prefs.getBoolean(m_isFile.getAbsolutePath(), false);
        if (!isLocked) {
            if (m_isFile.isDirectory()) {
                new getAllFilesFromInternalStorageTask().execute(m_isFile.toString());
//            getDirFromRoot(m_isFile.toString());
            } else {
                if (getIntent().getAction() == null) {
                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    if (m_isFile.getAbsolutePath().contains(".")) {
                        String extension = m_isFile.getAbsolutePath().substring(m_isFile.getAbsolutePath().lastIndexOf("."));
                        if (extension.equalsIgnoreCase(".JPG")) {
                            extension = ".jpeg";
                        }
                        type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                    }
                    if (type == null)
                        type = "*//*";
                    if (Objects.equals(type, "image/jpeg")) {
                        Intent intent = new Intent(InternalExplorerActivity.this, ImageFullScreenActivity.class);
                        for (String imageFiles :
                                m_path) {
                            File fileImageType = new File(imageFiles);
                            if (!fileImageType.isDirectory()) {
                                imagesList.add(imageFiles);
                            }
                        }
                        intent.putExtra("imgPath", imagesList);
                        intent.putExtra("position", getImagePosition(m_isFile.getAbsolutePath()));
                        intent.putExtra("imgFile", m_isFile.getAbsolutePath());
                        intent.putExtra("imageName", m_item.get(position));
                        startActivityForResult(intent, RESULT_DELETED);
                    } else if (Objects.equals(type, "video/mp4")) {
                        Intent intent = new Intent(this, VideoPlayerActivity.class);
                        intent.putExtra("path", m_path.get(position));
                        intent.putExtra("title", m_item.get(position));
                        startActivity(intent);
                    } else {
                        try {
                            if (!Objects.equals(type, "*//*")) {
                                Uri uri = FileProvider.getUriForFile(InternalExplorerActivity.this, getApplicationContext().getPackageName(), m_isFile);
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uri, type);
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                startActivity(intent);
                            } else {
                                Toast.makeText(InternalExplorerActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(InternalExplorerActivity.this, "No app found to open selected file", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    MimeTypeMap map = MimeTypeMap.getSingleton();
                    String extension = m_isFile.getAbsolutePath().substring(m_isFile.getAbsolutePath().lastIndexOf("."));
                    if (extension.equalsIgnoreCase(".JPG")) {
                        extension = ".jpeg";
                    }
                    type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                    Uri fileUri = FileProvider.getUriForFile(InternalExplorerActivity.this, getApplicationContext().getPackageName(), m_isFile);
                    /*String uri = "file://" + m_isFile.getPath();
                    Uri uploadUri = Uri.parse(uri);*/
                    Intent uploadIntent = ShareCompat.IntentBuilder.from(this)
                            .setType(type)
                            .setStream(fileUri)
                            .getIntent();
//                            .setPackage("com.google.android.apps.docs");
                    startActivity(uploadIntent);
                /*    Uri fileUri = FileProvider.getUriForFile(InternalExplorerActivity.this, getApplicationContext().getPackageName(), m_isFile)
                    theIntent.setData(fileUri.pat);
                    setResult(RESULT_OK,theIntent);*/
                    finish();
                }
            }
        } else {
            Toast.makeText(this, "This folder is locked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            nr++;
            selectedFiles.add((String) m_path.get(position));
        } else {
            nr--;
            selectedFiles.remove((String) m_path.get(position));
        }
        if (selectedFiles.size() > 1) {
            cabMenu.getItem(0).setVisible(true);
            cabMenu.getItem(1).setVisible(true);
            cabMenu.getItem(2).setVisible(true);
            cabMenu.getItem(3).setVisible(false);
            cabMenu.getItem(4).setVisible(true);
            cabMenu.getItem(5).setVisible(false);
            cabMenu.getItem(6).setVisible(false);
            cabMenu.getItem(7).setVisible(false);
        } else {
            if (selectedFiles.size() == 1) {
                isLocked = prefs.getBoolean(selectedFiles.get(0), false);
                if (isLocked) {
                    cabMenu.getItem(0).setVisible(false);
                    cabMenu.getItem(1).setVisible(false);
                    cabMenu.getItem(2).setVisible(false);
                    cabMenu.getItem(3).setVisible(false);
                    cabMenu.getItem(4).setVisible(false);
                    cabMenu.getItem(5).setVisible(false);
                    cabMenu.getItem(6).setVisible(false);
                    cabMenu.getItem(7).setVisible(true);
                    cabMenu.getItem(7).setEnabled(true);
                    cabMenu.getItem(8).setVisible(false);
                } else {
                    cabMenu.getItem(0).setVisible(true);
                    cabMenu.getItem(1).setVisible(true);
                    cabMenu.getItem(2).setVisible(true);
                    cabMenu.getItem(3).setVisible(true);
                    cabMenu.getItem(4).setVisible(true);
                    cabMenu.getItem(5).setVisible(true);
                    cabMenu.getItem(6).setVisible(true);
                    cabMenu.getItem(6).setEnabled(true);
                    cabMenu.getItem(7).setVisible(false);
                    cabMenu.getItem(8).setVisible(true);
                }
            }


        }


        mode.setTitle(nr + " Selected");
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        nr = 0;
        this.cabMenu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.cab_menu_internal, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_share:
                nr = 0;
                Utils.shareMultipleFiles(InternalExplorerActivity.this, selectedFiles);
                selectedFiles = new ArrayList<>();
                mode.finish();

                return true;

            case R.id.action_delete:
//                if (selectedFile.exists()) {
                AlertDialog.Builder alertDialogDelete = new AlertDialog.Builder(new ContextThemeWrapper(InternalExplorerActivity.this, R.style.myDialogTheme));
              /*  final AlertDialog.Builder alertDialogDelete = new AlertDialog.Builder(
                        InternalExplorerActivity.this);*/
                alertDialogDelete.setTitle("Alert");
                alertDialogDelete.setMessage("Are you sure you want to delete this file ?");
                alertDialogDelete.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isDeleted = Utils.deleteMultipleFiles(InternalExplorerActivity.this, selectedFiles);
                        if (isDeleted == true) {
                            for (String deletedSelectedFiles : selectedFiles) {
                                File file = new File(deletedSelectedFiles);
                                m_item.remove(file.getName());
                                m_path.remove(file.getPath());
                                selectedFiles = new ArrayList<>();
                                mode.finish();
                            }

                            m_listAdapter.notifyDataSetChanged();
                            Toast.makeText(InternalExplorerActivity.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(InternalExplorerActivity.this, "Error deleting file/folder", Toast.LENGTH_SHORT).show();
                        }
                    }

                });
                alertDialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogDelete.show();
                break;
//                }
            case R.id.action_copy:
                actionID = 1;
//                if (selectedFile.exists()) {
                Utils.copyMultipleFile(this, selectedFiles);
                mode.finish();
                if (isCutOrCopied) {
                    menu.getItem(1).setVisible(true);
                } else {
                    menu.getItem(1).setVisible(false);
                }

//                }
                break;
            case R.id.action_cut:
                actionID = 2;
//                if (selectedFile.exists()) {
                Utils.cutMultipleFiles(InternalExplorerActivity.this, selectedFiles);
                mode.finish();
                if (isCutOrCopied) {
                    menu.getItem(1).setVisible(true);
                } else {
                    menu.getItem(1).setVisible(false);
                }
//                }
                break;

            case R.id.action_rename:
                isRenameFile = true;
                if (selectedFiles != null && selectedFiles.size() == 1) {
                    File selectedFile = new File(selectedFiles.get(0));
                    showRenameDialog(isRenameFile, selectedFile, selectedFile.getName(), "Rename", "Rename");
                    selectedFiles = new ArrayList<>();
                    mode.finish();
                }
                break;

            case R.id.action_open:
                if (selectedFiles != null && selectedFiles.size() == 1) {
                    File selectedFile = new File(selectedFiles.get(0));
                    if (selectedFile.exists() && !selectedFile.isDirectory()) {
                        MimeTypeMap map = MimeTypeMap.getSingleton();
                        if (selectedFile.getAbsolutePath().contains(".")) {
                            String extension = selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().lastIndexOf("."));
                            if (extension.equals(".JPG")) {
                                extension = ".jpeg";
                            }
                            type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                            Uri uri = FileProvider.getUriForFile(InternalExplorerActivity.this, getApplicationContext().getPackageName(), selectedFile);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, type);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(this, "Please click folder to open", Toast.LENGTH_SHORT).show();
                    }
                    mode.finish();
                }
                break;

            case R.id.action_lock:
                final File lockedSelectedFile = new File(selectedFiles.get(0));
                outFile = new File(lockedSelectedFile.getAbsolutePath().substring(0, lockedSelectedFile.getAbsolutePath().lastIndexOf("/"))+"/"+lockedSelectedFile.getName());

                if (pwdSharedPrefs.getString(Constants.password_input, null) != null) {
                    SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_FOLDER_LOCK_MODE, MODE_PRIVATE).edit();
                    editor.putBoolean(Constants.is_folder_locked_enabled, true);
                    editor.commit();
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(InternalExplorerActivity.this);
                    prefs.edit().putBoolean(lockedSelectedFile.getAbsolutePath(), true).commit();
                    if (lockedSelectedFile.isDirectory()) {
                        try {
                            File output = new File(lockedSelectedFile.getAbsolutePath(), ".nomedia");
                            output.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                       /* try {
                            Encrypter.encrypt(key, paramSpec,
                                    new FileInputStream(lockedSelectedFile), new FileOutputStream(outFile,false));
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }*/

                        mode.finish();
                    }
                }else {
// get prompts.xml view
                    LayoutInflater li = LayoutInflater.from(InternalExplorerActivity.this);
                    View promptsView = li.inflate(R.layout.set_pwd_dialog, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(InternalExplorerActivity.this, R.style.myDialogTheme));


                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userNewPassword = (EditText) promptsView
                            .findViewById(R.id.etNewPwd);
                    final EditText userConfirmPassword = (EditText) promptsView
                            .findViewById(R.id.etCnfrmPwd);
                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            // get user input and set it to result
                                            // edit text
                                            if (userNewPassword.getText().toString().length() == 4) {
                                                if (userNewPassword.getText().toString().equals(userConfirmPassword.getText().toString())) {
                                                    SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_SET_PASSWORD, MODE_PRIVATE).edit();
                                                    editor.putString(Constants.password_input, userNewPassword.getText().toString());
                                                    editor.commit();
                                                    SharedPreferences.Editor prefLockEditor = getSharedPreferences(Constants.SHARED_PREF_FOLDER_LOCK_MODE, MODE_PRIVATE).edit();
                                                    prefLockEditor.putBoolean(Constants.is_folder_locked_enabled, true);
                                                    prefLockEditor.commit();
                                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(InternalExplorerActivity.this);
                                                    prefs.edit().putBoolean(lockedSelectedFile.getAbsolutePath(), true).commit();
                                                    if (lockedSelectedFile.isDirectory()) {
                                                        try {
                                                            File output = new File(lockedSelectedFile.getAbsolutePath(), ".nomedia");
                                                            output.createNewFile();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }/*else {
                                                        try {
                                                            Encrypter.encrypt(key2, paramSpec,
                                                                    new FileInputStream(lockedSelectedFile), new FileOutputStream(outFile_dec));
                                                        }catch (Exception ex){
                                                            ex.printStackTrace();
                                                        }
                                                        mode.finish();
                                                    }*/
                                                } else {
                                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(InternalExplorerActivity.this);
                                                    prefs.edit().putBoolean(lockedSelectedFile.getAbsolutePath(), false).commit();
                                                    mode.finish();
                                                    Toast.makeText(InternalExplorerActivity.this, "Password does'nt match", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(InternalExplorerActivity.this);
                                                prefs.edit().putBoolean(lockedSelectedFile.getAbsolutePath(), false).commit();
                                                mode.finish();
                                                Toast.makeText(InternalExplorerActivity.this, "Password must be of 4 numbers", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(InternalExplorerActivity.this);
                                            prefs.edit().putBoolean(lockedSelectedFile.getAbsolutePath(), false).commit();
                                            mode.finish();

                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }

                break;

            case R.id.action_unlock:
                showUnlockDialog(mode);
                break;
            case R.id.zip_file:
                mCompressFiles = new CompressFiles();
                mCompressFiles.execute(selectedFiles);
                mode.finish();
                break;
               /* mCompressFiles = new CompressFiles();
                mCompressFiles.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);*/
            default:
        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mMode = null;
        if (rl_lvListRoot.isItemChecked(selectedPosition)) {
            rl_lvListRoot.setItemChecked(selectedPosition, false);
            selectedPosition = -1;
        }
        rl_lvListRoot.setOnItemClickListener(InternalExplorerActivity.this);
        selectedFiles = new ArrayList<>();

    }

    public class getAllFilesFromInternalStorageTask extends AsyncTask<String, Void, Void> {

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
            m_listAdapter = new ListAdapter(InternalExplorerActivity.this, m_item, m_path, m_isRoot);
            rl_lvListRoot.setAdapter(m_listAdapter);
            rl_lvListRoot.setOnItemClickListener(InternalExplorerActivity.this);
            super.onPostExecute(aVoid);
        }

    }


    /*
     *
     * Old version context menu option
     *
     * */

   /* @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Move");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Copy");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Rename");
        menu.add(0, v.getId(), 0, "Share");
        menu.add(0, v.getId(), 0, "Open with");
    }*/

/*    @Override
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
        } else if (item.getTitle() == "Move") {
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
//            showChangeLangDialog(isRenameFile, selectedFile.getName(), "Rename", "Rename");

        } else if (item.getTitle() == "Share") {
            if (selectedFile.exists() && !selectedFile.isDirectory()) {
                Utils.shareFIle(this, selectedFile);
            } else {
                Toast.makeText(this, "Please select files to share", Toast.LENGTH_SHORT).show();
            }
        } else if (item.getTitle() == "Open with") {
            if (selectedFile.exists() && !selectedFile.isDirectory()) {
                MimeTypeMap map = MimeTypeMap.getSingleton();
                if (selectedFile.getAbsolutePath().contains(".")) {
                    String extension = selectedFile.getAbsolutePath().substring(selectedFile.getAbsolutePath().lastIndexOf("."));
                    if (extension.equals(".JPG")) {
                        extension = ".jpeg";
                    }
                    type = map.getMimeTypeFromExtension(extension.replace(".", ""));
                    Uri uri = FileProvider.getUriForFile(InternalExplorerActivity.this, getApplicationContext().getPackageName(), selectedFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, type);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
            } else {
                Toast.makeText(this, "Please click folder to open", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onContextItemSelected(item);

    }*/

    private int getImagePosition(String category) {
        imagePos = imagesList.indexOf(category);
        return imagePos;
    }

    private void showUnlockDialog(ActionMode mode) {
        actionMode = mode;
        if (enterPwdDialog == null || !enterPwdDialog.isShowing()) {
            enterPwdDialog = new Dialog(this);
            enterPwdDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            enterPwdDialog.setContentView(R.layout.enter_password_dialog);
            mPinLockView = (PinLockView) enterPwdDialog.findViewById(R.id.pin_lock_view);
            mIndicatorDots = (IndicatorDots) enterPwdDialog.findViewById(R.id.indicator_dots);

            mPinLockView.attachIndicatorDots(mIndicatorDots);
            mPinLockView.setPinLockListener(mPinLockListener);
            //mPinLockView.setCustomKeySet(new int[]{2, 3, 1, 5, 9, 6, 7, 0, 8, 4});
            //mPinLockView.enableLayoutShuffling();

            mPinLockView.setPinLength(4);
            mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

            mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);
       /*     ImageView iv_img = (ImageView) enterPwdDialog.findViewById(R.id.iv_info);
            Button logoutBtnYes = (Button) mPolicyDialog.findViewById(R.id.btn_ok);
            CustomTextView tv_content1 = (CustomTextView) mPolicyDialog.findViewById(R.id
                    .tv_content1);
            tv_content1.setText(getString(R.string.msg_forward_not_allowed));
            logoutBtnYes.setOnClickListener(this);*/
            enterPwdDialog.show();
        }
    }

    private String TAG = "LOCK SCREEN";

    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);
            if (pwdSharedPrefs.getString(Constants.password_input, null) != null) {
                if (pin.equals(pwdSharedPrefs.getString(Constants.password_input, null))) {
                    File unlockedSelectedFile = new File(selectedFiles.get(0));
                    SharedPreferences unlockPrefs = PreferenceManager.getDefaultSharedPreferences(InternalExplorerActivity.this);
                    unlockPrefs.edit().putBoolean(unlockedSelectedFile.getAbsolutePath(), false).commit();
                    outFile_dec = new File(unlockedSelectedFile.getAbsolutePath().substring(0, unlockedSelectedFile.getAbsolutePath().lastIndexOf("/"))+"/"+unlockedSelectedFile.getName());

                    actionMode.finish();
                    enterPwdDialog.dismiss();
                    File fdelete = new File(unlockedSelectedFile.getAbsolutePath() + "/.nomedia");
                    if (fdelete.exists()) {
                        fdelete.delete();
                    }
                   /* try {
                        Encrypter.decrypt(key2, paramSpec,
                                new FileInputStream(unlockedSelectedFile.getAbsolutePath()), new FileOutputStream(outFile_dec));
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }*/
                } else {
                    Toast.makeText(InternalExplorerActivity.this, "Incorrect Pin", Toast.LENGTH_SHORT).show();
                }
            }

        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };

    //zip() will be called from this AsyncTask as this is long task.
    private class CompressFiles extends AsyncTask<ArrayList, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(ArrayList... arrayLists) {
            File file = Utils.getOutputZipFile(Calendar.getInstance().getTime() + ".zip", rootPath);


            if (file != null) {
                zipFileName = file.getAbsolutePath();

                if (arrayLists[0].size() > 0) {
                    zip(zipFileName, arrayLists[0]);
                }
            }

            return true;
        }


        @Override
        protected void onPreExecute() {
            loadingIndicator.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.VISIBLE);
            try {
                mProgressView.setText("0%");
            } catch (Exception ignored) {
            }
        }


        public void publish(ArrayList<String> list, int filesCompressionCompleted) {
            int totalNumberOfFiles = list.size();
            publishProgress((100 * filesCompressionCompleted) / totalNumberOfFiles);
        }

        protected void onProgressUpdate(Integer... progress) {

            try {
                mProgressView.setText(Integer.toString(progress[0]) + "% ");
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }

        protected void onPostExecute(Boolean flag) {
            loadingIndicator.setVisibility(View.GONE);
            mProgressView.setVisibility(View.GONE);
            Log.d("COMPRESS_TASK", "COMPLETED");
            mProgressView.setText("100 % ");
            mProgressView.setVisibility(View.GONE);
            if (flag) {
                File zipFile = new File(zipFileName);
                m_item.add(zipFile.getName());
                m_path.add(zipFile.getPath());
                m_listAdapter.notifyDataSetChanged();
            }
            Toast.makeText(getApplicationContext(), "Zipping Completed", Toast.LENGTH_SHORT).show();
        }
    }

    //Function will get the call from compress function
    public void setCompressProgress(ArrayList<String> files, int filesCompressionCompleted) {
        mCompressFiles.publish(files, filesCompressionCompleted);
    }

    //Zipping function
    public void zip(String zipFilePath, ArrayList<String> mFilePathList) {
        try {
            BufferedInputStream origin;
            FileOutputStream dest = new FileOutputStream(zipFilePath);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));

            byte data[] = new byte[BUFFER];

            for (int i = 0; i < mFilePathList.size(); i++) {

                setCompressProgress(mFilePathList, i + 1);

                FileInputStream fi = new FileInputStream(mFilePathList.get(i));
                origin = new BufferedInputStream(fi, BUFFER);
                ZipEntry entry = new ZipEntry(mFilePathList.get(i).substring(mFilePathList.get(i).lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;
                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

