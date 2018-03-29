package com.app.ssoft.filemanager.Model;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Shekahar.Shrivastava on 28-Mar-18.
 */

public class PasteFile {
    public String fileName;
    public InputStream inputStream;
    public File srcPath;

    public PasteFile() {
    }

    public PasteFile(String fileName, InputStream inputStream) {
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public File getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(File srcPath) {
        this.srcPath = srcPath;
    }
}
