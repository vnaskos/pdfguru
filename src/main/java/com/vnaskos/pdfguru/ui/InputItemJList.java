package com.vnaskos.pdfguru.ui;

import com.vnaskos.pdfguru.input.items.InputItem;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class InputItemJList extends JList<InputItem> {

    private final DefaultListModel<InputItem> model = new DefaultListModel<>();

    public InputItemJList() {
        this.setModel(model);
    }

    public void addItem(InputItem item) {
        if (item == null) {
            return;
        }

        model.add(model.size(), item);
    }

    public void moveSelectedUp() {
        int[] selected = getSelectedIndices();
        for (int i = 0; i < selected.length; i++) {
            moveElement(selected[i], MoveDirection.UP);
            selected[i]--;
        }
        setSelectedIndices(selected);
    }

    public void moveSelectedDown() {
        int[] selected = getSelectedIndices();
        for (int i = selected.length - 1; i >= 0; i--) {
            moveElement(selected[i], MoveDirection.DOWN);
            selected[i]++;
        }
        setSelectedIndices(selected);
    }

    private void moveElement(int indexOfSelected, MoveDirection direction) {
        swapElements(indexOfSelected, indexOfSelected + direction.value);
        indexOfSelected = indexOfSelected + direction.value;
        setSelectedIndex(indexOfSelected);
        updateUI();
    }

    private void swapElements(int pos1, int pos2) {
        InputItem tmp = model.get(pos1);
        model.set(pos1, model.get(pos2));
        model.set(pos2, tmp);
    }

    public void removeAll() {
        model.removeAllElements();
    }

    public void removeSelected() {
        int[] selected = getSelectedIndices();
        for (int i = selected.length - 1; i >= 0; i--) {
            model.remove(selected[i]);
        }
    }

    public List<InputItem> getItems() {
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
