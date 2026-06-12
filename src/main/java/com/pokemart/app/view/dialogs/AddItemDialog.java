package com.pokemart.app.view.dialogs;

import com.pokemart.app.controller.ItemAdder;
import com.pokemart.app.model.entity.Item;
import com.pokemart.app.util.PokeTheme;
import com.pokemart.app.view.componentes.PokeButton;
import com.pokemart.app.view.componentes.PokePanel;
import com.pokemart.app.view.componentes.PokeScrollBarUI;
import com.pokemart.app.view.componentes.PokeTable;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AddItemDialog extends JDialog {

    private JTextField txtBarcode;
    private JTextField txtSearch;
    private JSpinner   spnQuantity;
    private JLabel     lblPreviewImage;
    private JLabel     lblPreviewName;
    private JLabel     lblPreviewPrice;
    private JLabel     lblPreviewStock;

    private PokeTable  itemTable;
    private ItemCatalogTableModel catalogModel;

    private Item selectedItem = null;
    private final ItemAdder controller;
    private final List<Item> allItems;

    public AddItemDialog(Window owner, ItemAdder controller, List<Item> items) {
        super(owner, "ADICIONAR ITEM", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.allItems   = new ArrayList<>(items);
        setUndecorated(true);
        setResizable(false);
        initComponents();
        pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screen.width - getWidth()) / 2, (screen.height - getHeight()) / 2);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Gradiente azul escuro -> azul
                GradientPaint gp = new GradientPaint(
                        0, 0, PokeTheme.ACCENT_BLUE_DARK,
                        getWidth(), 0, PokeTheme.ACCENT_BLUE);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(PokeTheme.ACCENT_YELLOW);
                g2.fillRect(0, getHeight()-2, getWidth(), 2);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 38));

        JPanel headerLeft = new JPanel(new MigLayout("insets 0 10 0 0", "[][grow]", "[grow]"));
        headerLeft.setOpaque(false);
        JLabel lblPokeIcon = new JLabel("*");
        lblPokeIcon.setFont(PokeTheme.getPixelFont(10f));
        lblPokeIcon.setForeground(PokeTheme.ACCENT_YELLOW);
        JLabel lblTitle = new JLabel("ADICIONAR ITEM");
        lblTitle.setFont(PokeTheme.getPixelFont(8f));
        lblTitle.setForeground(PokeTheme.TEXT_PRIMARY);
        headerLeft.add(lblPokeIcon, "ay center");
        headerLeft.add(lblTitle, "ay center");
        header.add(headerLeft, BorderLayout.CENTER);

        JButton btnClose = new JButton("X") {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(getModel().isRollover() ? new Color(0xff5555) : PokeTheme.ACCENT_RED);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        btnClose.setFont(PokeTheme.getPixelFont(8f));
        btnClose.setForeground(PokeTheme.TEXT_PRIMARY);
        btnClose.setPreferredSize(new Dimension(36, 36));
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        header.add(btnClose, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new MigLayout(
                "wrap 1, insets 12, gapy 10",
                "[620!, fill]"
        ));
        body.setBackground(PokeTheme.BACKGROUND);

        PokePanel searchPanel = new PokePanel();
        searchPanel.setLayout(new MigLayout(
                "wrap 4, insets 10 12 12 12, gapy 8",
                "[grow, fill][grow, fill][80!, right]12[120!, fill]"
        ));

        JLabel lblSearch = new JLabel("BUSCAR ITEM");
        lblSearch.setFont(PokeTheme.getPixelFont(8f));
        lblSearch.setForeground(PokeTheme.ACCENT_YELLOW);
        searchPanel.add(lblSearch, "span 4, gapbottom 4");

        txtBarcode = new JTextField();
        PokeTheme.styleTextField(txtBarcode);
        txtSearch  = new JTextField();
        PokeTheme.styleTextField(txtSearch);

        spnQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        spnQuantity.setBorder(BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE, 1));
        JTextField spnEd = ((JSpinner.DefaultEditor) spnQuantity.getEditor()).getTextField();
        spnEd.setBackground(PokeTheme.PANEL_BG);
        spnEd.setForeground(PokeTheme.TEXT_PRIMARY);
        spnEd.setFont(new Font("Courier New", Font.BOLD, 11));
        spnEd.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));

        PokeButton btnAdd = new PokeButton("+ ADICIONAR");

        searchPanel.add(makeFieldCol("CÓD. BARRAS", txtBarcode), "growx");
        searchPanel.add(makeFieldCol("NOME / BUSCA", txtSearch),  "growx");
        searchPanel.add(lbl("QUANTIDADE:"),                       "ay bottom");
        searchPanel.add(spnQuantity,                              "h 28!, ay bottom");

        body.add(searchPanel, "growx");

        PokePanel tablePanel = new PokePanel();
        tablePanel.setLayout(new MigLayout("fill, insets 10 12 12 12", "[grow]", "[grow]"));

        JLabel lblCatalog = new JLabel("CATÁLOGO DE ITENS");
        lblCatalog.setFont(PokeTheme.getPixelFont(8f));
        lblCatalog.setForeground(PokeTheme.ACCENT_YELLOW);
        tablePanel.add(lblCatalog, "wrap, gapbottom 6");

        catalogModel = new ItemCatalogTableModel(allItems);
        itemTable = new PokeTable(catalogModel);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.setPreferredScrollableViewportSize(new Dimension(580, 200));

        itemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && itemTable.getSelectedRow() != -1) {
                selectedItem = catalogModel.getItemAt(itemTable.getSelectedRow());
                txtBarcode.setText(selectedItem.getBarcode());
                atualizarPreview(selectedItem);
            }
        });

        itemTable.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && selectedItem != null) addItem();
            }
        });

        JScrollPane scroll = new JScrollPane(itemTable);
        scroll.getViewport().setBackground(PokeTheme.PANEL_BG);
        scroll.setBorder(BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE));
        scroll.getVerticalScrollBar().setUI(new PokeScrollBarUI());
        scroll.getVerticalScrollBar().setBackground(PokeTheme.PANEL_BG);

        tablePanel.add(scroll, "grow, h 200!");
        body.add(tablePanel, "growx");

        JPanel previewPanel = new JPanel() {
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
        previewPanel.setOpaque(false);
        previewPanel.setLayout(new MigLayout(
                "insets 8 12 8 12",
                "[64!][grow][grow][grow]",
                "[grow]"
        ));

        lblPreviewImage = new JLabel();
        lblPreviewImage.setPreferredSize(new Dimension(60, 60));
        lblPreviewImage.setBorder(BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE));
        lblPreviewImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreviewImage.setBackground(new Color(0x2a2a4a));
        lblPreviewImage.setOpaque(true);
        previewPanel.add(lblPreviewImage, "w 60!, h 60!, ay center");

        JPanel c1 = makePreviewCol();
        lblPreviewName  = makePreviewValue("—");
        lblPreviewName.setFont(PokeTheme.getPixelFont(9f));
        lblPreviewName.setForeground(PokeTheme.ACCENT_YELLOW);
        c1.add(makePreviewKey("ITEM")); c1.add(lblPreviewName);
        previewPanel.add(c1, "grow, ay center");

        JPanel c2 = makePreviewCol();
        lblPreviewPrice = makePreviewValue("—");
        lblPreviewPrice.setForeground(PokeTheme.ACCENT_YELLOW);
        c2.add(makePreviewKey("PREÇO")); c2.add(lblPreviewPrice);
        previewPanel.add(c2, "grow, ay center");

        JPanel c3 = makePreviewCol();
        lblPreviewStock = makePreviewValue("—");
        c3.add(makePreviewKey("ESTOQUE")); c3.add(lblPreviewStock);
        previewPanel.add(c3, "grow, ay center");

        body.add(previewPanel, "growx, h 80!");

        PokeButton btnAddFinal = new PokeButton("+ ADICIONAR À VENDA");
        PokeButton btnFechar   = new PokeButton("FECHAR");
        btnAddFinal.addActionListener(e -> addItem());
        btnFechar.addActionListener(e -> dispose());

        JPanel actions = new JPanel(new MigLayout("insets 0", "[grow][120!]"));
        actions.setOpaque(false);
        actions.add(btnAddFinal, "growx, h 34!");
        actions.add(btnFechar,   "h 34!");

        body.add(actions, "growx");
        add(body, BorderLayout.CENTER);

        txtBarcode.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) buscarPorBarcode();
            }
        });
        txtBarcode.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (!txtBarcode.getText().isBlank()) buscarPorBarcode();
            }
        });

        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e)  { filtrar(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { filtrar(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filtrar(); }
        });

        getRootPane().setBorder(BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE, 2));
    }

    private void buscarPorBarcode() {
        String code = txtBarcode.getText().trim();
        if (code.isBlank()) return;
        allItems.stream()
                .filter(i -> i.getBarcode().equalsIgnoreCase(code))
                .findFirst()
                .ifPresent(item -> {
                    selectedItem = item;
                    atualizarPreview(item);
                    for (int r = 0; r < catalogModel.getRowCount(); r++) {
                        if (catalogModel.getItemAt(r).getId().equals(item.getId())) {
                            itemTable.setRowSelectionInterval(r, r);
                            itemTable.scrollRectToVisible(itemTable.getCellRect(r, 0, true));
                            break;
                        }
                    }
                });
    }

    private void filtrar() {
        String term = txtSearch.getText().trim().toLowerCase();
        List<Item> filtered = term.isBlank()
                ? allItems
                : allItems.stream()
                .filter(i -> i.getName().toLowerCase().contains(term)
                        || i.getBarcode().toLowerCase().contains(term)
                        || i.getCategory().toLowerCase().contains(term))
                .toList();
        catalogModel.setItems(filtered);
        selectedItem = null;
        limparPreview();
    }

    private void addItem() {
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this,
                    "Selecione um item na tabela ou busque pelo código de barras.",
                    "Atenção", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int qty = (Integer) spnQuantity.getValue();
        controller.onAddItem(selectedItem.getBarcode(), qty);
        spnQuantity.setValue(1);
        txtBarcode.requestFocusInWindow();
    }

    private void atualizarPreview(Item item) {
        lblPreviewName.setText(item.getName());
        lblPreviewPrice.setText("₽ " + item.getPrice().toPlainString());
        lblPreviewStock.setText(item.getStock() + " un.");

        if (item.getImagePath() != null && !item.getImagePath().isBlank()) {
            File f = new File("data/images/" + item.getImagePath());
            if (!f.exists()) f = new File(item.getImagePath());
            if (f.exists()) {
                Image img = new ImageIcon(f.getAbsolutePath())
                        .getImage().getScaledInstance(58, 58, Image.SCALE_SMOOTH);
                lblPreviewImage.setIcon(new ImageIcon(img));
                lblPreviewImage.setText("");
                return;
            }
        }
        lblPreviewImage.setIcon(null);
        lblPreviewImage.setText("?");
        lblPreviewImage.setForeground(PokeTheme.ACCENT_BLUE);
    }

    private void limparPreview() {
        lblPreviewName.setText("—");
        lblPreviewPrice.setText("—");
        lblPreviewStock.setText("—");
        lblPreviewImage.setIcon(null);
        lblPreviewImage.setText("");
    }

    private JPanel makeFieldCol(String labelText, JComponent field) {
        JPanel p = new JPanel(new MigLayout("wrap 1, insets 0, gapy 4", "[grow, fill]"));
        p.setOpaque(false);
        p.add(lbl(labelText));
        p.add(field, "h 28!, growx");
        return p;
    }

    private JPanel makePreviewCol() {
        JPanel p = new JPanel(new MigLayout("wrap 1, insets 0", "[grow]", "[]2[]"));
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
        l.setFont(PokeTheme.getPixelFont(9f));
        l.setForeground(PokeTheme.TEXT_PRIMARY);
        return l;
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        PokeTheme.styleLabel(l);
        return l;
    }

    private static class ItemCatalogTableModel extends AbstractTableModel {
        private List<Item> items;
        private static final String[] COLS = {"Código", "Nome", "Categoria", "Preço", "Estoque"};

        ItemCatalogTableModel(List<Item> items) { this.items = new ArrayList<>(items); }

        void setItems(List<Item> items) {
            this.items = new ArrayList<>(items);
            fireTableDataChanged();
        }

        Item getItemAt(int row) { return items.get(row); }

        @Override public int getRowCount()    { return items.size(); }
        @Override public int getColumnCount() { return COLS.length; }
        @Override public String getColumnName(int col) { return COLS[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            Item i = items.get(row);
            return switch (col) {
                case 0 -> i.getBarcode();
                case 1 -> i.getName();
                case 2 -> i.getCategory();
                case 3 -> "₽ " + i.getPrice().toPlainString();
                case 4 -> i.getStock();
                default -> null;
            };
        }
    }
}