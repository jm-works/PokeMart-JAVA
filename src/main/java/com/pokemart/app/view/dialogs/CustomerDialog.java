package com.pokemart.app.view.dialogs;

import com.pokemart.app.controller.SaleController;
import com.pokemart.app.util.PokeTheme;
import com.pokemart.app.view.componentes.PokeButton;
import com.pokemart.app.view.componentes.PokePanel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

/**
 * Dialog de dados do cliente: CPF (qualquer 11 dígitos), nome e pagamento.
 * Não valida dígitos verificadores — aceita qualquer sequência numérica de 11 chars.
 */
public class CustomerDialog extends JDialog {

    private JTextField txtCpf;
    private JTextField txtName;
    private String selectedPayment = "Dinheiro";
    private JButton btnPayment;

    private final SaleController controller;

    public CustomerDialog(Window owner, SaleController controller) {
        super(owner, "DADOS DO CLIENTE", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
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
        JLabel lblIcon = new JLabel("?");
        lblIcon.setFont(PokeTheme.getPixelFont(12f));
        lblIcon.setForeground(PokeTheme.ACCENT_YELLOW);
        JLabel lblTitle = new JLabel("DADOS DO CLIENTE");
        lblTitle.setFont(PokeTheme.getPixelFont(8f));
        lblTitle.setForeground(PokeTheme.TEXT_PRIMARY);
        headerLeft.add(lblIcon, "ay center");
        headerLeft.add(lblTitle, "ay center");
        header.add(headerLeft, BorderLayout.CENTER);

        JButton btnClose = new JButton("✕") {
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

        JPanel body = new JPanel(new MigLayout("wrap 1, insets 12, gapy 10", "[460!, fill]"));
        body.setBackground(PokeTheme.BACKGROUND);

        PokePanel formPanel = new PokePanel();
        formPanel.setLayout(new MigLayout(
                "wrap 2, insets 10 12 14 12",
                "[90!, right]12[grow, fill]",
                "[]10[]10[]10[]"
        ));

        JLabel lblHeader = new JLabel("CLIENTE");
        lblHeader.setFont(PokeTheme.getPixelFont(8f));
        lblHeader.setForeground(PokeTheme.ACCENT_YELLOW);
        formPanel.add(lblHeader, "span 2, gapbottom 6");

        txtCpf = new JTextField();
        PokeTheme.styleTextField(txtCpf);
        ((AbstractDocument) txtCpf.getDocument()).setDocumentFilter(new DocumentFilter() {

            private String currentDigits(FilterBypass fb) {
                try {
                    return fb.getDocument().getText(0, fb.getDocument().getLength())
                            .replaceAll("[^\\d]", "");
                } catch (BadLocationException e) { return ""; }
            }

            private String format(String digits) {
                if (digits.length() > 9)
                    return digits.substring(0,3) + "." + digits.substring(3,6) + "."
                            + digits.substring(6,9) + "-" + digits.substring(9);
                if (digits.length() > 6)
                    return digits.substring(0,3) + "." + digits.substring(3,6) + "."
                            + digits.substring(6);
                if (digits.length() > 3)
                    return digits.substring(0,3) + "." + digits.substring(3);
                return digits;
            }

            private void applyFormatted(FilterBypass fb, String newDigits) throws BadLocationException {
                if (newDigits.length() > 11) return;
                String formatted = format(newDigits);
                fb.replace(0, fb.getDocument().getLength(), formatted, null);
            }

            @Override
            public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr)
                    throws BadLocationException {
                if (text == null) return;
                String digits = (currentDigits(fb) + text.replaceAll("[^\\d]", ""));
                applyFormatted(fb, digits);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attr)
                    throws BadLocationException {
                if (text == null) text = "";
                String current = currentDigits(fb);
                String removed = fb.getDocument().getText(offset, length).replaceAll("\\D", "");
                int removeCount = removed.length();
                String kept = current.substring(0, Math.max(0, current.length() - removeCount));
                String digits = kept + text.replaceAll("[^\\d]", "");
                applyFormatted(fb, digits);
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length)
                    throws BadLocationException {
                String removed = fb.getDocument().getText(offset, length).replaceAll("\\D", "");
                String current = currentDigits(fb);
                String digits = current.length() >= removed.length()
                        ? current.substring(0, current.length() - removed.length())
                        : "";
                applyFormatted(fb, digits);
            }
        });

        txtName = new JTextField();
        PokeTheme.styleTextField(txtName);

        btnPayment = makePaymentButton();

        formPanel.add(lbl("CPF:"));        formPanel.add(txtCpf,       "h 28!, growx");
        formPanel.add(lbl("NOME:"));       formPanel.add(txtName,      "h 28!, growx");
        formPanel.add(lbl("PAGAMENTO:")); formPanel.add(btnPayment,   "h 28!, growx");

        PokeButton btnConfirmar = new PokeButton("✔ CONFIRMAR");
        PokeButton btnCancelar  = new PokeButton("CANCELAR");
        btnConfirmar.addActionListener(e -> confirmar());
        btnCancelar.addActionListener(e -> dispose());

        JPanel actions = new JPanel(new MigLayout("insets 0", "[grow][grow]"));
        actions.setOpaque(false);
        actions.add(btnConfirmar, "growx, h 34!");
        actions.add(btnCancelar,  "growx, h 34!");

        body.add(formPanel, "growx");
        body.add(actions,   "growx");

        add(body, BorderLayout.CENTER);
        getRootPane().setBorder(BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE, 2));
    }

    private void confirmar() {
        String cpf      = txtCpf.getText().replaceAll("[^\\d]", "");
        String name     = txtName.getText().trim();

        if (cpf.length() != 11) {
            JOptionPane.showMessageDialog(this, "CPF deve ter 11 dígitos.", "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome do cliente é obrigatório.", "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        controller.onComplete(cpf, name, selectedPayment, this);
    }

    private JButton makePaymentButton() {
        String[] options = {"Dinheiro", "Cartão Débito", "Cartão Crédito", "Pix"};

        JButton btn = new JButton(selectedPayment + "  v") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(PokeTheme.PANEL_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(PokeTheme.ACCENT_BLUE);
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Courier New", Font.BOLD, 11));
        btn.setForeground(PokeTheme.TEXT_PRIMARY);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(PokeTheme.PANEL_BG);
        popup.setBorder(BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE, 1));

        for (String option : options) {
            JMenuItem item = new JMenuItem(option) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(getModel().isArmed() ? PokeTheme.ACCENT_BLUE : PokeTheme.PANEL_BG);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            item.setFont(new Font("Courier New", Font.BOLD, 11));
            item.setForeground(PokeTheme.TEXT_PRIMARY);
            item.setBackground(PokeTheme.PANEL_BG);
            item.setOpaque(false);
            item.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            item.addActionListener(e -> {
                selectedPayment = option;
                btn.setText(option + "  v");
            });
            popup.add(item);
        }

        btn.addActionListener(e -> popup.show(btn, 0, btn.getHeight()));
        return btn;
    }

    private JLabel lbl(String text) {
        JLabel l = new JLabel(text);
        PokeTheme.styleLabel(l);
        return l;
    }
}