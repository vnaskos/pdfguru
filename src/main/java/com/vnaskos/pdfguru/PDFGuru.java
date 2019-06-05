package com.vnaskos.pdfguru;

import com.vnaskos.pdfguru.execution.OutputParameters;
import com.vnaskos.pdfguru.execution.ProcessOrchestrator;
import com.vnaskos.pdfguru.execution.document.pdfbox.PdfboxDocumentManager;
import com.vnaskos.pdfguru.input.FileBrowser;
import com.vnaskos.pdfguru.input.FilenameUtils;
import com.vnaskos.pdfguru.input.items.InputItem;
import com.vnaskos.pdfguru.ui.AboutMeFrame;
import com.vnaskos.pdfguru.ui.OutputDialog;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Vasilis Naskos
 */
public class PDFGuru extends JFrame {

    private JSpinner compressionSpinner;
    private JList inputList;
    private JTextField pagesTextField;
    private JTextField outputFilepathField;
    private JCheckBox multipleFileOutputCheckBox;

    private final FileBrowser fileBrowser = createFileChooser(this);
    private final DefaultListModel model;

    public PDFGuru() {
        initComponents();
        this.setLocationRelativeTo(null);

        model = new DefaultListModel();
        inputList.setModel(model);
    }

    FileBrowser createFileChooser(Container parent) {
        return new FileBrowser(parent);
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("PDF Guru v0.3.1");

        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));

        inputList = new JList();
        inputList.addListSelectionListener(this::inputListValueChanged);
        JScrollPane inputScrollPane = new JScrollPane();
        inputScrollPane.setViewportView(inputList);

        JButton addButton = new JButton();
        addButton.setName("addButton");
        addButton.setText("Add");
        addButton.addActionListener(evt -> fileBrowser.selectFiles(this::addElements));

        JButton upButton = new JButton();
        upButton.setText("Up");
        upButton.addActionListener(evt -> moveUp());

        JButton downButton = new JButton();
        downButton.setText("Down");
        downButton.addActionListener(evt -> moveDown());

        JButton removeButton = new JButton();
        removeButton.setText("Remove");
        removeButton.addActionListener(evt -> removeSelected());

        JButton clearButton = new JButton();
        clearButton.setText("Clear");
        clearButton.addActionListener(evt -> model.removeAllElements());

        JPanel pagesPanel = new JPanel();
        pagesPanel.setBorder(BorderFactory.createTitledBorder("Pages"));

        pagesTextField = new JTextField();
        pagesTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                warn();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                warn();
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                warn();
            }

            private void warn() {
                InputItem item = (InputItem) model.get(inputList.getSelectedIndex());
                item.setPagesPattern(pagesTextField.getText());
            }
        });

        JButton pagesHelpButton = new JButton();
        pagesHelpButton.setText("?");
        pagesHelpButton.addActionListener(this::pagesHelpButtonActionPerformed);

        JPanel outputPanel = new JPanel();
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));

        JButton outputBrowseButton = new JButton();
        outputBrowseButton.setText("...");
        outputBrowseButton.addActionListener(evt -> browseOutput());

        outputFilepathField = new JTextField();
        outputFilepathField.setText(System.getProperty("user.home"));

        JLabel compressionLabel = new JLabel();
        compressionLabel.setText("compression:");
        compressionSpinner = new JSpinner();
        compressionSpinner.setModel(new SpinnerNumberModel(Float.valueOf(0.5f), Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(0.1f)));

        multipleFileOutputCheckBox = new JCheckBox();
        multipleFileOutputCheckBox.setText("Export in multiple files");

        JButton okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(this::okButtonActionPerformed);

        JButton aboutButton = new JButton();
        aboutButton.setName("aboutButton");
        aboutButton.setText("About");
        aboutButton.addActionListener(evt -> AboutMeFrame.display());

        GroupLayout inputPanelLayout = new GroupLayout(inputPanel);
        inputPanel.setLayout(inputPanelLayout);
        inputPanelLayout.setHorizontalGroup(
            inputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(addButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(upButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(downButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(removeButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clearButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(inputScrollPane)
        );
        inputPanelLayout.setVerticalGroup(
            inputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, inputPanelLayout.createSequentialGroup()
                .addComponent(inputScrollPane, GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(inputPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(addButton)
                    .addComponent(upButton)
                    .addComponent(downButton)
                    .addComponent(removeButton)
                    .addComponent(clearButton))
                .addContainerGap())
        );

        GroupLayout outputPanelLayout = new GroupLayout(outputPanel);
        outputPanel.setLayout(outputPanelLayout);
        outputPanelLayout.setHorizontalGroup(
            outputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(outputPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(outputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(outputPanelLayout.createSequentialGroup()
                        .addComponent(outputFilepathField)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(outputBrowseButton, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE))
                    .addGroup(outputPanelLayout.createSequentialGroup()
                        .addComponent(multipleFileOutputCheckBox)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE)
                        .addComponent(compressionLabel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(compressionSpinner, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        outputPanelLayout.setVerticalGroup(
            outputPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(outputPanelLayout.createSequentialGroup()
                .addGroup(outputPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(outputBrowseButton)
                    .addComponent(outputFilepathField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(outputPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(compressionSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(compressionLabel)
                    .addComponent(multipleFileOutputCheckBox))
                .addContainerGap())
        );

        GroupLayout jPanel1Layout = new GroupLayout(pagesPanel);
        pagesPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pagesTextField)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pagesHelpButton, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(pagesTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(pagesHelpButton))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(inputPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pagesPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(outputPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(aboutButton, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(okButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(inputPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pagesPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(outputPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(aboutButton))
                .addContainerGap())
        );

        pack();
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        List<InputItem> files = new ArrayList<>();
        float compression = Float.parseFloat(compressionSpinner.getValue().toString());
        String output = outputFilepathField.getText();
        
        for(int i=0; i<model.getSize(); i++) {
            InputItem file = (InputItem) model.get(i);
            files.add(file);
        }
        
        OutputParameters params = new OutputParameters(output);
        params.setCompression(compression);
        if (multipleFileOutputCheckBox.isSelected()) {
            params.setMultiFileOutput();
        }

        PdfboxDocumentManager documentManager = new PdfboxDocumentManager();
        ProcessOrchestrator handler = new ProcessOrchestrator(documentManager, files, params);
        OutputDialog outputDialog = new OutputDialog(files.size(), handler);
        outputDialog.setVisible(true);
        documentManager.registerProgressListener(outputDialog);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                handler.startProcess();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void inputListValueChanged(javax.swing.event.ListSelectionEvent evt) {
        try {
            InputItem item = (InputItem) model.get(inputList.getSelectedIndex());
            pagesTextField.setText(item.getPagesPattern());
        } catch(Exception e) {
            
        }
    }

    private void pagesHelpButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String dialogText = "Leave empty for all pages\n" +
                "Use page numbers separated by comma \"1,2,3\"\n" +
                "Use number intervals \"1-4\"\n" +
                "Use | character to group results \"1-4|3-5|3,4\"\n" +
                "1 is the first page, $ is the last";
        
        JOptionPane.showMessageDialog(this, dialogText);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String... args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PDFGuru.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> new PDFGuru().setVisible(true));
    }

    void addElements(File[] selectedFiles) {
        if (selectedFiles == null) {
            return;
        }

        for (File file : selectedFiles) {
            String path = FilenameUtils.normalize(file.getPath());
            InputItem item = new InputItem(path);
            addToModel(item);
        }
    }

    void addToModel(InputItem item) {
        model.add(model.size(), item);
    }

    private void moveUp() {
        int[] selected = inputList.getSelectedIndices();
        for (int i = 0; i < selected.length; i++) {
            moveElement(selected[i], -1);
            selected[i]--;
        }
        inputList.setSelectedIndices(selected);
    }
    
    private void moveDown() {
        int[] selected = inputList.getSelectedIndices();
        for (int i = selected.length - 1; i >= 0; i--) {
            moveElement(selected[i], 1);
            selected[i]++;
        }
        inputList.setSelectedIndices(selected);
    }
    
    private void moveElement(int indexOfSelected, int direction) {
        swapElements(indexOfSelected, indexOfSelected + 1 * direction);
        indexOfSelected = indexOfSelected + 1 * direction;
        inputList.setSelectedIndex(indexOfSelected);
        inputList.updateUI();
    }
    
    private void swapElements(int pos1, int pos2) {
        Object tmp = model.get(pos1);
        model.set(pos1, model.get(pos2));
        model.set(pos2, tmp);
    }

    private void removeSelected() {
        int[] selected = inputList.getSelectedIndices();
        for (int i = selected.length - 1; i >= 0; i--) {
            model.remove(selected[i]);
        }
    }
    
    private void browseOutput() {
        JFileChooser jChooser = fileBrowser.getFileChooser();

        if (jChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        if (jChooser.getSelectedFile() != null) {
            String path = jChooser.getSelectedFile().getPath();
            path = FilenameUtils.normalize(path);
            path += path.toLowerCase().endsWith(".pdf") ? "" : ".pdf";
            outputFilepathField.setText(path);
        }
    }
}
