package com.vnaskos.pdfguru;

import com.vnaskos.pdfguru.pdfbox.PdfboxProcessFactory;
import com.vnaskos.pdfguru.ui.PDFGuru;

import javax.swing.*;

public class Main {

    private final ProcessFactory processFactory = new PdfboxProcessFactory();
//    private final ProcessFactory processFactory = new ITextProcessFactory();

    private PDFGuru mainWindow;

    private Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        new Main();
    }

    private void startUserInterface() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        java.awt.EventQueue.invokeLater(() -> {
            mainWindow = new PDFGuru();
            mainWindow.setVisible(true);
            mainWindow.setProcessFactory(processFactory);
        });
    }
}
