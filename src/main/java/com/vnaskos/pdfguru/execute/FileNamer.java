package com.vnaskos.pdfguru.execute;

import java.io.File;

class FileNamer {

    private static final int INITIAL_INDEX = 1;

    private int index = INITIAL_INDEX;

    String createUniqueOutputFileName(String outputPath) {
        String outputFilePath = outputPath;

        if(outputFilePath.toLowerCase().endsWith(".pdf")) {
            outputFilePath = outputFilePath.substring(0, outputPath.length()-4);
        }

        String uniqueOutputFilePath;
        do {
            uniqueOutputFilePath = String.format("%s_%d.pdf", outputFilePath, index++);
        } while (fileExists(uniqueOutputFilePath));

        return uniqueOutputFilePath;
    }

    boolean fileExists(String testOut) {
        return (new File(testOut)).exists();
    }
}
