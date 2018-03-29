package com.app.ssoft.filemanager.Model;

import java.io.File;

/**
 * Created by Shekahar.Shrivastava on 29-Mar-18.
 */

public class PastedDetails {
    public File destinationFile;
    public boolean isPasted;

    public PastedDetails() {
    }

    public PastedDetails(File destinationFile, boolean isPasted) {
        this.destinationFile = destinationFile;
        this.isPasted = isPasted;
    }

    public File getDestinationFile() {
        return destinationFile;
    }

    public void setDestinationFile(File destinationFile) {
        this.destinationFile = destinationFile;
    }

    public boolean isPasted() {
        return isPasted;
    }

    public void setPasted(boolean pasted) {
        isPasted = pasted;
    }
}

