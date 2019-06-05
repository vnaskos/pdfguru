package com.vnaskos.pdfguru;

import java.util.List;

public interface ProcessListener {

    void run(List<InputItem> input,
             OutputParameters outputParameters,
             ExecutionProgressListener progressListener);

    void requestStop();

}
