package com.vnaskos.pdfguru.execution.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PagePatternTranslator {

    public static List<Integer> getSelectedIndicesFor(final String selectedPagesPattern, final int lastPage) {
        List<Integer> selectedPages = new ArrayList<>();

        String pagesPattern = selectedPagesPattern;

        if (pagesPattern.isEmpty()) {
            pagesPattern = "1-$";
        }

        pagesPattern = pagesPattern.replaceAll("\\$", String.valueOf(lastPage));

        for (String pipeSeparatedPattern : pagesPattern.split("\\|")) {
            for (String commaSeparatedPattern : pipeSeparatedPattern.split(",")) {
                if (commaSeparatedPattern.contains("-")) {
                    String[] limits = commaSeparatedPattern.split("-");
                    IntStream.rangeClosed(Integer.parseInt(limits[0]), Integer.parseInt(limits[1]))
                            .forEach(selectedPages::add);
                } else {
                    selectedPages.add(Integer.parseInt(commaSeparatedPattern));
                }
            }
        }

        return selectedPages;
    }

}
