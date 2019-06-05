package com.vnaskos.pdfguru.input;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author Vasilis Naskos
 */
public class SupportedFileTypes {

    private static final HashSet<String> supportedInputExtensions = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "bmp",
            "tiff", "tif", "gif", "psd",
            "tga", "pdf"));

    public static boolean isSupported(String fileType) {
        return supportedInputExtensions.contains(fileType.toLowerCase());
    }
    
    public static boolean isSupported(File file) {
        String extension = getFileExtension(file.getAbsolutePath());
        
        return isSupported(extension);
    }
    
    public static String getFileExtension(String filepath) {
        int indexOfExtension = filepath.lastIndexOf('.') + 1;

        return filepath.substring(indexOfExtension);
    }
}
