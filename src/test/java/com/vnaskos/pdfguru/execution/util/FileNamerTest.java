package com.vnaskos.pdfguru.execution.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class FileNamerTest {

    private static final String RANDOM_PATH = "/somewhere/over/the/rainbow";

    @Test
    public void createOutputNameCombiningOutputPathIndexAndExtension() {
        FileNamer fileNamer = new FileNamer();

        String outputFileName = fileNamer.createUniqueOutputFileName(RANDOM_PATH);

        assertThat(outputFileName).isEqualTo("/somewhere/over/the/rainbow_1.pdf");
    }

    @Test
    public void createOutputNameWhenOutputPathContainsExtensionMaintainExtensionLast() {
        FileNamer fileNamer = new FileNamer();

        String outputFileName = fileNamer.createUniqueOutputFileName(RANDOM_PATH + ".pdf");

        assertThat(outputFileName).isEqualTo("/somewhere/over/the/rainbow_1.pdf");
    }

    @Test
    public void createUniqueOutputNameWhenFilesWithTheFirstTwoIndicesAlreadyExists() {
        FileNamer fileNamer = spy(new FileNamer());

        doReturn(true).when(fileNamer).fileExists("/somewhere/over/the/rainbow_1.pdf");
        doReturn(true).when(fileNamer).fileExists("/somewhere/over/the/rainbow_2.pdf");

        String outputFileName = fileNamer.createUniqueOutputFileName(RANDOM_PATH);

        assertThat(outputFileName).isEqualTo("/somewhere/over/the/rainbow_3.pdf");
    }

}