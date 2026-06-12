package com.pokemart.app.model.tabela.modelo;

import com.pokemart.app.model.entity.Item;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ItemTableModel extends AbstractTableModel {

    private List<Item> items;
    private final String[] columns = {"Código", "Nome", "Categoria", "Preço", "Estoque"};

    public ItemTableModel() {
        this.items = new ArrayList<>();
    }

    public void setItems(List<Item> items) {
        this.items = items;
        fireTableDataChanged();
    }

    public Item getItemAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < items.size()) {
            return items.get(rowIndex);
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return items.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int column) {
        return columns[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Item item = items.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> item.getBarcode();
            case 1 -> item.getName();
            case 2 -> item.getCategory();
            case 3 -> item.getPrice();
            case 4 -> item.getStock();
            default -> null;
        };
    }
}