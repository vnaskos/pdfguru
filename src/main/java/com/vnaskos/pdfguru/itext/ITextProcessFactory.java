package com.vnaskos.pdfguru.itext;

import com.vnaskos.pdfguru.Process;
import com.vnaskos.pdfguru.*;

import java.util.List;

public class ITextProcessFactory implements ProcessFactory {

    @Override
    public ProcessListener newProcess(List<InputItem> input,
                                      OutputParameters outputParameters,
                                      ExecutionProgressListener progressListener) {
        ITextDocumentManager documentManager = new ITextDocumentManager();
        ITextProcessOrchestrator processOrchestrator = new ITextProcessOrchestrator(
                documentManager, input, outputParameters);

        return new Process(processOrchestrator, progressListener);
    }
}
