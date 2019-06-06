package com.vnaskos.pdfguru;

import java.io.IOException;

/**
 *
 * @author Vasilis Naskos
 */
public interface ProcessOrchestrator {
    void startProcess(ProcessEventListener processEventListener) throws IOException;
}
