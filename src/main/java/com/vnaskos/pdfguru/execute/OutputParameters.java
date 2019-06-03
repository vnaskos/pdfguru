package com.vnaskos.pdfguru.execute;

/**
 *
 * @author Vasilis Naskos
 */
public class OutputParameters {

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
        this.compression = compression;
    }

    public void setMultiFileOutput() {
        this.singleFileOutput = false;
    }

    public boolean isSingleFileOutput() {
        return singleFileOutput;
    }

    public boolean isMultipleFileOutput() {
        return !singleFileOutput;
    }
}
