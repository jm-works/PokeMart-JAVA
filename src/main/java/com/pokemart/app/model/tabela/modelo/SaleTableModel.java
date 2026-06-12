package com.pokemart.app.model.tabela.modelo;

import com.pokemart.app.model.entity.Item;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SaleTableModel extends AbstractTableModel {

    public static class SaleRow {
        public final Item item;
        public int quantity;
        public BigDecimal subtotal;

        public SaleRow(Item item, int quantity) {
            this.item = item;
            this.quantity = quantity;
            this.subtotal = item.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }

    private final List<SaleRow> rows;
    private static final String[] COLUMNS = {"Código", "Nome", "Qtd", "Subtotal (₽)"};

    public SaleTableModel() {
        this.rows = new ArrayList<>();
    }

    public void addRow(Item item, int quantity) {
        for (SaleRow row : rows) {
            if (row.item.getBarcode().equals(item.getBarcode())) {
                row.quantity += quantity;
                row.subtotal = item.getPrice().multiply(BigDecimal.valueOf(row.quantity));
                fireTableDataChanged();
                return;
            }
        }
        rows.add(new SaleRow(item, quantity));
        fireTableRowsInserted(rows.size() - 1, rows.size() - 1);
    }

    public void removeRow(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < rows.size()) {
            rows.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    public void clear() {
        rows.clear();
        fireTableDataChanged();
    }

    public SaleRow getRowAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < rows.size()) return rows.get(rowIndex);
        return null;
    }

    public List<SaleRow> getRows() {
        return new ArrayList<>(rows);
    }

    public BigDecimal getTotal() {
        return rows.stream()
                .map(r -> r.subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override public int getRowCount()    { return rows.size(); }
    @Override public int getColumnCount() { return COLUMNS.length; }
    @Override public String getColumnName(int col) { return COLUMNS[col]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SaleRow row = rows.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> row.item.getBarcode();
            case 1 -> row.item.getName();
            case 2 -> row.quantity;
            case 3 -> "₽ " + row.subtotal.toPlainString();
            default -> null;
        };
    }
}
