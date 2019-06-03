package com.vnaskos.pdfguru.execute;

import com.vnaskos.pdfguru.input.items.InputItem;
import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;
import org.mockito.Mockito;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProcessHandlerTest {

    @Test
    public void addNewPdfPageFromALoadedImageOnComputer()
            throws IOException, COSVisitorException {
        List<InputItem> inputFiles = new ArrayList<>();

        File inputImage = new File("src/test/resources/sample.jpg");
        InputItem imageItem = new InputItem(inputImage.getAbsolutePath());
        inputFiles.add(imageItem);

        OutputParameters outputParameters = new OutputParameters.Builder().build();
        ProcessHandler processHandler = new ProcessHandler(inputFiles, outputParameters);
        ProcessHandler processHandlerSpy = spy(processHandler);

        processHandlerSpy.addImage(inputFiles.get(0));

        verify(processHandlerSpy, times(1)).addPage(any());
    }
}
