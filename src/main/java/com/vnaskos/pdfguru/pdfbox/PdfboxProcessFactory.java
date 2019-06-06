package com.vnaskos.pdfguru.pdfbox;

import com.vnaskos.pdfguru.*;
import com.vnaskos.pdfguru.Process;

import java.util.List;

public class PdfboxProcessFactory implements ProcessFactory {

    @Override
    public ProcessListener newProcess(List<InputItem> input,
                                      OutputParameters outputParameters,
                                      ExecutionProgressListener progressListener) {
        PdfboxDocumentManager documentManager = new PdfboxDocumentManager();
        PdfboxProcessOrchestrator processOrchestrator = new PdfboxProcessOrchestrator(
                documentManager, input, outputParameters);

        return new Process(processOrchestrator, progressListener);
    }
}
