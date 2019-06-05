package com.vnaskos.pdfguru;

import com.vnaskos.pdfguru.execution.ExecutionControlListener;
import com.vnaskos.pdfguru.execution.document.ExecutionProgressListener;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 * @author Vasilis Naskos
 */
public class OutputDialog extends javax.swing.JFrame implements ExecutionProgressListener {

    private JButton cancelButton;
    private JTextArea logTextArea;
    private JProgressBar progressBar;

    private ExecutionControlListener executionControlListener;
    private final int totalItemsToProcess;
    private int processedItems = 0;

    public OutputDialog(int totalItemsToProcess, ExecutionControlListener executionControlListener) {
        this.totalItemsToProcess = totalItemsToProcess;
        this.executionControlListener = executionControlListener;

        initComponents();
        this.setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Progress");

        cancelButton = new JButton();
        cancelButton.setName("cancelButton");
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(this::cancelButtonActionPerformed);

        logTextArea = new JTextArea();
        logTextArea.setName("logTextArea");
        logTextArea.setEditable(false);
        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        logTextArea.setBorder(javax.swing.BorderFactory.createTitledBorder("Output Progress Log"));

        JScrollPane logScrollPane = new JScrollPane();
        logScrollPane.setViewportView(logTextArea);

        progressBar = new JProgressBar();
        progressBar.setName("progressBar");
        progressBar.setStringPainted(true);
        progressBar.setMaximum(totalItemsToProcess);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(logScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addGap(7, 7, 7))
        );

        pack();
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        executionControlListener.requestStop();
        this.dispose();
    }

    @Override
    public void incrementProgress() {
        processedItems++;

        SwingUtilities.invokeLater(() -> progressBar.setValue(processedItems));

        if (allItemsProcessed()) {
            finish();
        }
    }

    private boolean allItemsProcessed() {
        return processedItems == totalItemsToProcess;
    }

    private void finish() {
        updateStatus("Completed!");
        SwingUtilities.invokeLater(() -> cancelButton.setText("OK"));
    }

    @Override
    public void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> logTextArea.setText(
                String.format("%s\n%s", status, logTextArea.getText())));
    }
}
