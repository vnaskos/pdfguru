package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.OutputDialog;
import com.vnaskos.pdfguru.PDFGuru;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.Sanselan;

/**
 *
 * @author Vasilis Naskos
 */
public class ProcessHandler {
    
    private final List<String> files;
    private final boolean useTemp;
    private final float compression;
    private final String outputFile;

    public ProcessHandler(List<String> files, boolean useTemp, float compression, String outputFile) {
        this.files = files;
        this.useTemp = useTemp;
        this.compression = compression;
        this.outputFile = outputFile;
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
    
    private void startProcess() throws IOException, COSVisitorException {
        PDFMergerUtility ut = new PDFMergerUtility();
        OutputDialog progress = new OutputDialog();
        progress.setVisible(true);
        progress.setProgressMax(files.size());

        for (int i = 0; i < files.size(); i++) {
            if (!progress.isVisible()) {
                return;
            }
            String file = files.get(i);
            progress.updateLog(file);

            if (file.toLowerCase().endsWith(".pdf")) {
                if (!isFileEncrypted(file)) {
                    ut.addSource(file);
                } else {
                    progress.updateLog("Skip! " + file + " is encrypted");
                }
            } else {
                byte[] bytes = getImageAsByteArray(file);
                if (useTemp) {
                    File tmp = File.createTempFile("guru", ".tmp");
                    tmp.deleteOnExit();
                    FileOutputStream fout = new FileOutputStream(tmp);
                    fout.write(bytes);
                    ut.addSource(tmp);
                } else {
                    ut.addSource(new ByteArrayInputStream(bytes));
                }
            }

            progress.updateProgress(i + 1);
        }

        ut.setDestinationFileName(outputFile);
        ut.mergeDocuments();

        progress.updateLog("Completed!");
    }
    
    byte[] getImageAsByteArray(String file) throws IOException, COSVisitorException {
        PDDocument doc = new PDDocument();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        BufferedImage bufferedImage = loadImage(file);

        PDPage page = new PDPage(new PDRectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
        doc.addPage(page);

        PDXObjectImage ximage = new PDJpeg(doc, bufferedImage, compression);

        PDPageContentStream content = new PDPageContentStream(doc, page);

        content.drawImage(ximage, 0, 0);
        content.close();

        doc.save(baos);
        doc.close();

        return baos.toByteArray();
    }
    
    private BufferedImage loadImage(String file) throws IOException {
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
        BufferedInputStream bis = new BufferedInputStream(fis);
        PDFParser parser = new PDFParser(bis);
        parser.parse();

        PDDocument originialPdfDoc = parser.getPDDocument();

        boolean isOriginalDocEncrypted = originialPdfDoc.isEncrypted();
        originialPdfDoc.close();
        parser.clearResources();
//        if (isOriginalDocEncrypted) {
//            originialPdfDoc.openProtection(new StandardDecryptionMaterial("password"));
//        }

        return isOriginalDocEncrypted;
    }
    
}
