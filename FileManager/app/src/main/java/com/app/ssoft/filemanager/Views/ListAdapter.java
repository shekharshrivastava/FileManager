package com.app.ssoft.filemanager.Views;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shekahar.Shrivastava on 18-Jan-18.
 */

public class ListAdapter extends BaseAdapter {
    private List<String> m_item;
    private List<String> m_path;
    public ArrayList<Integer> m_selectedItem;
    Context m_context;
    Boolean m_isRoot;
    private Bitmap thumbnailDrawable;

    public ListAdapter(Context p_context, List<String> p_item, List<String> p_path, Boolean p_isRoot) {
        m_context = p_context;
        m_item = p_item;
        m_path = p_path;
        m_selectedItem = new ArrayList<Integer>();
        m_isRoot = p_isRoot;
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
        View m_view = null;
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

        m_viewHolder.m_tvFileName.setText(m_item.get(p_position));
        String m_filepath = new File(m_path.get(p_position)).getAbsolutePath();

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
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            setFileImageType(new File(m_path.get(p_position))).compress(Bitmap.CompressFormat.PNG, 50, stream);
            Glide.with(m_context)
                    .load(new File(m_path.get(p_position)))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.picture_folder)
                    .error(R.drawable.doc_folder)
                    .into(m_viewHolder.m_ivIcon);
        } else {
            m_viewHolder.m_ivIcon.setImageResource(R.drawable.closed_folders);
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

}