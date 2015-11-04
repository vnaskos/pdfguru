package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.OutputDialog;
import com.vnaskos.pdfguru.PDFGuru;
import com.vnaskos.pdfguru.input.items.InputItem;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
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

/**
 *
 * @author Vasilis Naskos
 */
public class ProcessHandler {
    
    private final List<InputItem> files;
    private final boolean separateFiles;
    private final float compression;
    private final String outputFile;

    public ProcessHandler(List<InputItem> files, OutputParameters params) {
        this.files = files;
        this.compression = params.getCompression();
        this.outputFile = params.getOutputFile();
        this.separateFiles = params.isSeparateFiles();
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
        OutputDialog progress = new OutputDialog();
        progress.setVisible(true);
        progress.setProgressMax(files.size());
        
        PDDocument newDoc = new PDDocument();
        PDDocument originialPdfDoc = null;
        
        int o = 1;
        
        for (int i=0; i<files.size(); i++) {
            InputItem file = files.get(i);
            
            if (!progress.isVisible()) {
                return;
            }
            progress.updateLog(file.getPath());
            if (isPDF(file)) {
                if (isFileEncrypted(file.getPath())) {
                    progress.updateLog("Skip! " + file + " is encrypted");
                    continue;
                }
                FileInputStream fis = new FileInputStream(file.getPath());
                PDFParser parser = new PDFParser(fis);
                parser.parse();
                originialPdfDoc = parser.getPDDocument();
                PDDocumentCatalog cat = originialPdfDoc.getDocumentCatalog();
                List<PDPage> pages = cat.getAllPages();
                String pagesField = file.getPages();
                if(pagesField.isEmpty()) {
                    for(PDPage p : pages) {
                        newDoc.importPage(p);
                    }
                    if (separateFiles) {
                        newDoc.save(getOutputName(outputFile, o++));
                        newDoc = new PDDocument();
                    }
                } else {
                    pagesField = pagesField.replaceAll("\\$", pages.size()+"");
                    String[] groups = pagesField.split("\\|");
                    for(String g : groups) {
                        String[] subGroups = g.split(",");
                        for(String sub : subGroups) {
                            if(sub.contains("-")) {
                                String[] lim = sub.split("-");
                                int lim0 = Integer.parseInt(lim[0]);
                                int lim1 = Integer.parseInt(lim[1]);
                                if(lim0 < lim1) {
                                    for(int j=lim0; j<=lim1; j++ ) {
                                        newDoc.importPage(pages.get(j-1));
                                    }
                                } else {
                                    for(int j=lim0; j>=lim1; j--) {
                                        newDoc.importPage(pages.get(j-1));
                                    }
                                }
                            } else {
                                int p = Integer.parseInt(sub);
                                newDoc.importPage(pages.get(p-1));
                            }
                            if(separateFiles) {
                                newDoc.save(getOutputName(outputFile, o++));
                                newDoc = new PDDocument();
                            }
                        }
                    }
                }
                fis.close();
            } else {
                BufferedImage bufferedImage = loadImage(file.getPath());
                if(bufferedImage != null) {
                    PDPage page = new PDPage(new PDRectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
                    newDoc.addPage(page);
                    PDXObjectImage ximage = new PDJpeg(newDoc, bufferedImage, compression);
                    PDPageContentStream content = new PDPageContentStream(newDoc, page);
                    content.drawImage(ximage, 0, 0);
                    content.close();
                }
                
                if(separateFiles) {
                    newDoc.save(getOutputName(outputFile, o++));
                    newDoc = new PDDocument();
                }
            }
            progress.updateProgress(i + 1);
        }
        if (!separateFiles) {
            newDoc.save(getOutputName(outputFile, o++));
        }
        newDoc.close();
        if(originialPdfDoc != null) {
            originialPdfDoc.close();
        }
        progress.updateLog("Completed!");
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
    
    byte[] getImageAsByteArray(String file) throws IOException, COSVisitorException {
        PDDocument doc = new PDDocument();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        BufferedImage bufferedImage = loadImage(file);

        PDPage page = new PDPage(new PDRectangle(bufferedImage.getWidth(), bufferedImage.getHeight()));
        doc.importPage(page);

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
//        BufferedInputStream bis = new BufferedInputStream(fis);
        PDFParser parser = new PDFParser(fis);
        parser.parse();

        PDDocument originialPdfDoc = parser.getPDDocument();

        boolean isOriginalDocEncrypted = originialPdfDoc.isEncrypted();
        originialPdfDoc.close();
        parser.clearResources();
//        if (isOriginalDocEncrypted) {
//            originialPdfDoc.openProtection(new StandardDecryptionMaterial("password"));
//        }

        fis.close();
        return isOriginalDocEncrypted;
    }
    
}
