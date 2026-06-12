package com.pokemart.app.view.dialogs;

import com.pokemart.app.model.entity.SaleHistoryEntry;
import com.pokemart.app.util.PokeTheme;
import com.pokemart.app.view.componentes.PokeButton;
import com.pokemart.app.view.componentes.PokeScrollBarUI;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.format.DateTimeFormatter;

public class ReceiptDialog extends JDialog {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final SaleHistoryEntry entry;
    private JTextArea txtReceipt;

    public ReceiptDialog(Window owner, SaleHistoryEntry entry) {
        super(owner, "NOTA FISCAL", ModalityType.APPLICATION_MODAL);
        this.entry = entry;
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
                g.setColor(PokeTheme.ACCENT_BLUE);
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        header.setOpaque(false);
        header.setPreferredSize(new Dimension(0, 36));

        JLabel lblTitle = new JLabel("  NOTA FISCAL");
        lblTitle.setFont(PokeTheme.getPixelFont(8f));
        lblTitle.setForeground(PokeTheme.TEXT_PRIMARY);
        header.add(lblTitle, BorderLayout.CENTER);

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

        JPanel body = new JPanel(new MigLayout("fill, insets 12, wrap 1", "[460!, fill]", "[grow][36!]"));
        body.setBackground(PokeTheme.BACKGROUND);

        txtReceipt = new JTextArea(buildReceipt());
        txtReceipt.setEditable(false);
        txtReceipt.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtReceipt.setBackground(new Color(0xf5f5f0));
        txtReceipt.setForeground(Color.BLACK);
        txtReceipt.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        txtReceipt.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(txtReceipt);
        scroll.setBorder(BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE));
        scroll.getVerticalScrollBar().setUI(new PokeScrollBarUI());
        scroll.getVerticalScrollBar().setBackground(PokeTheme.PANEL_BG);
        scroll.setPreferredSize(new Dimension(460, 460));

        body.add(scroll, "grow");

        PokeButton btnPrint = new PokeButton("IMPRIMIR");
        PokeButton btnFechar = new PokeButton("FECHAR");

        btnPrint.addActionListener(e -> imprimir());
        btnFechar.addActionListener(e -> dispose());

        JPanel actions = new JPanel(new MigLayout("insets 0", "[grow][grow]"));
        actions.setOpaque(false);
        actions.add(btnPrint,  "growx, h 34!");
        actions.add(btnFechar, "growx, h 34!");
        body.add(actions, "growx");

        add(body, BorderLayout.CENTER);
        getRootPane().setBorder(BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE, 2));
    }

    private String buildReceipt() {
        String line  = "============================================";
        String thin  = "--------------------------------------------";
        StringBuilder sb = new StringBuilder();

        sb.append(line).append("\n");
        sb.append(center("POKEMARKET", 44)).append("\n");
        sb.append(center("-- PALLET TOWN --", 44)).append("\n");
        sb.append(center("CNPJ: 03.372.437/0001-44", 44)).append("\n");
        sb.append(line).append("\n");
        sb.append("\n");

        String tipo = "SALE".equals(entry.getType()) ? "CUPOM FISCAL" : "REGISTRO DE REMOCAO";
        sb.append(center(tipo, 44)).append("\n");
        sb.append("\n");

        sb.append(thin).append("\n");
        sb.append(String.format("%-12s %s\n", "DATA:",
                entry.getDate() != null ? entry.getDate().format(FMT) : "—"));
        sb.append(String.format("%-12s %s\n", "CLIENTE:", entry.getCustomerName()));
        sb.append(String.format("%-12s %s\n", "CPF:", formatCpf(entry.getCustomerCpf())));
        if ("SALE".equals(entry.getType()))
            sb.append(String.format("%-12s %s\n", "PAGAMENTO:", entry.getPaymentMethod()));
        sb.append(thin).append("\n");
        sb.append("\n");

        sb.append(String.format("%-24s %5s  %10s\n", "ITEM", "QTD", "SUBTOTAL"));
        sb.append(thin).append("\n");

        if (entry.getItems() != null) {
            for (SaleHistoryEntry.SaleHistoryItem item : entry.getItems()) {
                String name = item.getItemName() != null ? item.getItemName() : "—";
                if (name.length() > 28) name = name.substring(0, 27) + ".";
                sb.append(name).append("\n");
                sb.append(String.format("  P %-10s x%-4d  P %s\n",
                        item.getUnitPrice().toPlainString(),
                        item.getQuantity(),
                        item.getSubtotal().toPlainString()));
            }
        }

        sb.append(thin).append("\n");
        sb.append(String.format("%-30s P %s\n", "TOTAL:", entry.getTotal().toPlainString()));
        sb.append(line).append("\n");
        sb.append("\n");
        sb.append(center("Obrigado pela preferencia!", 44)).append("\n");
        sb.append(center("Volte sempre, Treinador!", 44)).append("\n");
        sb.append("\n");
        sb.append(line).append("\n");

        return sb.toString();
    }

    private void imprimir() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Nota Fiscal - PokeMarket");
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            g2.setFont(new Font("Courier New", Font.PLAIN, 10));
            g2.setColor(Color.BLACK);
            String[] lines = buildReceipt().split("\n");
            int y = 14;
            for (String l : lines) {
                g2.drawString(l, 0, y);
                y += 14;
            }
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try { job.print(); }
            catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao imprimir: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static String center(String text, int width) {
        if (text.length() >= width) return text;
        int pad = (width - text.length()) / 2;
        return " ".repeat(pad) + text;
    }

    private static String formatCpf(String cpf) {
        if (cpf == null) return "—";
        String d = cpf.replaceAll("[^\\d]", "");
        if (d.length() == 11)
            return d.substring(0,3)+"."+d.substring(3,6)+"."+d.substring(6,9)+"-"+d.substring(9);
        return cpf;
    }
}