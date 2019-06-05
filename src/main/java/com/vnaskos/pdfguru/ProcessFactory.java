package com.vnaskos.pdfguru;

import com.vnaskos.pdfguru.processing.Process;

public class ProcessFactory {

    public static ProcessListener newProcess() {
        return new Process();
    }

}
