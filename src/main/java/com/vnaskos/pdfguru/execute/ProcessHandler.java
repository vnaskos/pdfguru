package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.OutputDialog;
import com.vnaskos.pdfguru.PDFGuru;
import com.vnaskos.pdfguru.input.items.InputItem;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vasilis Naskos
 */
public class ProcessHandler {
    
    private final List<InputItem> files;
    private final boolean separateFiles;
    private final float compression;
    private final String outputFile;
    private PDDocument newDoc;
    private int fileIndex;
    private OutputDialog progress;
    private PDDocument originialPdfDoc;
    private List<PDPage> pages;

    public ProcessHandler(List<InputItem> files, OutputParameters params) {
        this.files = files;
        this.compression = params.getCompression();
        this.outputFile = params.getOutputFile();
        this.separateFiles = params.isSeparateFiles();

        newDoc = new PDDocument();
        fileIndex = 1;
    }

    private void tryToStartProcess() {
        try {
            startProcess();
        } catch (IOException ex) {
            Logger.getLogger(ProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (COSVisitorException ex) {
            Logger.getLogger(ProcessHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    void startProcess() throws IOException, COSVisitorException {
        progress = createOutputDialog();
        progress.setVisible(true);
        progress.setProgressMax(files.size());
        
        for (int i=0; i<files.size(); i++) {
            InputItem file = files.get(i);
            
            if (!progress.isVisible()) {
                return;
            }
            
            progress.updateLog(file.getPath());
            if (isPDF(file)) {
                addPDF(file);
            } else {
                addImage(file);
            }
            progress.updateProgress(i + 1);
        }
        
        saveAndCleanUp();
        progress.updateLog("Completed!");
    }

    OutputDialog createOutputDialog() {
        return new OutputDialog();
    }

    private void saveAndCleanUp() throws IOException, COSVisitorException {
        if (!separateFiles) {
            saveFile();
        }
        
        newDoc.close();
        
        if (originialPdfDoc != null) {
            originialPdfDoc.close();
        }
    }
    
    private void addPDF(InputItem file)
            throws FileNotFoundException, IOException, COSVisitorException {
        if (isFileEncrypted(file.getPath())) {
            progress.updateLog("Skip! " + file + " is encrypted");
            return;
        }
        
        originialPdfDoc = PDDocument.load(file.getPath());
        PDDocumentCatalog cat = originialPdfDoc.getDocumentCatalog();
        pages = cat.getAllPages();
        String pagesField = file.getPages();
        
        if (pagesField.isEmpty()) {
            addAllPDFPages();
        } else {
            addSelectedPDFPages(pagesField);
        }
    }
    
    private void addAllPDFPages()
            throws IOException, COSVisitorException {
        for (PDPage p : pages) {
            newDoc.importPage(p);
        }
        if (separateFiles) {
            saveFile();
        }
    }
    
    private void addSelectedPDFPages(String pagesField)
            throws IOException, COSVisitorException {
        pagesField = pagesField.replaceAll("\\$", pages.size() + "");
        String[] groups = pagesField.split("\\|");
        for (String g : groups) {
            createGroups(g);
            if (separateFiles) {
                saveFile();
            }
        }
    }
    
    private void createGroups(String g) throws IOException {
        String[] subGroups = g.split(",");
        for (String sub : subGroups) {
            if (sub.contains("-")) {
                addPagesWithInterval(sub);
            } else {
                int p = Integer.parseInt(sub);
                newDoc.importPage(pages.get(p - 1));
            }
        }
    }
    
    private void addPagesWithInterval(String sub) throws IOException {
        String[] lim = sub.split("-");
        int lim0 = Integer.parseInt(lim[0]);
        int lim1 = Integer.parseInt(lim[1]);
        if (lim0 < lim1) {
            for (int j = lim0; j <= lim1; j++) {
                newDoc.importPage(pages.get(j - 1));
            }
        } else {
            for (int j = lim0; j >= lim1; j--) {
                newDoc.importPage(pages.get(j - 1));
            }
        }
    }
    
    void addImage(InputItem file)
            throws IOException, COSVisitorException {
        BufferedImage image = loadImage(file.getPath());
        
        if (image == null) {
            return;
        }

        PDPage page = addBlankPage(image.getWidth(), image.getHeight());

        try (PDPageContentStream content = new PDPageContentStream(newDoc, page)) {
            PDXObjectImage ximage = new PDJpeg(newDoc, image, compression);
            content.drawImage(ximage, 0, 0);
        }

        if (separateFiles) {
            saveFile();
        }
    }

    void saveFile() throws IOException, COSVisitorException {
        String name = getOutputName(outputFile, fileIndex++);
        newDoc.save(name);
        newDoc = new PDDocument();
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
    
    BufferedImage loadImage(String file) throws IOException {
        BufferedImage bufferedImage = null;

        try {
            bufferedImage = Sanselan.getBufferedImage(new File(file));
        } catch (ImageReadException ex) {
            bufferedImage = ImageIO.read(new File(file));
        }

        return bufferedImage;
    }
    
    public void execute() {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                tryToStartProcess();
            }
        };

        Thread t1 = new Thread(task);
        t1.start();
    };
    
    private boolean isFileEncrypted(String file) {
        try {
            return isEncrypted(file);
        } catch (IOException ex) {
            Logger.getLogger(PDFGuru.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    private boolean isEncrypted(String file) throws IOException {
        File originalPDF = new File(file);
        FileInputStream fis = new FileInputStream(originalPDF);
//        BufferedInputStream bis = new BufferedInputStream(fis);
        PDFParser parser = new PDFParser(fis);
        parser.parse();

        PDDocument pdfDoc = parser.getPDDocument();

        boolean isOriginalDocEncrypted = pdfDoc.isEncrypted();
        pdfDoc.close();
        parser.clearResources();
//        if (isOriginalDocEncrypted) {
//            originialPdfDoc.openProtection(new StandardDecryptionMaterial("password"));
//        }

        fis.close();
        return isOriginalDocEncrypted;
    }

    PDPage addBlankPage(float width, float height) {
        PDPage page = new PDPage(
                new PDRectangle(width, height));

        newDoc.addPage(page);

        return page;
    }
}
