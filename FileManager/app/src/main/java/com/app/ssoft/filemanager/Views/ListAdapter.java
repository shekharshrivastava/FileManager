package com.app.ssoft.filemanager.Views;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Constants;
import com.app.ssoft.filemanager.Utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Shekahar.Shrivastava on 18-Jan-18.
 */

public class ListAdapter extends BaseAdapter {
    private final SharedPreferences sharedPrefs;
    private List<String> m_item;
    private List<String> m_path;
    public ArrayList<Integer> m_selectedItem;
    Context m_context;
    Boolean m_isRoot;
    private Bitmap thumbnailDrawable;
    private SharedPreferences prefs;
    private TapTargetSequence sequence1;
    private View m_view;
    private boolean isTutorialCompleted = false;

    public ListAdapter(Context p_context, List<String> p_item, List<String> p_path, Boolean p_isRoot) {
        m_context = p_context;
        m_item = p_item;
        m_path = p_path;
        m_selectedItem = new ArrayList<Integer>();
        m_isRoot = p_isRoot;
        sharedPrefs = m_context.getSharedPreferences(Constants.SHARED_PREF_SET_APP_TOUR_, MODE_PRIVATE);
    }

    @Override
    public int getCount() {
        return m_item.size();
    }

    @Override
    public Object getItem(int position) {
        return m_item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int p_position, View p_convertView, ViewGroup p_parent) {
        m_view = null;
        ViewHolder m_viewHolder = null;
        if (p_convertView == null) {
            LayoutInflater m_inflater = LayoutInflater.from(m_context);
            m_view = m_inflater.inflate(R.layout.row_layout, null);
            m_viewHolder = new ViewHolder();
            m_viewHolder.m_tvFileName = (TextView) m_view.findViewById(R.id.lr_tvFileName);
            m_viewHolder.m_tvDate = (TextView) m_view.findViewById(R.id.lr_tvdate);
            m_viewHolder.m_ivIcon = (ImageView) m_view.findViewById(R.id.lr_ivFileIcon);
            m_viewHolder.m_cbCheck = (CheckBox) m_view.findViewById(R.id.lr_cbCheck);
            m_view.setTag(m_viewHolder);
        } else {
            m_view = p_convertView;
            m_viewHolder = ((ViewHolder) m_view.getTag());
        }
        if (!m_isRoot && p_position == 0) {
            m_viewHolder.m_cbCheck.setVisibility(View.GONE);
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(m_context);
        m_viewHolder.m_tvFileName.setText(m_item.get(p_position));
        isTutorialCompleted = sharedPrefs.getBoolean(Constants.is_internal_app_tour_completed, false);
//        showTutorial(isTutorialCompleted);
//        String m_filepath = new File(m_path.get(p_position)).getAbsolutePath();

     /*   int m_lastIndex = new File(m_path.get(p_position)).getAbsolutePath().lastIndexOf(".");
        String m_filepath = new File(m_path.get(p_position)).getAbsolutePath();
        Bitmap imageThumbnail = setFileImageType(new File(m_path.get(p_position)));
        if (imageThumbnail != null && !(new File(m_path.get(p_position)).isDirectory())) {
            m_viewHolder.m_ivIcon.setImageBitmap(imageThumbnail);
        } else if ((new File(m_path.get(p_position)).isDirectory())) {
            m_viewHolder.m_ivIcon.setImageResource(R.drawable.closed_folders);
        } else if (m_filepath.substring(m_lastIndex).equalsIgnoreCase(".png") ||
                m_filepath.substring(m_lastIndex).equalsIgnoreCase(".jpg")) {
            m_viewHolder.m_ivIcon.setImageResource(R.drawable.picture_folder);
        } else {
            m_viewHolder.m_ivIcon.setImageResource(R.drawable.doc_folder);
        }*/


        if (!(new File(m_path.get(p_position)).isDirectory())) {
            if ((prefs.getBoolean(m_path.get(p_position), false) == true)) {
                m_viewHolder.m_ivIcon.setImageResource(R.drawable.locked_files);
            } else if (m_path.get(p_position).endsWith(".pdf")) {
                m_viewHolder.m_ivIcon.setImageResource(R.drawable.pdf_icon);
            } else if (m_path.get(p_position).endsWith(".txt")) {
                m_viewHolder.m_ivIcon.setImageResource(R.drawable.txt_icon);
            } else if (m_path.get(p_position).endsWith(".doc")) {
                m_viewHolder.m_ivIcon.setImageResource(R.drawable.doc_icon);
            } else if (m_path.get(p_position).endsWith(".apk")) {
                PackageManager pm = m_context.getPackageManager();
                PackageInfo pi = pm.getPackageArchiveInfo(m_path.get(p_position), PackageManager.GET_META_DATA);

                // the secret are these two lines....
                if (pi != null) {
                    pi.applicationInfo.sourceDir = m_path.get(p_position);
                    pi.applicationInfo.publicSourceDir = m_path.get(p_position);
                    //
                    Drawable APKicon = pi.applicationInfo.loadIcon(pm);
                    String AppName = (String) pi.applicationInfo.loadLabel(pm);

                    m_viewHolder.m_ivIcon.setImageDrawable(APKicon);
                }

            }else if(m_path.get(p_position).endsWith(".zip")){
                m_viewHolder.m_ivIcon.setImageResource(R.drawable.zip_folder);
            }
            else {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            setFileImageType(new File(m_path.get(p_position))).compress(Bitmap.CompressFormat.PNG, 50, stream);
                Glide.with(m_context)
                        .load(new File(m_path.get(p_position)))
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.empty_doc)
                        .into(m_viewHolder.m_ivIcon);
            }
        } else {
            if ((prefs.getBoolean(m_path.get(p_position), false) == true)) {
                m_viewHolder.m_ivIcon.setImageResource(R.drawable.locked_files);
            } else {
                m_viewHolder.m_ivIcon.setImageResource(R.drawable.closed_folders);
            }
        }

        m_viewHolder.m_tvDate.setText(getLastDate(p_position));
        m_viewHolder.m_cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    m_selectedItem.add(p_position);
                } else {
                    m_selectedItem.remove(m_selectedItem.indexOf(p_position));
                }
            }
        });
        return m_view;
    }

    class ViewHolder {
        CheckBox m_cbCheck;
        ImageView m_ivIcon;
        TextView m_tvFileName;
        TextView m_tvDate;
    }

    private Bitmap setFileImageType(File m_file) {
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

    String getLastDate(int p_pos) {
        File m_file = new File(m_path.get(p_pos));
        SimpleDateFormat m_dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:a");
        return m_dateFormat.format(m_file.lastModified());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void showTutorial(boolean isTutorialCompleted) {
        if (!isTutorialCompleted) {
            final SharedPreferences.Editor editor = m_context.getSharedPreferences(Constants.SHARED_PREF_SET_APP_TOUR_, MODE_PRIVATE).edit();

            sequence1 = new TapTargetSequence((Activity) m_context)
                    .targets(
                            TapTarget.forView(m_view.findViewById(R.id.lr_ivFileIcon),
                                    m_context.getString(R.string.str_walkthrough_storage_info),
                                    m_context.getString(R.string.str_walkthrough_long_click_substring))
                                    .outerCircleColor(R.color.colorPrimary)
                                    .targetCircleColor(R.color.white)
                                    .cancelable(true)
                                    .titleTextColor(R.color.white)
                                    .textColor(R.color.white)
                                    .drawShadow(true)
                                    .id(1)).listener(new TapTargetSequence.Listener() {
                                                         @Override
                                                         public void onSequenceFinish() {
                                                             editor.putBoolean(Constants.is_internal_app_tour_completed, true);
                                                             editor.commit();
                                                         }

                                                         @Override
                                                         public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                                                         }

                                                         @Override
                                                         public void onSequenceCanceled(TapTarget lastTarget) {
                                                             new AlertDialog.Builder(m_context)
                                                                     .setTitle(m_context.getString(R.string.str_walkthrough_cancelled))
                                                                     .setMessage(m_context.getString(R.string.str_walkthrough_cancelled_substring))
                                                                     .setPositiveButton(m_context.getString(R.string.str_walkthrough_ok), new DialogInterface.OnClickListener() {
                                                                         @Override
                                                                         public void onClick(DialogInterface dialog, int which) {
                                                                             editor.putBoolean(Constants.is_internal_app_tour_completed, true);
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