package com.vnaskos.pdfguru.ui;

import com.vnaskos.pdfguru.InputItem;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

class InputItemJList extends JList<InputItem> {

    private final DefaultListModel<InputItem> model = new DefaultListModel<>();

    InputItemJList() {
        this.setModel(model);
    }

    void addItem(InputItem item) {
        if (item == null) {
            return;
        }

        model.add(model.size(), item);
    }

    void moveSelectedUp() {
        int topIndex = 0;
        if (getMinSelectionIndex() == topIndex) {
            return;
        }

        int[] selection = getSelectedIndices();
        int[] newSelection = new int[selection.length];
        for (int i=0; i<selection.length; i++) {
            newSelection[i] = moveElement(selection[i], MoveDirection.UP);
        }

        setSelectedIndices(newSelection);
        updateUI();
    }

    void moveSelectedDown() {
        int bottomIndex = model.size()-1;
        if (getMaxSelectionIndex() == bottomIndex) {
            return;
        }

        int[] selection = getSelectedIndices();
        int[] newSelection = new int[selection.length];
        for (int i=selection.length-1; i>=0; i--) {
            newSelection[i] = moveElement(selection[i], MoveDirection.DOWN);
        }

        setSelectedIndices(newSelection);
        updateUI();
    }

    private int moveElement(int selectedIndex, MoveDirection direction) {
        int newIndex = selectedIndex + direction.value;

        InputItem tmp = model.get(selectedIndex);
        model.set(selectedIndex, model.get(newIndex));
        model.set(newIndex, tmp);

        return newIndex;
    }

    public void removeAll() {
        model.removeAllElements();
    }

    void removeSelected() {
        int[] selected = getSelectedIndices();
        for (int i = selected.length - 1; i >= 0; i--) {
            model.remove(selected[i]);
        }
    }

    List<InputItem> getItems() {
        return Collections.list(model.elements());
    }

    private enum MoveDirection {
        UP(-1), DOWN(1);

        final int value;

        MoveDirection(int value) {
            this.value = value;
        }
    }
}
