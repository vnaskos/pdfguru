package com.vnaskos.pdfguru.execution;

/**
 *
 * @author Vasilis Naskos
 */
public class OutputParameters {

    static final float COMPRESSED_MAX = 0.0f;
    static final float UNCOMPRESSED_MAX = 1.0f;

    private final String outputFile;

    private float compression = 0.5f;
    private boolean singleFileOutput = true;

    public OutputParameters(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public float getCompression() {
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

    public boolean isSingleFileOutput() {
        return singleFileOutput;
    }
}
