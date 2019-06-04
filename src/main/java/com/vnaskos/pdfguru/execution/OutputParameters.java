package com.vnaskos.pdfguru.execution;

import java.io.File;

/**
 *
 * @author Vasilis Naskos
 */
public class OutputParameters {

    private static final int INITIAL_INDEX = 1;
    static final float COMPRESSED_MAX = 0.0f;
    static final float UNCOMPRESSED_MAX = 1.0f;

    private final String outputFilePath;
    private int index = INITIAL_INDEX;

    private float compression = 0.5f;
    private boolean singleFileOutput = true;

    public OutputParameters(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    String getUniqueOutputFileName() {
        String tmpOutputFilePath = outputFilePath;

        if(outputFilePath.toLowerCase().endsWith(".pdf")) {
            tmpOutputFilePath = outputFilePath.substring(0, outputFilePath.length()-4);
        }

        String uniqueOutputFilePath;
        do {
            uniqueOutputFilePath = String.format("%s_%d.pdf", tmpOutputFilePath, index++);
        } while (fileExists(uniqueOutputFilePath));

        return uniqueOutputFilePath;
    }

    boolean fileExists(String testOut) {
        return (new File(testOut)).exists();
    }

    float getCompression() {
        return compression;
    }

    public void setCompression(float compression) {
        if (compression < 0.0f) {
            this.compression = COMPRESSED_MAX;
        } else if (compression > 1.0f) {
            this.compression = UNCOMPRESSED_MAX;
        } else {
            this.compression = compression;
        }
    }

    public void setMultiFileOutput() {
        this.singleFileOutput = false;
    }

    boolean isSingleFileOutput() {
        return singleFileOutput;
    }
}
