package com.vnaskos.pdfguru.input.items;

/**
 *
 * @author Vasilis Naskos
 */
public class InputItem {
    
    private final String path;
    private String pages;
    
    public InputItem(String path) {
        this.path = path;
        pages = "";
    }
    
    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getPath() {
        return path;
    }

    public String getPages() {
        return pages;
    }

    @Override
    public String toString() {
        return path;
    }
}
