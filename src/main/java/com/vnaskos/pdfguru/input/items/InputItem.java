package com.vnaskos.pdfguru.input.items;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 *
 * @author Vasilis Naskos
 */
public class InputItem {
    
    private final String path;
    private String pagesPattern;
    
    public InputItem(String path) {
        this.path = path;
        pagesPattern = "";
    }

    public String getPath() {
        return path;
    }

    public boolean isPdf() {
        String file = this.getPath().toLowerCase();

        return file.endsWith(".pdf");
    }

    public void setPagesPattern(String pagesPattern) {
        this.pagesPattern = pagesPattern;
    }

    public String getPagesPattern() {
        return pagesPattern;
    }

    public List<Integer> getSelectedPages(final int lastPage) {
        List<Integer> selectedPages = new ArrayList<>();

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

    @Override
    public String toString() {
        return path;
    }
}
