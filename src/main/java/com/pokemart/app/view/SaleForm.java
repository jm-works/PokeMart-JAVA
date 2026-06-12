package com.pokemart.app.view;

import com.pokemart.app.controller.SaleController;
import com.pokemart.app.model.entity.Item;
import com.pokemart.app.model.tabela.modelo.SaleTableModel;
import com.pokemart.app.util.PokeTheme;
import com.pokemart.app.view.componentes.PokeButton;
import com.pokemart.app.view.componentes.PokePanel;
import com.pokemart.app.view.componentes.PokeScrollBarUI;
import com.pokemart.app.view.componentes.PokeTable;

import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class SaleForm extends JPanel {

    @Getter
    private SaleTableModel tableModel;
    private final SaleController controller;

    private PokeTable table;
    private PokeButton btnRemove;

    private JLabel lblTotal;
    private JLabel lblPreviewImage;
    private JLabel lblPreviewName;
    private JLabel lblPreviewPrice;
    private JLabel lblPreviewStock;

    public SaleForm(MainForm mainForm) {
        initComponents();
        this.controller = new SaleController(this, mainForm);
    }

    private void initComponents() {
        setBackground(PokeTheme.BACKGROUND);
        setLayout(new MigLayout("fill, insets 12", "[grow]", "[grow][100!]"));

        PokePanel mainPanel = new PokePanel();
        mainPanel.setLayout(new MigLayout("fill, insets 10", "[grow]", "[44!][grow]"));

        JPanel header = new JPanel(new MigLayout("insets 0", "[][grow][]", "[grow]"));
        header.setOpaque(false);

        JPanel breadcrumb = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        breadcrumb.setOpaque(false);
        breadcrumb.add(makeHeaderLabel("HOME", PokeTheme.ACCENT_BLUE));
        breadcrumb.add(makeHeaderLabel("›", PokeTheme.ACCENT_BLUE));
        breadcrumb.add(makeHeaderLabel("VENDAS", PokeTheme.TEXT_PRIMARY));

        JLabel lblTitle = new JLabel("PDV — POKEMART");
        lblTitle.setFont(PokeTheme.getPixelFont(10f));
        lblTitle.setForeground(PokeTheme.ACCENT_YELLOW);

        JPanel actionBar = new JPanel(new MigLayout("insets 0", "[][][][]", "[]"));
        actionBar.setOpaque(false);

        PokeButton btnNova    = new PokeButton("+ NOVA VENDA");
        btnRemove             = new PokeButton("✕ REMOVER");
        PokeButton btnClear   = new PokeButton("↺ LIMPAR");
        PokeButton btnConcluir = new PokeButton("✔ CONCLUIR");

        btnRemove.setEnabled(false);

        actionBar.add(btnNova,     "w 130!, h 32!");
        actionBar.add(btnRemove,   "w 110!, h 32!");
        actionBar.add(btnClear,    "w 100!, h 32!");
        actionBar.add(btnConcluir, "w 120!, h 32!");

        header.add(breadcrumb, "growy");
        header.add(lblTitle,   "growx, al center");
        header.add(actionBar);

        mainPanel.add(header, "growx, wrap");

        tableModel = new SaleTableModel();
        table = new PokeTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean sel = table.getSelectedRow() != -1;
                btnRemove.setEnabled(sel);
                if (sel) {
                    SaleTableModel.SaleRow row = tableModel.getRowAt(table.getSelectedRow());
                    if (row != null) atualizarPreview(row.item);
                } else {
                    limparPreview();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(PokeTheme.PANEL_BG);
        scroll.setBorder(BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE));
        scroll.getVerticalScrollBar().setUI(new PokeScrollBarUI());
        scroll.getVerticalScrollBar().setBackground(PokeTheme.PANEL_BG);
        scroll.getHorizontalScrollBar().setUI(new PokeScrollBarUI());

        mainPanel.add(scroll, "grow");
        add(mainPanel, "grow, wrap");

        JPanel bottomPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x1e1e38));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(PokeTheme.ACCENT_BLUE);
                g2.fillRect(0, 0, getWidth(), 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new MigLayout(
                "insets 10 15 10 15",
                "[80!][grow][grow][180!]",
                "[grow]"
        ));

        lblPreviewImage = new JLabel();
        lblPreviewImage.setPreferredSize(new Dimension(70, 70));
        lblPreviewImage.setBorder(BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE));
        lblPreviewImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreviewImage.setBackground(new Color(0x2a2a4a));
        lblPreviewImage.setOpaque(true);
        bottomPanel.add(lblPreviewImage, "w 70!, h 70!, ay center");

        JPanel colInfo = makePreviewCol();
        lblPreviewName  = makePreviewValue("—");
        lblPreviewName.setFont(PokeTheme.getPixelFont(11f));
        lblPreviewName.setForeground(PokeTheme.ACCENT_YELLOW);
        lblPreviewPrice = makePreviewValue("—");
        lblPreviewPrice.setFont(PokeTheme.getPixelFont(11f));
        lblPreviewPrice.setForeground(PokeTheme.ACCENT_YELLOW);
        colInfo.add(makePreviewKey("ITEM"));
        colInfo.add(lblPreviewName);
        colInfo.add(makePreviewKey("PREÇO UNIT."));
        colInfo.add(lblPreviewPrice);
        bottomPanel.add(colInfo, "grow, ay center");

        JPanel colStock = makePreviewCol();
        lblPreviewStock = makePreviewValue("—");
        colStock.add(makePreviewKey("ESTOQUE"));
        colStock.add(lblPreviewStock);
        bottomPanel.add(colStock, "grow, ay center");

        JPanel totalPanel = new JPanel(new MigLayout("wrap 1, insets 0", "[grow, fill]"));
        totalPanel.setOpaque(false);
        JLabel lblTotalKey = new JLabel("TOTAL DA VENDA");
        lblTotalKey.setFont(PokeTheme.getPixelFont(7f));
        lblTotalKey.setForeground(PokeTheme.ACCENT_BLUE);
        lblTotal = new JLabel("₽ 0.00");
        lblTotal.setFont(PokeTheme.getPixelFont(16f));
        lblTotal.setForeground(PokeTheme.ACCENT_YELLOW);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        totalPanel.add(lblTotalKey);
        totalPanel.add(lblTotal, "growx");
        bottomPanel.add(totalPanel, "w 180!, ay center");

        add(bottomPanel, "growx, h 100!");

        btnNova.addActionListener(e -> controller.onOpenAddItemDialog());

        btnRemove.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) controller.onRemoveItem(row);
        });

        btnClear.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                    "Limpar a venda atual?", "Atenção",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok == JOptionPane.YES_OPTION) controller.onClear();
        });

        btnConcluir.addActionListener(e -> controller.onOpenCustomerDialog());
    }

    public void atualizarTotal() {
        lblTotal.setText("₽ " + tableModel.getTotal().toPlainString());
    }

    public void limparCampos() { /* campos agora no dialog */ }

    public void limparCampoBarcode() { /* barcode agora no dialog */ }

    public void atualizarPreview(Item item) {
        if (item == null) { limparPreview(); return; }
        lblPreviewName.setText(item.getName());
        lblPreviewPrice.setText("₽ " + item.getPrice().toPlainString());
        lblPreviewStock.setText(item.getStock() + " un.");

        if (item.getImagePath() != null && !item.getImagePath().isBlank()) {
            File f = PokeTheme.resolveItemImage(item.getImagePath());
            if (f != null) {
                Image img = new ImageIcon(f.getAbsolutePath())
                        .getImage().getScaledInstance(68, 68, Image.SCALE_SMOOTH);
                lblPreviewImage.setIcon(new ImageIcon(img));
                lblPreviewImage.setText("");
                return;
            }
        }
        lblPreviewImage.setIcon(null);
        lblPreviewImage.setText("?");
        lblPreviewImage.setForeground(PokeTheme.ACCENT_BLUE);
    }

    public void limparPreview() {
        lblPreviewName.setText("—");
        lblPreviewPrice.setText("—");
        lblPreviewStock.setText("—");
        lblPreviewImage.setIcon(null);
        lblPreviewImage.setText("");
    }

    private JLabel makeHeaderLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(PokeTheme.getPixelFont(7f));
        l.setForeground(color);
        return l;
    }

    private JPanel makePreviewCol() {
        JPanel p = new JPanel(new MigLayout("wrap 1, insets 0", "[grow]", "[]2[]8[]2[]"));
        p.setOpaque(false);
        return p;
    }

    private JLabel makePreviewKey(String text) {
        JLabel l = new JLabel(text);
        l.setFont(PokeTheme.getPixelFont(7f));
        l.setForeground(PokeTheme.ACCENT_BLUE);
        return l;
    }

    private JLabel makePreviewValue(String text) {
        JLabel l = new JLabel(text);
        l.setFont(PokeTheme.getPixelFont(10f));
        l.setForeground(PokeTheme.TEXT_PRIMARY);
        return l;
    }
}