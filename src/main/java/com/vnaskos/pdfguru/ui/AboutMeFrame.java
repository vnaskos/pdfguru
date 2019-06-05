package com.vnaskos.pdfguru.ui;

import javax.swing.*;

/**
 *
 * @author Vasilis Naskos
 */
public class AboutMeFrame extends JFrame {

    private AboutMeFrame() {
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("About PDF Guru");

        JTextPane infoPane = new JTextPane();
        infoPane.setName("infoPane");
        infoPane.setEditable(false);
        infoPane.setContentType("text/html");
        infoPane.setText(
                "<html><body><center>" +
                "<p>created by <a href=\"http://vnaskos.com\">vnaskos</a></p>" +
                "<p>vnaskos.com</p>" +
                "</center></body></html>");

        JScrollPane infoScrollPane = new JScrollPane();
        infoScrollPane.setViewportView(infoPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(infoScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(infoScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }

    public static AboutMeFrame display() {
        AboutMeFrame aboutMeForm = new AboutMeFrame();
        aboutMeForm.setVisible(true);
        return aboutMeForm;
    }
}
