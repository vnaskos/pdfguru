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
import java.util.stream.IntStream;

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

        for (InputItem inputItem : inputItems) {
            if (stopRequested) {
                return;
            }

            progressListeners.forEach(l -> l.updateStatus(inputItem.getPath()));
            if (inputItem.isPdf()) {
                addPDF(inputItem);
            } else {
                addImage(inputItem);
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
        PDPageTree pages = cat.getPages();
        String pagesPattern = file.getPages();

        for (int page : getSelectedPageIndicesFor(pagesPattern, originalPdfDoc.getNumberOfPages())) {
            addPage(pages.get(page-1));
        }
    }

    List<Integer> getSelectedPageIndicesFor(final String selectedPagesPattern, final int lastPage) {
        List<Integer> selectedPages = new ArrayList<>();

        String pagesPattern = selectedPagesPattern;

        if (pagesPattern.isEmpty()) {
            pagesPattern = "1-$";
        }

        pagesPattern = pagesPattern.replaceAll("\\$", String.valueOf(lastPage));

        for (String pipeSeparatedPattern : pagesPattern.split("\\|")) {
            for (String commaSeparatedPattern : pipeSeparatedPattern.split(",")) {
                if (commaSeparatedPattern.contains("-")) {
                    String[] limits = commaSeparatedPattern.split("-");
                    IntStream.rangeClosed(Integer.parseInt(limits[0]), Integer.parseInt(limits[1]))
                            .forEach(selectedPages::add);
                } else {
                    selectedPages.add(Integer.parseInt(commaSeparatedPattern));
                }
            }
        }

        return selectedPages;
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

    private PDPage addBlankPage(PDDocument document, float width, float height) {
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
