package com.vnaskos.pdfguru.ui;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.nio.file.Paths;

/**
 *
 * @author Vasilis Naskos
 */
class FileBrowser extends JFileChooser {
    
    FileBrowser() {
        super();
    }
    
    private void configureFileBrowserForInput() {
        setMultiSelectionEnabled(true);
        setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
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
            setFileFilter(filter);
        }
    }

    private void configureFileBrowserForOutput() {
        setMultiSelectionEnabled(false);
        setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        resetChoosableFileFilters();
    }

    void selectInputFiles(FileArrayConsumer callback) {
        configureFileBrowserForInput();

        if (showOpenDialog(getParent()) != JFileChooser.APPROVE_OPTION) {
            callback.handleFiles();
            return;
        }

        DirectoryScanner.scanSubDirectoriesForSupportedFiles(getSelectedFiles(), callback);
    }

    void selectOutputDirectory(FileArrayConsumer callback) {
        configureFileBrowserForOutput();

        if (showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        callback.handleFiles(Paths.get(getSelectedFile().getPath())
                .normalize()
                .toFile());
    }
    
}
