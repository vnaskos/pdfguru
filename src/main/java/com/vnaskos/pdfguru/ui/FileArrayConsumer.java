package com.vnaskos.pdfguru.ui;

import java.io.File;

public interface FileArrayConsumer {

    void handleFiles(File... selectedFiles);
}
