package com.vnaskos.pdfguru.input;

import java.io.File;

public interface SelectedFilesCallback {

    void handleSelectedFiles(File[] selectedFiles);
}
