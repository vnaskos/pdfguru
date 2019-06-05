package com.vnaskos.pdfguru.ui;

import com.vnaskos.pdfguru.ProcessListener;
import com.vnaskos.pdfguru.execution.Process;
import com.vnaskos.pdfguru.OutputParameters;
import com.vnaskos.pdfguru.InputItem;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Vasilis Naskos
 */
public class PDFGuru extends JFrame {

    private final FileBrowser fileBrowser = createFileChooser();

    private InputItemJList inputList;
    private JTextField pagesTextField;
    private SpinnerNumberModel compressionModel;
    private JCheckBox multipleFileOutputCheckBox;
    private JTextField outputFilepathField;

    public PDFGuru() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setTitle("PDF Guru v0.3.2");

        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input"));

        inputList = new InputItemJList();
        inputList.setName("inputList");
        inputList.addListSelectionListener(evt -> inputListValueChanged());
        JScrollPane inputScrollPane = new JScrollPane();
        inputScrollPane.setViewportView(inputList);

        JButton addButton = new JButton();
        addButton.setName("addButton");
        addButton.setText("Add");
        addButton.addActionListener(evt -> fileBrowser.selectInputFiles(this::addElements));

        JButton upButton = new JButton();
        upButton.setName("upButton");
        upButton.setText("Up");
        upButton.addActionListener(evt -> inputList.moveSelectedUp());

        JButton downButton = new JButton();
        downButton.setName("downButton");
        downButton.setText("Down");
        downButton.addActionListener(evt -> inputList.moveSelectedDown());

        JButton removeButton = new JButton();
        removeButton.setName("removeButton");
        removeButton.setText("Remove");
        removeButton.addActionListener(evt -> inputList.removeSelected());

        JButton clearButton = new JButton();
        clearButton.setName("clearButton");
        clearButton.setText("Clear");
        clearButton.addActionListener(evt -> inputList.removeAll());

        JPanel pagesPanel = new JPanel();
        pagesPanel.setBorder(BorderFactory.createTitledBorder("Pages"));

        pagesTextField = new JTextField();
        pagesTextField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateInputItem();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateInputItem();
            }

            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateInputItem();
            }

            private void updateInputItem() {
                inputList.getSelectedValue().setPagesPattern(pagesTextField.getText());
            }
        });

        JButton pagesHelpButton = new JButton();
        pagesHelpButton.setName("pagesHelpButton");
        pagesHelpButton.setText("?");
        pagesHelpButton.addActionListener(evt -> pagesHelpButtonActionPerformed());

        JPanel outputPanel = new JPanel();
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output"));

        JButton outputBrowseButton = new JButton();
        outputBrowseButton.setName("outputBrowseButton");
        outputBrowseButton.setText("...");
        outputBrowseButton.addActionListener(evt -> fileBrowser.selectOutputDirectory(files -> browseOutput(files[0])));

        outputFilepathField = new JTextField();
        outputFilepathField.setText(System.getProperty("user.home"));

        JLabel compressionLabel = new JLabel();
        compressionLabel.setText("compression:");

        compressionModel = new SpinnerNumberModel(
                Float.valueOf(0.5f), Float.valueOf(0.0f),
                Float.valueOf(1.0f), Float.valueOf(0.1f));

        JSpinner compressionSpinner = new JSpinner();
        compressionSpinner.setModel(compressionModel);

        multipleFileOutputCheckBox = new JCheckBox();
        multipleFileOutputCheckBox.setText("Export in multiple files");

        JButton okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(evt -> okButtonActionPerformed());

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

    private FileBrowser createFileChooser() {
        return new FileBrowser();
    }

    private void okButtonActionPerformed() {
        List<InputItem> files = inputList.getItems();

        OutputParameters params = new OutputParameters(outputFilepathField.getText());
        params.setCompression(compressionModel.getNumber().floatValue());
        if (multipleFileOutputCheckBox.isSelected()) {
            params.setMultiFileOutput();
        }

        ProcessListener process = new Process();

        OutputDialog outputDialog = new OutputDialog(files.size());
        outputDialog.setVisible(true);
        outputDialog.setProcessListener(process);

        process.run(files, params, outputDialog);
    }

    private void inputListValueChanged() {
        if (inputList.getSelectedValue() == null) {
            return;
        }
        pagesTextField.setText(inputList.getSelectedValue().getPagesPattern());
    }

    private void pagesHelpButtonActionPerformed() {
        String dialogText = "Leave empty for all pages\n" +
                "Use page numbers separated by comma \"1,2,3\"\n" +
                "Use number intervals \"1-4\"\n" +
                "Use | character to group results \"1-4|3-5|3,4\"\n" +
                "1 is the first page, $ is the last";
        
        JOptionPane.showMessageDialog(this, dialogText);
    }

    public void addElements(File[] selectedFiles) {
        if (selectedFiles == null) {
            return;
        }

        Arrays.stream(selectedFiles)
                .map(File::getPath)
                .map(InputItem::new)
                .forEach(this::addToModel);
    }

    void addToModel(InputItem item) {
        inputList.addItem(item);
    }
    
    private void browseOutput(File outputDirectory) {
        if (outputDirectory == null) {
            return;
        }

        outputFilepathField.setText(outputDirectory.getAbsolutePath() + File.separator);
    }
}
