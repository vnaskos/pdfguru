package com.vnaskos.pdfguru.input;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 *
 * @author Vasilis Naskos
 */
public class DirectoryScanner {

    private final ArrayList<File> rootDir;
    private final File[] selectedFiles;
    
    public DirectoryScanner(File[] selecedFiles) {
        this.selectedFiles = selecedFiles;
        this.rootDir = new ArrayList<File>();
    }
    
    public ArrayList<File[]> getFiles() {
        ArrayList<File[]> files = new ArrayList<File[]>();
        
        for(File file : selectedFiles)
            files.addAll(getChildren(file));
        
        files.add(rootDir.toArray(new File[]{}));
        
        return files;
    }
    
    private ArrayList<File[]> getChildren(File parentFile) {
        ArrayList<File[]> files = new ArrayList<File[]>();
        
        if(isDirectory(parentFile))
            files.addAll(scanDirectory(parentFile));
        else if(isSupportedFile(parentFile))
            rootDir.add(parentFile);        
        
        return files;
    }
    
    private boolean isDirectory(File file) {
        return file.isDirectory();
    }
    
    private boolean isSupportedFile(File file) {
        boolean isFile = file.isFile();
        boolean isSupported = SupportedFileTypes.isSupported(file);
        
        return isFile && isSupported;
    }
    
    private ArrayList<File[]> scanDirectory(File parentFile) {
        ArrayList<File[]> directoryFiles = new ArrayList<File[]>();
        
        File[] currentDirFiles = getFilesFromDirectory(parentFile);
        if(currentDirFiles.length != 0)
            directoryFiles.add(currentDirFiles);
        
        File[] subDirectories = getDirectoriesFromDirectory(parentFile);
        for(File subDir : subDirectories)
            directoryFiles.addAll(scanDirectory(subDir));
        
        return directoryFiles;
    }
    
    private File[] getFilesFromDirectory(File directory) {
        File[] currentDirFiles = directory.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                String extension = SupportedFileTypes.getFileExtension(name);
                return SupportedFileTypes.isSupported(extension);
            }
        });
        
        return currentDirFiles;
    }
    
    private File[] getDirectoriesFromDirectory(File directory) {
        File[] subDirectories = directory.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                String filePath = dir.getPath() + File.separator + name;
                File checkFile = new File(filePath);
                return checkFile.isDirectory();
            }
        });
        
        return subDirectories;
    }
    
}
