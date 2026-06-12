package com.pokemart.app.view.componentes;

import com.pokemart.app.util.PokeTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

public class PokeTable extends JTable {

    public PokeTable(TableModel model) {
        super(model);
        setBackground(PokeTheme.PANEL_BG);
        setForeground(PokeTheme.TEXT_PRIMARY);
        setSelectionBackground(PokeTheme.ACCENT_YELLOW);
        setSelectionForeground(PokeTheme.BACKGROUND);
        setGridColor(PokeTheme.ACCENT_BLUE);
        setShowGrid(true);
        setRowHeight(32);
        setFont(PokeTheme.getPixelFont(8f));

        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setBackground(PokeTheme.ACCENT_BLUE);
                label.setForeground(PokeTheme.TEXT_PRIMARY);
                label.setFont(PokeTheme.getPixelFont(8f));
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createLineBorder(PokeTheme.BACKGROUND, 1));
                label.setOpaque(true);
                return label;
            }
        });

        getTableHeader().setPreferredSize(new Dimension(0, 36));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                if (isSelected) {
                    setBackground(PokeTheme.ACCENT_YELLOW);
                    setForeground(PokeTheme.BACKGROUND);
                } else if (row % 2 == 0) {
                    setBackground(PokeTheme.PANEL_BG);
                    setForeground(PokeTheme.TEXT_PRIMARY);
                } else {
                    setBackground(new Color(0x252540));
                    setForeground(PokeTheme.TEXT_PRIMARY);
                }
                return this;
            }
        };

        for (int x = 0; x < getColumnCount(); x++) {
            getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
        }
    }
}