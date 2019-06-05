package com.vnaskos.pdfguru;

import com.vnaskos.pdfguru.ui.PDFGuru;

public class Main {

    public static void main(String... args) {
        java.awt.EventQueue.invokeLater(() -> new PDFGuru().setVisible(true));
    }
}
