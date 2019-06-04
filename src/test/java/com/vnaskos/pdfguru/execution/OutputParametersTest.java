package com.vnaskos.pdfguru.execution;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class OutputParametersTest {

    private static final String AN_OUTPUT_FILEPATH = "/somewhere/over/the/rainbow";

    @Test
    public void providingACompressionShouldSetTheCompressionValue() {
        float aRandomValidValue = 0.8f;

        OutputParameters parameters = new OutputParameters(AN_OUTPUT_FILEPATH);
        parameters.setCompression(aRandomValidValue);

        assertThat(parameters.getCompression()).isEqualTo(aRandomValidValue);
    }

    @Test
    public void providingNegativeValueShouldSetTheCompressionToMaximumCompression() {
        float aNegativeValue = -10.0f;

        OutputParameters parameters = new OutputParameters(AN_OUTPUT_FILEPATH);
        parameters.setCompression(aNegativeValue);

        assertThat(parameters.getCompression()).isEqualTo(OutputParameters.COMPRESSED_MAX);
    }

    @Test
    public void providingAValueLargerThanTheUpperLimitShouldSetTheCompressionToMinimumCompression() {
        float aValueAboveOne = 10.0f;

        OutputParameters parameters = new OutputParameters(AN_OUTPUT_FILEPATH);
        parameters.setCompression(aValueAboveOne);

        assertThat(parameters.getCompression()).isEqualTo(OutputParameters.UNCOMPRESSED_MAX);
    }

    @Test
    public void createOutputNameCombiningOutputPathIndexAndExtension() {
        OutputParameters outputParameters = new OutputParameters(AN_OUTPUT_FILEPATH);

        String outputFileName = outputParameters.getUniqueOutputFileName();

        assertThat(outputFileName).isEqualTo("/somewhere/over/the/rainbow_1.pdf");
    }

    @Test
    public void createOutputNameWhenOutputPathContainsExtensionMaintainExtensionLast() {
        OutputParameters outputParameters = new OutputParameters(AN_OUTPUT_FILEPATH + ".pdf");

        String outputFileName = outputParameters.getUniqueOutputFileName();

        assertThat(outputFileName).isEqualTo("/somewhere/over/the/rainbow_1.pdf");
    }

    @Test
    public void createUniqueOutputNameWhenFilesWithTheFirstTwoIndicesAlreadyExists() {
        OutputParameters outputParameters = spy(new OutputParameters(AN_OUTPUT_FILEPATH));

        doReturn(true).when(outputParameters).fileExists("/somewhere/over/the/rainbow_1.pdf");
        doReturn(true).when(outputParameters).fileExists("/somewhere/over/the/rainbow_2.pdf");

        String outputFileName = outputParameters.getUniqueOutputFileName();

        assertThat(outputFileName).isEqualTo("/somewhere/over/the/rainbow_3.pdf");
    }
}