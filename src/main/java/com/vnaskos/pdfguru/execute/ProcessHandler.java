package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.input.items.InputItem;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode.APPEND;

/**
 *
 * @author Vasilis Naskos
 */
public class ProcessHandler implements ExecutionControlListener {

    private List<ExecutionProgressListener> progressListeners = new ArrayList<>();
    private boolean stopRequested = false;

    private final List<InputItem> inputItems;
    private final OutputParameters outputParameters;

    private PDDocument newDoc;
    private int fileIndex;
    private PDDocument originalPdfDoc;
    private PDPageTree pages;

    public ProcessHandler(List<InputItem> inputItems, OutputParameters outputParameters) {
        this.inputItems = inputItems;
        this.outputParameters = outputParameters;

        fileIndex = 1;
    }

    public void registerProgressListener(ExecutionProgressListener listener) {
        progressListeners.add(listener);
    }

    private void tryToStartProcess() {
        try {
            startProcess();
        } catch (IOException ex) {
            Logger.getLogger(ProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void startProcess() throws IOException {
        newDoc = new PDDocument();

        for (InputItem file : inputItems) {
            if (stopRequested) {
                return;
            }

            progressListeners.forEach(l -> l.updateStatus(file.getPath()));
            if (isPDF(file)) {
                addPDF(file);
            } else {
                addImage(file);
            }
            progressListeners.forEach(ExecutionProgressListener::incrementProgress);

            if (outputParameters.isMultipleFileOutput()) {
                saveDocument();
                newDoc = new PDDocument();
            }
        }

        if (outputParameters.isSingleFileOutput()) {
            saveDocument();
        }

        progressListeners.forEach(ExecutionProgressListener::finish);
    }

    void addPage(PDPage page) throws IOException {
        newDoc.importPage(page);
    }

    void saveDocument() throws IOException {
        String name = getOutputName(outputParameters.getOutputFile(), fileIndex++);
        newDoc.save(name);
        newDoc.close();

        if (originalPdfDoc != null) {
            originalPdfDoc.close();
        }
    }
    
    private void addPDF(InputItem file)
            throws IOException {
        originalPdfDoc = PDDocument.load(new File(file.getPath()));

        if (originalPdfDoc.isEncrypted()) {
            originalPdfDoc.close();
            progressListeners.forEach(l -> l.updateStatus("Skip encrypted! %s" + file));
            return;
        }

        PDDocumentCatalog cat = originalPdfDoc.getDocumentCatalog();
        pages = cat.getPages();
        String pagesField = file.getPages();
        
        if (pagesField.isEmpty()) {
            addAllPDFPages();
        } else {
            addSelectedPDFPages(pagesField);
        }
    }
    
    private void addAllPDFPages()
            throws IOException {
        for (PDPage p : pages) {
            addPage(p);
        }
    }
    
    private void addSelectedPDFPages(String pagesField)
            throws IOException {
        pagesField = pagesField.replaceAll("\\$", originalPdfDoc.getNumberOfPages() + "");
        String[] groups = pagesField.split("\\|");
        for (String g : groups) {
            createGroups(g);
        }
    }
    
    private void createGroups(String g) throws IOException {
        String[] subGroups = g.split(",");
        for (String sub : subGroups) {
            if (sub.contains("-")) {
                addPagesWithInterval(sub);
            } else {
                int p = Integer.parseInt(sub);
                addPage(pages.get(p - 1));
            }
        }
    }
    
    private void addPagesWithInterval(String sub) throws IOException {
        String[] lim = sub.split("-");
        int lim0 = Integer.parseInt(lim[0]);
        int lim1 = Integer.parseInt(lim[1]);
        if (lim0 < lim1) {
            for (int j = lim0; j <= lim1; j++) {
                addPage(pages.get(j - 1));
            }
        } else {
            for (int j = lim0; j >= lim1; j--) {
                addPage(pages.get(j - 1));
            }
        }
    }
    
    void addImage(InputItem file) throws IOException {
        BufferedImage image = loadImage(file.getPath());
        
        if (image == null) {
            return;
        }

        originalPdfDoc = new PDDocument();
        PDPage page = addBlankPage(originalPdfDoc, image.getWidth(), image.getHeight());
        PDImageXObject pdImage = JPEGFactory.createFromImage(originalPdfDoc, image, outputParameters.getCompression());

        try (PDPageContentStream contentStream = new PDPageContentStream(originalPdfDoc, page, APPEND, true, true)) {
            contentStream.drawImage(pdImage, 0, 0, pdImage.getWidth(), pdImage.getHeight());
        }

        addPage(page);
    }
    
    private String getOutputName(String name, int o) {
        String out;
        
        if(name.toLowerCase().endsWith(".pdf")) {
            out = name.substring(0, name.length()-4);
        } else {
            out = name;
        }
        
        String testOut = out + "_" + o + ".pdf";
        while((new File(testOut)).exists()) {
            o++;
            testOut = out + "_" + o + ".pdf";
        }
        
        return testOut;
    }
    
    private boolean isPDF(InputItem item) {
        String file = item.getPath().toLowerCase();
        
        return file.endsWith(".pdf");
    }
    
    private BufferedImage loadImage(String file) {
        BufferedImage bufferedImage;

        try {
            bufferedImage = Sanselan.getBufferedImage(new File(file));
        } catch (IOException | ImageReadException ex) {
            try {
                bufferedImage = ImageIO.read(new File(file));
            } catch (IOException e) {
                // log: skip image, could not be loaded
                return null;
            }
        }

        return bufferedImage;
    }
    
    public void execute() {
        Runnable task = this::tryToStartProcess;

        Thread t1 = new Thread(task);
        t1.start();
    }

    PDPage addBlankPage(PDDocument document, float width, float height) {
        PDPage page = new PDPage(
                new PDRectangle(width, height));

        document.addPage(page);

        return page;
    }

    @Override
    public void requestStop() {
        this.stopRequested = true;
    }
}
