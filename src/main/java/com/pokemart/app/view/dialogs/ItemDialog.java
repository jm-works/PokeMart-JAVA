package com.pokemart.app.view.dialogs;

import com.pokemart.app.controller.ItemController;
import com.pokemart.app.model.entity.Item;
import com.pokemart.app.util.PokeTheme;
import com.pokemart.app.view.componentes.PokeButton;
import com.pokemart.app.view.componentes.PokePanel;
import lombok.Getter;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class ItemDialog extends JDialog {

    private JTextField txtBarcode;
    private JTextField txtName;
    private JTextField txtCategory;
    private JTextField txtPrice;
    private JTextField txtStock;
    private JLabel lblImagePreview;
    @Getter
    private String currentImagePath;

    private final ItemController controller;
    private final String itemId;

    public ItemDialog(Window owner, String title, Item itemToEdit, ItemController controller) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.itemId = (itemToEdit != null) ? itemToEdit.getId() : null;

        setUndecorated(true);
        setSize(460, 430);
        setLocationRelativeTo(owner);
        setResizable(false);

        initComponents(title);
        if (itemToEdit != null) popularCampos(itemToEdit);
    }

    private void initComponents(String title) {
        setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(PokeTheme.ACCENT_BLUE);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 32));

        JLabel lblTitle = new JLabel("  " + title);
        lblTitle.setFont(PokeTheme.getPixelFont(8f));
        lblTitle.setForeground(PokeTheme.TEXT_PRIMARY);
        header.add(lblTitle, BorderLayout.CENTER);

        JButton btnClose = new JButton("✕") {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getModel().isRollover()
                        ? new Color(0xff5555) : PokeTheme.ACCENT_RED);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        btnClose.setFont(PokeTheme.getPixelFont(8f));
        btnClose.setForeground(PokeTheme.TEXT_PRIMARY);
        btnClose.setPreferredSize(new Dimension(32, 32));
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        header.add(btnClose, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new MigLayout("fill, insets 15", "[grow]", "[grow]"));
        body.setBackground(PokeTheme.BACKGROUND);

        PokePanel formPanel = new PokePanel();
        formPanel.setLayout(new MigLayout("wrap 2", "[right][grow, fill]", "[]12[]"));

        txtBarcode = new JTextField(15);
        txtName = new JTextField(15);
        txtCategory = new JTextField(15);
        txtPrice = new JTextField(15);
        txtStock = new JTextField(15);

        PokeTheme.styleTextField(txtBarcode);
        PokeTheme.styleTextField(txtName);
        PokeTheme.styleTextField(txtCategory);
        PokeTheme.styleTextField(txtPrice);
        PokeTheme.styleTextField(txtStock);

        formPanel.add(lbl("COD. BARRAS:")); formPanel.add(txtBarcode);
        formPanel.add(lbl("NOME:"));        formPanel.add(txtName);
        formPanel.add(lbl("CATEGORIA:"));   formPanel.add(txtCategory);
        formPanel.add(lbl("PREÇO (₽):"));  formPanel.add(txtPrice);
        formPanel.add(lbl("ESTOQUE:"));     formPanel.add(txtStock);

        lblImagePreview = new JLabel();
        lblImagePreview.setPreferredSize(new Dimension(56, 56));
        lblImagePreview.setBorder(BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE));
        lblImagePreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblImagePreview.setBackground(new Color(0x2a2a4a));
        lblImagePreview.setOpaque(true);

        PokeButton btnImg = new PokeButton("SELECIONAR");
        btnImg.addActionListener(e -> selecionarImagem());

        formPanel.add(lbl("IMAGEM:"));
        formPanel.add(btnImg, "split 2, growx, h 28!");
        formPanel.add(lblImagePreview, "w 56!, h 56!");

        PokeButton btnConfirm = new PokeButton("CONFIRMAR");
        PokeButton btnCancel = new PokeButton("CANCELAR");
        btnConfirm.addActionListener(e -> {
            if (itemId == null) controller.onSave(this);
            else controller.onUpdate(itemId, this);
        });
        btnCancel.addActionListener(e -> dispose());

        JPanel actions = new JPanel(new MigLayout("insets 0", "[grow][grow]"));
        actions.setOpaque(false);
        actions.add(btnConfirm, "growx, h 32!");
        actions.add(btnCancel,  "growx, h 32!");

        formPanel.add(actions, "span 2, growx, gaptop 15");
        body.add(formPanel, "grow");
        add(body, BorderLayout.CENTER);

        getRootPane().setBorder(
                BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE, 2));
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        PokeTheme.styleLabel(l);
        return l;
    }

    private void popularCampos(Item item) {
        txtBarcode.setText(item.getBarcode());
        txtName.setText(item.getName());
        txtCategory.setText(item.getCategory());
        txtPrice.setText(item.getPrice().toPlainString());
        txtStock.setText(String.valueOf(item.getStock()));
        currentImagePath = item.getImagePath();
        if (currentImagePath != null && !currentImagePath.isBlank()) {
            File f = PokeTheme.resolveItemImage(currentImagePath);
            if (f != null) {
                Image img = new ImageIcon(f.getAbsolutePath())
                        .getImage().getScaledInstance(54, 54, Image.SCALE_SMOOTH);
                lblImagePreview.setIcon(new ImageIcon(img));
            }
        }
    }

    private void selecionarImagem() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Imagens (PNG, JPG)", "png", "jpg", "jpeg"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentImagePath = fc.getSelectedFile().getAbsolutePath();
            Image img = new ImageIcon(currentImagePath)
                    .getImage().getScaledInstance(54, 54, Image.SCALE_SMOOTH);
            lblImagePreview.setIcon(new ImageIcon(img));
        }
    }

    public String getTxtBarcode()     { return txtBarcode.getText(); }
    public String getTxtName()        { return txtName.getText(); }
    public String getTxtCategory()    { return txtCategory.getText(); }
    public String getTxtPrice()       { return txtPrice.getText(); }
    public String getTxtStock()       { return txtStock.getText(); }
}