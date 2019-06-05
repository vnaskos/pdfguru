package com.vnaskos.pdfguru.input;

import java.awt.Container;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Vasilis Naskos
 */
public class FileBrowser {
    
    private JFileChooser fileChooser;
    private Container parent;
    private static String lastDir;
    
    public FileBrowser() {
        setupFileChooser();
    }
    
    public FileBrowser(Container parent) {
        this();
        
        this.parent = parent;
    }
    
    private void setupFileChooser() {
        fileChooser = new JFileChooser(lastDir);
        setFileChooserProperties();
    }
    
    private void setFileChooserProperties() {
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter[] filters = {
            new FileNameExtensionFilter("JPEG Image(*.jpg, *.jpeg)", "JPG", "JPEG"),
            new FileNameExtensionFilter("PNG Images(*.png)", "PNG"),
            new FileNameExtensionFilter("BMP Images(*.bmp)", "BMP"),
            new FileNameExtensionFilter("TIFF Images(*.tiff, *.tif)", "TIFF", "TIF"),
            new FileNameExtensionFilter("GIF Images(*.gif)", "GIF"),
            new FileNameExtensionFilter("Photoshop Files(*.psd)", "PSD"),
            new FileNameExtensionFilter("TGA Images(*.tga)", "TGA"),
            new FileNameExtensionFilter("PDF Files(*.pdf)", "PDF"),
            new FileNameExtensionFilter("All supported file types", "JPG", "JPEG", "PNG", "GIF", "BMP", "TIFF", "TIF", "TGA", "PSD", "PDF")
        };

        for (FileNameExtensionFilter filter : filters) {
            fileChooser.setFileFilter(filter);
        }
    }
    
    public JFileChooser getFileChooser() {
        return fileChooser;
    }
    
    public void selectFiles(SelectedFilesCallback callback) {
        if (fileChooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
            callback.handleSelectedFiles(new File[0]);
            return;
        }

        lastDir = fileChooser.getSelectedFile().getPath();
        callback.handleSelectedFiles(fileChooser.getSelectedFiles());
    }
    
}
