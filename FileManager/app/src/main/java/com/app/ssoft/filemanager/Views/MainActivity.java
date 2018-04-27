package com.app.ssoft.filemanager.Views;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Constants;
import com.app.ssoft.filemanager.Utils.Utils;
import com.crashlytics.android.Crashlytics;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.intentfilter.androidpermissions.PermissionManager;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import at.grabner.circleprogress.CircleProgressView;
import io.fabric.sdk.android.Fabric;

import static java.util.Collections.singleton;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final int REQUEST_CODE_OPENER = 2;
    private TextView internalStorageSpace;
    private CircleProgressView externalProgressBar;
    private RelativeLayout externalStorageLayout;
    private TextView externalStorageSpace;
    private float percentage;
    private CircleProgressView internalProgressBar;
    private GridView quickLinksGV;
    private LinearLayout internalStorageLinearLayout;
    private GoogleApiClient mGoogleApiClient;
    private DriveId mFileId;
    private TextView noMediaText;
    private UsbManager usbManager;
    private UsbDevice clef;
    ArrayList<File> images;
    private int[] bgColors;
    private int[] mStartColors;
    private int imageIndex;
    private float scale;
    private int countImgs;
    private NativeExpressAdView mAdView;
    private TextView versionTV;
    private PermissionManager permissionManager;
    private TextView internalTotalSpace;

    private TapTargetSequence sequence1;
    private SharedPreferences.Editor editor;
    private boolean isTutorialCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_main);
        SharedPreferences sharedPrefs = getSharedPreferences(Constants.SHARED_PREF_SET_APP_TOUR_, MODE_PRIVATE);
        isTutorialCompleted = sharedPrefs.getBoolean(Constants.is_home_app_tour_completed, false);
        mAdView = findViewById(R.id.nativeAdView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("7B739675F49D587BB5D2F85182CECA54").build();
        mAdView.loadAd(adRequest);
 /*       AdRequest adRequest = new AdRequest.Builder().build();
        */
        MobileAds.initialize(this, getString(R.string.ad_mob_app_id));
        permissionManager = PermissionManager.getInstance(this);
        permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
            @Override
            public void onPermissionGranted() {
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();
            }
        });
        String[] web = {
                "Documents",
                "App",
                "Images",
                "Audio",
                "Video",
                "Downloads",

        };
        int[] imageId = {
                R.drawable.documents,
                R.drawable.apk,
                R.drawable.images,
                R.drawable.audio,
                R.drawable.movies,
                R.drawable.downloads,


        };

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        internalStorageLinearLayout = findViewById(R.id.internalStorageLinearLayout);
        internalStorageLinearLayout.setOnClickListener(this);
        internalStorageSpace = findViewById(R.id.internalStorageSpace);
        internalTotalSpace = findViewById(R.id.internalTotalSpace);
        internalProgressBar = findViewById(R.id.internal_progress_bar);
        externalProgressBar = findViewById(R.id.external_progress_bar);
        externalStorageLayout = findViewById(R.id.externalStorageLayout);
        externalStorageSpace = findViewById(R.id.externalStorageSpace);

        showTutorial(isTutorialCompleted);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout =
                navigationView.inflateHeaderView(R.layout.nav_header_main);
        versionTV = headerLayout.findViewById(R.id.version);
        versionTV.setText("v" + Constants.getVersionName(this));

        if (externalMemoryAvailable()) {
            externalStorageLayout.setVisibility(View.VISIBLE);
            externalStorageSpace.setText(getAvailableExternalMemorySize() + " / " + getTotalExternalMemorySize());
            externalProgressBar.setValueAnimated(calculatePercentage(getTotalExternalMemorySize(), getAvailableExternalMemorySize()));

        } else {
            externalStorageLayout.setVisibility(View.GONE);
        }

        internalStorageSpace.setText(getAvailableInternalMemorySize());
        internalTotalSpace.setText(" / " + getTotalInternalMemorySize());
        internalProgressBar.setValueAnimated(calculatePercentage(getTotalInternalMemorySize(), getUsedInternalSpace()));
        internalProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                        permissionManager.checkPermissions(singleton(Manifest.permission.READ_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(MainActivity.this, StorageInfoActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onPermissionDenied() {
                                Toast.makeText(MainActivity.this, "Required storage permission to access file manager", Toast.LENGTH_SHORT).show();

                            }


                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(MainActivity.this, "Required storage permission to access file manager", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });

        quickLinksGV = findViewById(R.id.quickLinksGV);
        CustomQLGridAdapter adapter = new CustomQLGridAdapter(MainActivity.this, web, imageId);
        quickLinksGV.setAdapter(adapter);
        quickLinksGV.setOnItemClickListener(this);

        accessUSBDevice();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                @Override
                public void onPermissionGranted() {
                    Intent intent = new Intent(MainActivity.this, DownloadFilterActivity.class);
                    startActivity(intent);

                }

                @Override
                public void onPermissionDenied() {
                    Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();
                }
            });

            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                @Override
                public void onPermissionGranted() {
                    Intent imageFilterIntent = new Intent(MainActivity.this, ImageFilterActivity.class);
                    imageFilterIntent.putExtra("chooserIntent", 2);
                    startActivity(imageFilterIntent);

                }

                @Override
                public void onPermissionDenied() {
                    Toast.makeText(MainActivity.this, "Required permission to access gallery", Toast.LENGTH_SHORT).show();
                }
            });


        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(this, StorageInfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_setting) {

            /*permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                @Override
                public void onPermissionGranted() {
                    permissionManager.checkPermissions(singleton(Manifest.permission.READ_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                        @Override
                        public void onPermissionGranted() {
                            Utils.shareApplication(MainActivity.this);
                        }

                        @Override
                        public void onPermissionDenied() {
                            Toast.makeText(MainActivity.this, "Required storage permission for sharing apk", Toast.LENGTH_SHORT).show();

                        }


                    });
                }

                @Override
                public void onPermissionDenied() {
                    Toast.makeText(MainActivity.this, "Required storage permission for sharing apk", Toast.LENGTH_SHORT).show();
                }
            });*/
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_drive) {
            mGoogleApiClient.connect();

            if (mGoogleApiClient.isConnected()) {
                OpenFileFromGoogleDrive();
            }

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return Utils.bytesToHuman(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return Utils.bytesToHuman(totalBlocks * blockSize);
    }

    //    public static String getUsedInternalStorage (){}
    public static boolean externalMemoryAvailable() {
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        Boolean isSDSupportedDevice = Environment.isExternalStorageRemovable();

        return isSDSupportedDevice && isSDPresent;
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return Utils.bytesToHuman(availableBlocks * blockSize);
        } else {
            return "";
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return Utils.bytesToHuman(totalBlocks * blockSize);
        } else {
            return "";
        }
    }

    public float calculatePercentage(String totalSpace, String avblSpace) {
        if (totalSpace != null && avblSpace != null) {
            String[] concatTotalSpace = totalSpace.split(" ");
            String[] concatAvblSpace = avblSpace.split(" ");

            double spaceAvbl = Double.parseDouble(concatAvblSpace[0]);
            double totalSpcAvbl = Double.parseDouble(concatTotalSpace[0]);
            double usedSpace = totalSpcAvbl - spaceAvbl;
            double res = (usedSpace / totalSpcAvbl) * 100f;
            percentage = (float) (res);
        } else {
            percentage = 0f;
        }
        return percentage;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                        permissionManager.checkPermissions(singleton(Manifest.permission.READ_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent docFilterIntent = new Intent(MainActivity.this, DocumentFIlterActivity.class);
                                startActivity(docFilterIntent);
                            }

                            @Override
                            public void onPermissionDenied() {
                                Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();

                            }


                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case 1:
                permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {

                        permissionManager.checkPermissions(singleton(Manifest.permission.READ_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent appFilterIntent = new Intent(MainActivity.this, AppsFilterActivity.class);
                                startActivity(appFilterIntent);
                            }

                            @Override
                            public void onPermissionDenied() {
                                Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();

                            }


                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();
                    }
                });


                break;

            case 2:
                permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                        permissionManager.checkPermissions(singleton(Manifest.permission.READ_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent imageFilterIntent = new Intent(MainActivity.this, ImageFilterActivity.class);
                                imageFilterIntent.putExtra("chooserIntent", 2);
                                startActivity(imageFilterIntent);

                            }

                            @Override
                            public void onPermissionDenied() {
                                Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();

                            }


                        });
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case 3:
                permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                        permissionManager.checkPermissions(singleton(Manifest.permission.READ_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent musicFilterIntent = new Intent(MainActivity.this, MediaFilterActivity.class);
                                musicFilterIntent.putExtra("chooserIntent", 3);
                                startActivity(musicFilterIntent);
                            }

                            @Override
                            public void onPermissionDenied() {
                                Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();

                            }


                        });

                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();
                    }
                });

                break;

            case 4:
                permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                        permissionManager.checkPermissions(singleton(Manifest.permission.READ_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent videoFilterIntent = new Intent(MainActivity.this, MediaFilterActivity.class);
                                videoFilterIntent.putExtra("chooserIntent", 4);
                                startActivity(videoFilterIntent);
                            }

                            @Override
                            public void onPermissionDenied() {
                                Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();

                            }


                        });

                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();
                    }
                });

                break;
            case 5:
                permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                        permissionManager.checkPermissions(singleton(Manifest.permission.READ_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(MainActivity.this, DownloadFilterActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onPermissionDenied() {
                                Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();

                            }


                        });

                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(MainActivity.this, "Required permission to access file manager", Toast.LENGTH_SHORT).show();
                    }
                });

                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.internalStorageLinearLayout:
                permissionManager.checkPermissions(singleton(Manifest.permission.WRITE_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                    @Override
                    public void onPermissionGranted() {
                        permissionManager.checkPermissions(singleton(Manifest.permission.READ_EXTERNAL_STORAGE), new PermissionManager.PermissionRequestListener() {
                            @Override
                            public void onPermissionGranted() {
                                Intent intent = new Intent(MainActivity.this, InternalExplorerActivity.class);
                                startActivity(intent);
                            }


                            @Override
                            public void onPermissionDenied() {
                                Toast.makeText(MainActivity.this, "Please grant Storage permission", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }


                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(MainActivity.this, "Please grant Storage permission", Toast.LENGTH_SHORT).show();
                    }
                });


        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        InternalExplorerActivity.isCutOrCopied = false;
    }

    public static String getUsedInternalSpace() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long totalBlocks = stat.getBlockCountLong();
        long totalAvailableBlock = availableBlocks * blockSize;
        long totalInternalStorageBlock = totalBlocks * blockSize;
        return Utils.bytesToHuman(totalInternalStorageBlock - totalAvailableBlock);


    }

    @Override
    protected void onResume() {

        if (mGoogleApiClient == null) {

            /**
             * Create the API client and bind it to an instance variable.
             * We use this instance as the callback for connection and connection failures.
             * Since no account name is passed, the user is prompted to choose.
             */
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }


        super.onResume();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {

            // disconnect Google Android Drive API connection.
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        OpenFileFromGoogleDrive();


    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.i("MainActivity", "GoogleApiClient connection suspended");


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("MainActivity", "GoogleApiClient connection failed:" + connectionResult.toString());
        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0).show();
            return;


        }
        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */

        try {

            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);

        } catch (IntentSender.SendIntentException e) {

            Log.i("MainActivity", "GoogleApiClient connection failed:" + connectionResult.toString());
        }
    }

    public void OpenFileFromGoogleDrive() {

        IntentSender intentSender = Drive.DriveApi
                .newOpenFileActivityBuilder()
                .build(mGoogleApiClient);
        try {
            startIntentSenderForResult(

                    intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);

        } catch (IntentSender.SendIntentException e) {

            Log.w("MainActivtity", "Unable to send intent", e);
        }


    }

    @Override
    protected void onActivityResult(final int requestCode,
                                    final int resultCode, final Intent data) {
        switch (requestCode) {

            case REQUEST_CODE_OPENER:

                if (resultCode == RESULT_OK) {

                    mFileId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    Log.e("file id", mFileId.getResourceId() + "");

                    String url = "https://drive.google.com/open?id=" + mFileId.getResourceId();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }

                break;
            case REQUEST_CODE_RESOLUTION:
                if (mGoogleApiClient != null) {
                    if (mGoogleApiClient.isConnected()) {
                        IntentSender intentSender = Drive.DriveApi
                                .newOpenFileActivityBuilder()
                                .build(mGoogleApiClient);
                        try {
                            startIntentSenderForResult(

                                    intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);

                        } catch (IntentSender.SendIntentException e) {

                            Log.w("MainActivtity", "Unable to send intent", e);
                        }
                    }/*else{
                        mGoogleApiClient.connect();
                    }*/
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private boolean fileOperation = false;
    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {

                    if (result.getStatus().isSuccess()) {
                        if (fileOperation == true) {
                            //CreateFileOnGoogleDrive(result);
                        } else {
                            OpenFileFromGoogleDrive();
                        }
                    }

                }
            };

    public void onClickOpenFile() {
        fileOperation = false;

        // create new contents resource
        Drive.DriveApi.newDriveContents(mGoogleApiClient)
                .setResultCallback(driveContentsCallback);
    }

    public void accessUSBDevice() {
        Intent intent = getIntent();
        UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);


        if (usbManager != null) {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            UsbDevice deviceName = deviceList.get("deviceName");
            if (deviceList != null) {
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                while (deviceIterator.hasNext()) {
                    clef = deviceIterator.next();
                }
            }
        }

        if (clef != null) {
            File directory = new File("/storage/UsbDriveA/");
            if (directory != null) {
                if (directory.canRead()) {

                    images = new ArrayList<File>();
                    String[] imageExtensions = {"jpg", "jpeg", "png", "gif", "JPG", "JPEG", "PNG", "GIF"};
                    Iterator<File> iterateImages = FileUtils.iterateFiles(directory, imageExtensions, true);
                    while (iterateImages.hasNext()) {
                        File theImage = iterateImages.next();
                        if (!theImage.getName().startsWith(".", 0))
                            images.add(theImage);
                    }

                    // custom process / methods... not very relevant here :
                    imageIndex = 0;
                    scale = 1.0f;
                    countImgs = images.size();
//                    loadImage(imageIndex);
                }
            }
        }
    }

    public void showTutorial(boolean isTutorialCompleted) {
        if (!isTutorialCompleted) {
            final SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREF_SET_APP_TOUR_, MODE_PRIVATE).edit();

            sequence1 = new TapTargetSequence(this)
                    .targets(
                            TapTarget.forView(findViewById(R.id.internal_progress_bar),
                                    getString(R.string.str_walkthrough_storage_info),
                                    getString(R.string.str_walkthrough_storage_info_substring))
                                    .outerCircleColor(R.color.colorPrimary)
                                    .targetCircleColor(R.color.white)
                                    .cancelable(true)
                                    .titleTextColor(R.color.white)
                                    .textColor(R.color.white)
                                    .drawShadow(true)
                                    .id(1),
                            TapTarget.forView(findViewById(R.id.internalStorageLinearLayout),
                                    getString(R.string.str_walkthrough_internal_storage),
                                    getString(R.string.str_walkthrough_internal_storage_substring))
                                    .outerCircleColor(R.color.colorPrimary)
                                    .targetCircleColor(R.color.white)
                                    .cancelable(true)
                                    .titleTextColor(R.color.white)
                                    .textColor(R.color.white)
                                    .drawShadow(true)
                                    .id(1)).listener(new TapTargetSequence.Listener() {
                                                         @Override
                                                         public void onSequenceFinish() {
                                                             editor.putBoolean(Constants.is_home_app_tour_completed, true);
                                                             editor.commit();
                                                         }

                                                         @Override
                                                         public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                                                         }

                                                         @Override
                                                         public void onSequenceCanceled(TapTarget lastTarget) {
                                                             new AlertDialog.Builder(MainActivity.this)
                                                                     .setTitle(getString(R.string.str_walkthrough_cancelled))
                                                                     .setMessage(getString(R.string.str_walkthrough_cancelled_substring))
                                                                     .setPositiveButton(getString(R.string.str_walkthrough_ok), new DialogInterface.OnClickListener() {
                                                                         @Override
                                                                         public void onClick(DialogInterface dialog, int which) {
                                                                             editor.putBoolean(Constants.is_home_app_tour_completed, true);
                                                                             editor.commit();
                                                                         }
                                                                     })
                                                                     .show();
                                                             ;
                                                         }
                                                     }
                    );


            sequence1.start();
        }
    }
}
