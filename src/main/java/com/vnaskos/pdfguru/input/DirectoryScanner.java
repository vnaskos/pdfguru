package com.vnaskos.pdfguru.input;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Vasilis Naskos
 */
public class DirectoryScanner {

    public void scanSubDirectoriesForSupportedFiles(File[] selectedFiles, FileArrayConsumer callback) {
        List<File> supportedFiles = new ArrayList<>();

        for(File file : selectedFiles) {
            if (file.isDirectory()) {
                scanDirectories(supportedFiles, file);
            } else if (SupportedFileTypes.isSupported(file)) {
                supportedFiles.add(file);
            }
        }

        callback.handleArray(supportedFiles.toArray(new File[0]));
    }
    
    private void scanDirectories(List<File> supportedFilesAccumulator, File parentFile) {
        File[] supportedFilesInDir = parentFile.listFiles(SupportedFileTypes::isSupported);
        if(supportedFilesInDir != null && supportedFilesInDir.length != 0) {
            supportedFilesAccumulator.addAll(Arrays.asList(supportedFilesInDir));
        }
        
        File[] subDirectories = parentFile.listFiles(File::isDirectory);
        if(subDirectories != null  && subDirectories.length != 0) {
            Arrays.stream(subDirectories).forEach(subDir -> scanDirectories(supportedFilesAccumulator, subDir));
        }
    }
    
}
