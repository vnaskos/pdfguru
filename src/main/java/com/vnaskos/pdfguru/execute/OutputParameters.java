package com.vnaskos.pdfguru.execute;

/**
 *
 * @author Vasilis Naskos
 */
public class OutputParameters {
    
    private final float compression;
    private final String outputFile;
    private final boolean singleFileOutput;

    private OutputParameters(Builder b) {
        this.compression = b.compression;
        this.outputFile = b.outputFile;
        this.singleFileOutput = b.singleFileOutput;
    }
    
    public float getCompression() {
        return compression;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public boolean isSingleFileOutput() {
        return singleFileOutput;
    }

    public boolean isMultipleFileOutput() {
        return !singleFileOutput;
    }

    public static class Builder {
        private String outputFile;
        private float compression = 0.5f;
        private boolean singleFileOutput = true;

        public Builder(String outputFile) {
            this.outputFile = outputFile;
        }
        
        public Builder compression(float comp) {
            this.compression = comp;
            return this;
        }
        
        public Builder singleFileOutput(boolean singleFileOutput) {
            this.singleFileOutput = singleFileOutput;
            return this;
        }
        
        public OutputParameters build() {
            return new OutputParameters(this);
        }
    }
}
