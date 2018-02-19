package com.app.ssoft.filemanager.Views;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.app.ssoft.filemanager.R;
import com.app.ssoft.filemanager.Utils.Utils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.util.ArrayList;

public class StorageInfoActivity extends AppCompatActivity {

    private PieChart pieChart;
    ArrayList<Entry> entries;
    private ArrayList<String> PieEntryLabels;
    private PieDataSet pieDataSet;
    private PieData pieData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_info);
        pieChart = (PieChart) findViewById(R.id.chart1);
        getDocumentsFileSize();
        entries = new ArrayList<>();

        PieEntryLabels = new ArrayList<String>();

        AddValuesToPIEENTRY();

        AddValuesToPieEntryLabels();

        pieDataSet = new PieDataSet(entries, "");

        pieData = new PieData(PieEntryLabels, pieDataSet);

        pieDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        pieChart.setData(pieData);

        pieChart.animateY(3000);

    }

    public void AddValuesToPIEENTRY() {

        entries.add(new BarEntry(2f, 0));
        entries.add(new BarEntry(4f, 1));
        entries.add(new BarEntry(6f, 2));
        entries.add(new BarEntry(8f, 3));
        entries.add(new BarEntry(7f, 4));


    }

    public void AddValuesToPieEntryLabels() {

        PieEntryLabels.add("Images");
        PieEntryLabels.add("Audio");
        PieEntryLabels.add("Video");
        PieEntryLabels.add("Docs");
        PieEntryLabels.add("App");


    }

    public static long getDocumentsFileSize() {

        String p_rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File m_file = new File(p_rootPath);
        File[] m_filesArray = m_file.listFiles();
        long docFileSize = 0;
        for (int i = 0; i < m_filesArray.length; i++) {
            File file = m_filesArray[i];

            if (!file.isDirectory()) {
                if (file.getName().endsWith(".pdf") || file.getName().endsWith(".doc") ||
                        file.getName().endsWith(".txt") || file.getName().endsWith(".xls")) {
                    docFileSize = docFileSize + file.length();
                }

            } else {
                File[] dirFiles = file.listFiles();
                for (File docFiles : dirFiles) {
                    if (docFiles.getName().endsWith(".pdf") || docFiles.getName().endsWith(".doc") ||
                            docFiles.getName().endsWith(".txt") || docFiles.getName().endsWith(".xls")) {
                        docFileSize = docFileSize + docFiles.length();
                    }
                }
            }
        }
        Utils.bytesToHuman(docFileSize);
        return docFileSize;
    }


}