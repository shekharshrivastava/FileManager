package com.app.ssoft.filemanager.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shekahar.Shrivastava on 19-Jan-18.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>  {
    private List<String> m_item;
    private List<String> m_path;
    public ArrayList<Integer> m_selectedItem;
    Context m_context;
    Boolean m_isRoot;
    private Bitmap thumbnailDrawable;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView m_tvFileName, m_tvDate;
        public ImageView m_ivIcon;
        public CheckBox m_cbCheck;


        public MyViewHolder(View m_view) {
            super(m_view);
            m_tvFileName = (TextView) m_view.findViewById(R.id.lr_tvFileName);
            m_tvDate = (TextView) m_view.findViewById(R.id.lr_tvdate);
            m_ivIcon = (ImageView) m_view.findViewById(R.id.lr_ivFileIcon);
            m_cbCheck = (CheckBox) m_view.findViewById(R.id.lr_cbCheck);
        }
    }
    public RecyclerViewAdapter (Context p_context, List<String> p_item, List<String> p_path, Boolean p_isRoot) {
        m_context = p_context;
        m_item = p_item;
        m_path = p_path;
        m_selectedItem = new ArrayList<Integer>();
        m_isRoot = p_isRoot;
    }
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.MyViewHolder m_viewHolder, final int p_position) {
        m_viewHolder.m_tvFileName.setText(m_item.get(p_position));

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
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Utils.setFileImageType(m_context,new File(m_path.get(p_position))).compress(Bitmap.CompressFormat.PNG, 50, stream);
            Glide.with(m_context)
                    .load(stream.toByteArray())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
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
        m_viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File m_isFile = new File(m_path.get(p_position));
                if (m_isFile.isDirectory()) {
//                    getDirFromRoot(m_isFile.toString());
                } else {

                    Toast.makeText(m_context, "" +m_item.get(p_position), Toast.LENGTH_SHORT).show();
                    MimeTypeMap map = MimeTypeMap.getSingleton();
//        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
                    String extension = m_isFile.getAbsolutePath().substring(m_isFile.getAbsolutePath().lastIndexOf("."));
                    String type = map.getMimeTypeFromExtension(extension.replace(".",""));

                    if (type == null)
                        type = "*//*";
                    Uri uri = FileProvider.getUriForFile(m_context, m_context.getApplicationContext().getPackageName(), m_isFile);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, type);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    m_context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return m_item.size();
    }
    String getLastDate(int p_pos) {
        File m_file = new File(m_path.get(p_pos));
        SimpleDateFormat m_dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return m_dateFormat.format(m_file.lastModified());
    }
}
