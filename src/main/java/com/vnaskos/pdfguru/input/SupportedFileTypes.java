package com.vnaskos.pdfguru.input;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author Vasilis Naskos
 */
public class SupportedFileTypes {
    
    public static boolean isSupported(String fileType) {
        HashSet<String> supportedInputExtensions = new HashSet<String>();

        supportedInputExtensions.addAll(Arrays.asList(
                "jpg", "jpeg", "png", "bmp",
                "tiff", "tif", "gif", "psd",
                "tga", "pdf"
        ));
        
        return supportedInputExtensions.contains(fileType);
    }
    
    public static boolean isSupported(File file) {
        String extension = getFileExtension(file);
        
        return isSupported(extension);
    }
    
    public static String getFileExtension(String file) {
        int indexOfExtension = file.lastIndexOf('.') + 1;

        String extension = file.substring(indexOfExtension);

        return extension;
    }
    
    public static String getFileExtension(File file) {
        String filePath = file.getAbsolutePath();
        
        return getFileExtension(filePath);
    }
}
