package com.vnaskos.pdfguru.execute;

/**
 *
 * @author Vasilis Naskos
 */
public class OutputParameters {
    
    private final float compression;
    private final String outputFile;
    private final boolean separateFiles;

    private OutputParameters(Builder b) {
        this.compression = b.compression;
        this.outputFile = b.outputFile;
        this.separateFiles = b.separateFiles;
    }
    
    public float getCompression() {
        return compression;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public boolean isSeparateFiles() {
        return separateFiles;
    }
    
    public static class Builder {
        private float compression;
        private String outputFile;
        private boolean separateFiles;
        
        public Builder compression(float comp) {
            this.compression = comp;
            return this;
        }
        
        public Builder outputFile(String out) {
            this.outputFile = out;
            return this;
        }
        
        public Builder separateFiles(boolean sep) {
            this.separateFiles = sep;
            return this;
        }
        
        public OutputParameters build() {
            return new OutputParameters(this);
        }
    }
}
