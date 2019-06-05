package com.vnaskos.pdfguru.input;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Vasilis Naskos
 */
public class DirectoryScanner {

    private List<File> rootDir;
    private File[] selectedFiles;

    public DirectoryScanner() {}

    public List<File> getAllSupportedFiles(File[] selectedFiles) {
        this.selectedFiles = selectedFiles;
        this.rootDir = new ArrayList<>();

        return getFiles();
    }

    private List<File> getFiles() {
        ArrayList<File> files = new ArrayList<>();
        
        for(File file : selectedFiles)
            files.addAll(getChildren(file));
        
        files.addAll(rootDir);
        
        return files;
    }
    
    private List<File> getChildren(File parentFile) {
        List<File> files = new ArrayList<>();
        
        if(isDirectory(parentFile)) {
            files.addAll(scanDirectory(parentFile));
        } else if(SupportedFileTypes.isSupported(parentFile)) {
            rootDir.add(parentFile);
        }
        
        return files;
    }
    
    private boolean isDirectory(File file) {
        return file.isDirectory();
    }
    
    private List<File> scanDirectory(File parentFile) {
        ArrayList<File> directoryFiles = new ArrayList<>();
        
        File[] currentDirFiles = parentFile.listFiles((dir, name) -> {
            String extension = SupportedFileTypes.getFileExtension(name);
            return SupportedFileTypes.isSupported(extension);
        });
        if(currentDirFiles != null && currentDirFiles.length != 0) {
            directoryFiles.addAll(Arrays.asList(currentDirFiles));
        }
        
        File[] subDirectories = parentFile.listFiles((dir, name) -> {
            String filePath = dir.getPath() + File.separator + name;
            File checkFile = new File(filePath);
            return checkFile.isDirectory();
        });
        if(subDirectories != null) {
            for (File subDir : subDirectories) {
                directoryFiles.addAll(scanDirectory(subDir));
            }
        }
        
        return directoryFiles;
    }
    
}
