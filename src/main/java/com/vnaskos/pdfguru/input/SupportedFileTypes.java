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

    public static boolean isSupported(File file) {
        String filepath = file.getAbsolutePath();

        int indexOfExtension = filepath.lastIndexOf('.') + 1;
        String extension = filepath.substring(indexOfExtension).toLowerCase();

        return supportedInputExtensions.contains(extension);
    }

}
