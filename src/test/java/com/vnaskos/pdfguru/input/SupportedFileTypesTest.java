package com.vnaskos.pdfguru.input;

import org.junit.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

public class SupportedFileTypesTest {

    @Test
    public void pdfExtensionShouldBeASupportedFileType() {
        String pdfFileExtension = "pdf";
        boolean isPdfSupported = SupportedFileTypes.isSupported(pdfFileExtension);
        assertThat(isPdfSupported).isTrue();
    }

    @Test
    public void anUnknownFileExtensionShouldNotBeASupportedFileType() {
        String unknownFileExtension = "UNKNOWN";
        boolean isUnknownSupported = SupportedFileTypes.isSupported(unknownFileExtension);
        assertThat(isUnknownSupported).isFalse();
    }

    @Test
    public void fileWithPdfExtensionShouldBeASupportedFile() {
        File pdf = new File("/providing/a.pdf");
        boolean isPdfFileSupported = SupportedFileTypes.isSupported(pdf);
        assertThat(isPdfFileSupported).isTrue();
    }

    @Test
    public void fileWithAnUnknownFileExtensionShouldBeAnUnsupportedFile() {
        File unknown = new File("/providing/a.unknown");
        boolean isUnknownFileSupported = SupportedFileTypes.isSupported(unknown);
        assertThat(isUnknownFileSupported).isFalse();
    }

    @Test
    public void fileWithoutExtensionShouldBeUnsupported() {
        File withoutExtension = new File("/providing/a");
        boolean isFileWithoutExtensionSupported = SupportedFileTypes.isSupported(withoutExtension);
        assertThat(isFileWithoutExtensionSupported).isFalse();
    }

}