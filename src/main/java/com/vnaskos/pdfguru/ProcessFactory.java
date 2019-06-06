package com.vnaskos.pdfguru;

import java.util.List;

public interface ProcessFactory {

    ProcessListener newProcess(List<InputItem> input,
                               OutputParameters outputParameters,
                               ExecutionProgressListener progressListener);

}
