package com.pokemart.app.view;

import com.pokemart.app.controller.ItemController;
import com.pokemart.app.model.entity.Item;
import com.pokemart.app.model.tabela.modelo.ItemTableModel;
import com.pokemart.app.util.PokeTheme;
import com.pokemart.app.view.componentes.PokeButton;
import com.pokemart.app.view.componentes.PokePanel;
import com.pokemart.app.view.componentes.PokeScrollBarUI;
import com.pokemart.app.view.componentes.PokeTable;
import com.pokemart.app.view.dialogs.ItemDialog;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class ItemForm extends JPanel {

    private PokeTable table;
    @Getter
    private ItemTableModel tableModel;
    @Getter
    private ItemController controller;

    private PokeButton btnEdit;
    private PokeButton btnDelete;

    private JLabel lblPreviewName;
    private JLabel lblPreviewBarcode;
    private JLabel lblPreviewCategory;
    private JLabel lblPreviewPrice;
    private JLabel lblPreviewStock;
    private JLabel lblPreviewImage;

    public ItemForm() {
        initComponents();
        this.controller = new ItemController(this);
        this.controller.onLoadAll();
    }

    private void initComponents() {
        setBackground(PokeTheme.BACKGROUND);
        setLayout(new MigLayout("fill, insets 12", "[grow]", "[grow][160!]"));

        PokePanel mainPanel = new PokePanel();
        mainPanel.setLayout(new MigLayout("fill, insets 10", "[grow]", "[44!][grow]"));

        JPanel header = new JPanel(new MigLayout("insets 0", "[][grow][]", "[grow]"));
        header.setOpaque(false);

        JPanel breadcrumb = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        breadcrumb.setOpaque(false);
        JLabel lblHome = makeHeaderLabel("HOME", PokeTheme.ACCENT_BLUE);
        JLabel lblArrow = makeHeaderLabel("›", PokeTheme.ACCENT_BLUE);
        JLabel lblSection = makeHeaderLabel("PRODUTOS", PokeTheme.TEXT_PRIMARY);
        breadcrumb.add(lblHome);
        breadcrumb.add(lblArrow);
        breadcrumb.add(lblSection);

        JLabel lblTitle = new JLabel("CATÁLOGO DE ITENS");
        lblTitle.setFont(PokeTheme.getPixelFont(10f));
        lblTitle.setForeground(PokeTheme.ACCENT_YELLOW);

        JPanel actionBar = new JPanel(new MigLayout("insets 0", "[][][]", "[]"));
        actionBar.setOpaque(false);

        PokeButton btnAdd = new PokeButton("+ ADICIONAR");
        btnEdit = new PokeButton("✎ EDITAR");
        btnDelete = new PokeButton("✕ EXCLUIR");
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        actionBar.add(btnAdd, "w 120!, h 32!");
        actionBar.add(btnEdit, "w 100!, h 32!");
        actionBar.add(btnDelete, "w 100!, h 32!");

        header.add(breadcrumb, "growy");
        header.add(lblTitle, "growx, al center");
        header.add(actionBar);

        mainPanel.add(header, "growx, wrap");

        tableModel = new ItemTableModel();
        table = new PokeTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean sel = table.getSelectedRow() != -1;
                btnEdit.setEnabled(sel);
                btnDelete.setEnabled(sel);
                if (sel) atualizarPreview(tableModel.getItemAt(table.getSelectedRow()));
                else limparPreview();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1)
                    abrirModalEdicao();
            }
        });

        btnAdd.addActionListener(e -> {
            ItemDialog d = new ItemDialog(
                    SwingUtilities.getWindowAncestor(this), "ADICIONAR ITEM", null, controller);
            d.setVisible(true);
        });
        btnEdit.addActionListener(e -> abrirModalEdicao());
        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row != -1) {
                Item item = tableModel.getItemAt(row);
                int ok = JOptionPane.showConfirmDialog(this,
                        "Excluir \"" + item.getName() + "\"?", "Atenção",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (ok == JOptionPane.YES_OPTION) controller.onDelete(item.getId());
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

        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint sky = new GradientPaint(
                        0, 0, new Color(0x0e1a30),
                        getWidth(), getHeight(), new Color(0x1a1040));
                g2.setPaint(sky);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(new Color(0x334466));
                int[][] stars = {{30,8},{80,20},{150,6},{240,14},{350,9},{450,18},
                        {520,5},{620,16},{700,11},{820,7},{900,20},{60,25}};
                for (int[] s : stars) {
                    if (s[0] < getWidth()) { g2.fillRect(s[0], s[1], 2, 2); }
                }

                int groundY = getHeight() - 28;
                GradientPaint ground = new GradientPaint(
                        0, groundY, new Color(0x2a4a2a),
                        0, getHeight(), new Color(0x1a2a1a));
                g2.setPaint(ground);
                g2.fillRect(0, groundY, getWidth(), getHeight() - groundY);

                g2.setColor(new Color(0x44aa44));
                g2.setStroke(new java.awt.BasicStroke(2f));
                g2.drawLine(0, groundY, getWidth(), groundY);

                g2.setColor(PokeTheme.ACCENT_BLUE);
                g2.fillRect(0, 0, getWidth(), 3);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        previewPanel.setOpaque(false);
        previewPanel.setLayout(new MigLayout(
                "insets 8 15 8 15, gap 20",
                "[92!][grow, fill][grow, fill][160!]",
                "[grow]"
        ));

        JPanel imgFrame = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x0a1020));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(PokeTheme.TEXT_PRIMARY);
                g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(PokeTheme.BACKGROUND);
                g2.drawRect(2, 2, getWidth()-5, getHeight()-5);
                g2.setColor(PokeTheme.ACCENT_BLUE);
                g2.drawRect(4, 4, getWidth()-9, getHeight()-9);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        imgFrame.setOpaque(false);
        imgFrame.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        lblPreviewImage = new JLabel();
        lblPreviewImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreviewImage.setVerticalAlignment(SwingConstants.CENTER);
        lblPreviewImage.setForeground(PokeTheme.ACCENT_BLUE);
        imgFrame.add(lblPreviewImage, BorderLayout.CENTER);
        previewPanel.add(imgFrame, "w 88!, h 88!, ay center");

        JPanel col1 = makePreviewCol();
        lblPreviewName = makePreviewValue("---");
        lblPreviewName.setFont(PokeTheme.getPixelFont(9f));
        lblPreviewName.setForeground(PokeTheme.ACCENT_YELLOW);
        lblPreviewBarcode = makePreviewValue("---");
        col1.add(makePreviewKey("NOME"));
        col1.add(lblPreviewName);
        col1.add(makePreviewKey("CODIGO"));
        col1.add(lblPreviewBarcode);
        previewPanel.add(col1, "grow, ay center");

        JPanel col2 = new JPanel(new MigLayout("wrap 1, insets 0", "[grow]", "[]2[]8[]2[]")) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
            }
        };
        col2.setOpaque(false);
        lblPreviewCategory = makePreviewValue("---");
        lblPreviewPrice    = makePreviewValue("---");
        lblPreviewPrice.setForeground(PokeTheme.ACCENT_YELLOW);
        col2.add(makePreviewKey("CATEGORIA"));
        col2.add(lblPreviewCategory);
        col2.add(makePreviewKey("PRECO"));
        col2.add(lblPreviewPrice);
        previewPanel.add(col2, "grow, ay center");

        JPanel hpPanel = new JPanel(new MigLayout("wrap 1, insets 6 8 6 8", "[grow]", "[]4[]")) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x0a1428));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(PokeTheme.TEXT_PRIMARY);
                g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(PokeTheme.BACKGROUND);
                g2.drawRect(1, 1, getWidth()-3, getHeight()-3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        hpPanel.setOpaque(false);

        JLabel lblHpTitle = new JLabel("ESTOQUE");
        lblHpTitle.setFont(PokeTheme.getPixelFont(7f));
        lblHpTitle.setForeground(PokeTheme.ACCENT_BLUE);

        lblPreviewStock = makePreviewValue("---");
        lblPreviewStock.setFont(PokeTheme.getPixelFont(9f));
        lblPreviewStock.setForeground(PokeTheme.ACCENT_GREEN);

        JPanel hpBar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x1a1a2e));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(0x333355));
                g2.drawRect(0, 0, getWidth()-1, getHeight()-1);

                String txt = lblPreviewStock.getText().replaceAll("[^0-9]", "");
                if (!txt.isBlank()) {
                    int stock = Integer.parseInt(txt);
                    float ratio = Math.min(1f, stock / 100f);
                    Color barColor = ratio > 0.5f ? PokeTheme.ACCENT_GREEN
                            : ratio > 0.2f ? PokeTheme.ACCENT_YELLOW
                            : PokeTheme.ACCENT_RED;
                    g2.setColor(barColor);
                    g2.fillRect(1, 1, (int)((getWidth()-2) * ratio), getHeight()-2);
                }
                g2.dispose();
            }
        };
        hpBar.setPreferredSize(new Dimension(0, 8));
        hpBar.setOpaque(false);

        hpPanel.add(lblHpTitle);
        hpPanel.add(lblPreviewStock);
        hpPanel.add(hpBar, "growx, h 8!");
        previewPanel.add(hpPanel, "w 180!, growy, ay center");

        add(previewPanel, "growx, h 140!");
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

    private void atualizarPreview(Item item) {
        if (item == null) { limparPreview(); return; }
        lblPreviewName.setText(item.getName());
        lblPreviewBarcode.setText(item.getBarcode());
        lblPreviewCategory.setText(item.getCategory());
        lblPreviewPrice.setText("₽ " + item.getPrice().toPlainString());
        lblPreviewStock.setText(item.getStock() + " un.");
        if (lblPreviewStock.getParent() != null) lblPreviewStock.getParent().repaint();

        if (item.getImagePath() != null && !item.getImagePath().isBlank()) {
            File f = resolverImagem(item.getImagePath());
            if (f != null) {
                Image img = new ImageIcon(f.getAbsolutePath())
                        .getImage().getScaledInstance(74, 74, Image.SCALE_SMOOTH);
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
        lblPreviewName.setText("---");
        lblPreviewBarcode.setText("---");
        lblPreviewCategory.setText("---");
        lblPreviewPrice.setText("---");
        lblPreviewStock.setText("---");
        if (lblPreviewStock.getParent() != null) lblPreviewStock.getParent().repaint();
        lblPreviewImage.setIcon(null);
        lblPreviewImage.setText("");
    }

    private File resolverImagem(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) return null;
        File f = new File("data/images/" + imagePath);
        if (f.exists()) return f;
        f = new File(imagePath);
        if (f.exists()) return f;
        return null;
    }

    private void abrirModalEdicao() {
        int row = table.getSelectedRow();
        if (row == -1) return;
        Item item = tableModel.getItemAt(row);
        new ItemDialog(SwingUtilities.getWindowAncestor(this),
                "EDITAR ITEM", item, controller).setVisible(true);
    }

}