package com.vnaskos.pdfguru.execute;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}