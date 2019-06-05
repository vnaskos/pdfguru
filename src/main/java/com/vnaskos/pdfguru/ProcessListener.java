package com.vnaskos.pdfguru;

import com.vnaskos.pdfguru.execution.OutputParameters;
import com.vnaskos.pdfguru.input.InputItem;

import java.util.List;

public interface ProcessListener {

    void run(List<InputItem> input,
             OutputParameters outputParameters,
             ExecutionProgressListener progressListener);

    void requestStop();

}
