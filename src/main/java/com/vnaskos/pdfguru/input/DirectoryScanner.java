package com.vnaskos.pdfguru.input;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Vasilis Naskos
 */
public class DirectoryScanner {

    private static final HashSet<String> supportedInputExtensions = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "bmp",
            "tiff", "tif", "gif", "psd",
            "tga", "pdf"));

    public void scanSubDirectoriesForSupportedFiles(File[] selectedFiles, FileArrayConsumer callback) {
        List<File> supportedFiles = new ArrayList<>();

        for(File file : selectedFiles) {
            if (file.isDirectory()) {
                scanDirectories(supportedFiles, file);
            } else if (isSupported(file)) {
                supportedFiles.add(file);
            }
        }

        callback.handleArray(supportedFiles.toArray(new File[0]));
    }
    
    private void scanDirectories(List<File> supportedFilesAccumulator, File parentFile) {
        File[] supportedFilesInDir = parentFile.listFiles(this::isSupported);
        if(supportedFilesInDir != null && supportedFilesInDir.length != 0) {
            supportedFilesAccumulator.addAll(Arrays.asList(supportedFilesInDir));
        }
        
        File[] subDirectories = parentFile.listFiles(File::isDirectory);
        if(subDirectories != null  && subDirectories.length != 0) {
            Arrays.stream(subDirectories).forEach(subDir -> scanDirectories(supportedFilesAccumulator, subDir));
        }
    }

    boolean isSupported(File file) {
        String filepath = file.getAbsolutePath();

        int indexOfExtension = filepath.lastIndexOf('.') + 1;
        String extension = filepath.substring(indexOfExtension).toLowerCase();

        return supportedInputExtensions.contains(extension);
    }
    
}
