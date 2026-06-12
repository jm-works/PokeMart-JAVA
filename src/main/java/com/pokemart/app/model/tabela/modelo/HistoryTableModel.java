package com.pokemart.app.model.tabela.modelo;

import com.pokemart.app.model.entity.SaleHistoryEntry;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryTableModel extends AbstractTableModel {

    private List<SaleHistoryEntry> entries;
    private static final String[] COLS = {"Data", "Tipo", "CPF", "Cliente", "Pagamento", "Total (₽)"};
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

    public HistoryTableModel() {
        this.entries = new ArrayList<>();
    }

    public void setEntries(List<SaleHistoryEntry> entries) {
        this.entries = new ArrayList<>(entries);
        fireTableDataChanged();
    }

    public SaleHistoryEntry getEntryAt(int row) {
        return (row >= 0 && row < entries.size()) ? entries.get(row) : null;
    }

    @Override public int getRowCount()    { return entries.size(); }
    @Override public int getColumnCount() { return COLS.length; }
    @Override public String getColumnName(int col) { return COLS[col]; }

    @Override
    public Object getValueAt(int row, int col) {
        SaleHistoryEntry e = entries.get(row);
        return switch (col) {
            case 0 -> e.getDate() != null ? e.getDate().format(FMT) : "—";
            case 1 -> e.getType();
            case 2 -> e.getCustomerCpf();
            case 3 -> e.getCustomerName();
            case 4 -> e.getPaymentMethod();
            case 5 -> "₽ " + (e.getTotal() != null ? e.getTotal().toPlainString() : "0");
            default -> null;
        };
    }
}
