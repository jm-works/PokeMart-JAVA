package com.pokemart.app.view;

import com.pokemart.app.model.entity.SaleHistoryEntry;
import com.pokemart.app.model.repository.impl.ItemRepository;
import com.pokemart.app.model.repository.impl.SaleRepository;
import com.pokemart.app.model.service.SaleService;
import com.pokemart.app.model.tabela.modelo.HistoryTableModel;
import com.pokemart.app.util.PokeTheme;
import com.pokemart.app.view.componentes.PokeButton;
import com.pokemart.app.view.componentes.PokePanel;
import com.pokemart.app.view.componentes.PokeScrollBarUI;
import com.pokemart.app.view.componentes.PokeTable;
import com.pokemart.app.view.dialogs.ReceiptDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryForm extends JPanel {

    private final SaleService saleService;
    private HistoryTableModel tableModel;
    private PokeTable table;

    private JTextField txtSearch;
    private PokeButton btnDelete;
    private PokeButton btnRefresh;

    private JLabel lblDetailDate;
    private JLabel lblDetailType;
    private JLabel lblDetailCpf;
    private JLabel lblDetailName;
    private JLabel lblDetailPayment;
    private JLabel lblDetailTotal;
    private JTextArea txtDetailItems;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public HistoryForm() {
        this.saleService = new SaleService(new ItemRepository(), new SaleRepository());
        initComponents();
        loadHistory(null);
    }

    private void initComponents() {
        setBackground(PokeTheme.BACKGROUND);
        setLayout(new MigLayout("fill, insets 12", "[grow][320!]", "[grow]"));

        PokePanel mainPanel = new PokePanel();
        mainPanel.setLayout(new MigLayout("fill, insets 10", "[grow]", "[44!][36!][grow]"));

        JPanel header = new JPanel(new MigLayout("insets 0", "[][grow][]", "[grow]"));
        header.setOpaque(false);

        JPanel breadcrumb = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        breadcrumb.setOpaque(false);
        breadcrumb.add(makeHeaderLabel("HOME", PokeTheme.ACCENT_BLUE));
        breadcrumb.add(makeHeaderLabel("›", PokeTheme.ACCENT_BLUE));
        breadcrumb.add(makeHeaderLabel("HISTÓRICO", PokeTheme.TEXT_PRIMARY));

        JLabel lblTitle = new JLabel("HISTÓRICO DE VENDAS");
        lblTitle.setFont(PokeTheme.getPixelFont(10f));
        lblTitle.setForeground(PokeTheme.ACCENT_YELLOW);

        JPanel actionBar = new JPanel(new MigLayout("insets 0", "[][]", "[]"));
        actionBar.setOpaque(false);

        btnDelete  = new PokeButton("✕ APAGAR");
        btnRefresh = new PokeButton("↺ ATUALIZAR");
        btnDelete.setEnabled(false);

        actionBar.add(btnDelete,  "w 100!, h 32!");
        actionBar.add(btnRefresh, "w 120!, h 32!");

        header.add(breadcrumb, "growy");
        header.add(lblTitle,   "growx, al center");
        header.add(actionBar);
        mainPanel.add(header, "growx, wrap");

        JPanel searchBar = new JPanel(new MigLayout("insets 0", "[80!][grow]", "[grow]"));
        searchBar.setOpaque(false);
        JLabel lblSearch = new JLabel("BUSCAR CPF:");
        lblSearch.setFont(PokeTheme.getPixelFont(7f));
        lblSearch.setForeground(PokeTheme.ACCENT_BLUE);
        txtSearch = new JTextField();
        PokeTheme.styleTextField(txtSearch);
        searchBar.add(lblSearch);
        searchBar.add(txtSearch, "growx, h 28!");
        mainPanel.add(searchBar, "growx, wrap");

        tableModel = new HistoryTableModel();
        table = new PokeTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean sel = table.getSelectedRow() != -1;
                btnDelete.setEnabled(sel);
                if (sel) mostrarDetalhe(tableModel.getEntryAt(table.getSelectedRow()));
                else limparDetalhe();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    SaleHistoryEntry entry = tableModel.getEntryAt(table.getSelectedRow());
                    new ReceiptDialog(SwingUtilities.getWindowAncestor(HistoryForm.this), entry)
                            .setVisible(true);
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
        add(mainPanel, "grow");

        PokePanel detailPanel = new PokePanel();
        detailPanel.setLayout(new MigLayout(
                "wrap 1, fill, insets 10 12 12 12",
                "[grow, fill]",
                "[][grow]"
        ));

        JLabel lblDetTitle = new JLabel("DETALHE");
        lblDetTitle.setFont(PokeTheme.getPixelFont(8f));
        lblDetTitle.setForeground(PokeTheme.ACCENT_YELLOW);
        detailPanel.add(lblDetTitle, "gapbottom 10");

        JPanel fields = new JPanel(new MigLayout(
                "wrap 2, fillx",
                "[70!, right]8[grow, fill]",
                "[]6[]6[]6[]6[]6[]12[]"
        ));
        fields.setOpaque(false);

        lblDetailDate    = makeDetailValue("—");
        lblDetailType    = makeDetailValue("—");
        lblDetailCpf     = makeDetailValue("—");
        lblDetailName    = makeDetailValue("—");
        lblDetailPayment = makeDetailValue("—");
        lblDetailTotal   = makeDetailValue("—");
        lblDetailTotal.setFont(PokeTheme.getPixelFont(11f));
        lblDetailTotal.setForeground(PokeTheme.ACCENT_YELLOW);

        fields.add(makeDetailKey("DATA:"));     fields.add(lblDetailDate);
        fields.add(makeDetailKey("TIPO:"));     fields.add(lblDetailType);
        fields.add(makeDetailKey("CPF:"));      fields.add(lblDetailCpf);
        fields.add(makeDetailKey("NOME:"));     fields.add(lblDetailName);
        fields.add(makeDetailKey("PGTO:"));     fields.add(lblDetailPayment);
        fields.add(makeDetailKey("TOTAL:"));    fields.add(lblDetailTotal);

        JLabel lblItens = makeDetailKey("ITENS:");
        fields.add(lblItens, "span 2, gaptop 4");

        txtDetailItems = new JTextArea();
        txtDetailItems.setEditable(false);
        txtDetailItems.setFont(new Font("Courier New", Font.PLAIN, 10));
        txtDetailItems.setBackground(new Color(0x1a1a2e));
        txtDetailItems.setForeground(PokeTheme.TEXT_PRIMARY);
        txtDetailItems.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        txtDetailItems.setLineWrap(true);
        txtDetailItems.setWrapStyleWord(false);

        JScrollPane detailScroll = new JScrollPane(txtDetailItems);
        detailScroll.setBorder(BorderFactory.createLineBorder(PokeTheme.ACCENT_BLUE));
        detailScroll.getVerticalScrollBar().setUI(new PokeScrollBarUI());
        detailScroll.getVerticalScrollBar().setBackground(PokeTheme.PANEL_BG);

        detailPanel.add(fields, "growx");
        detailPanel.add(detailScroll, "grow");

        add(detailPanel, "growy, w 320!");

        btnRefresh.addActionListener(e -> loadHistory(txtSearch.getText().trim()));

        btnDelete.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) return;
            SaleHistoryEntry entry = tableModel.getEntryAt(row);
            String tipo = "SALE".equals(entry.getType()) ? "venda e do sales.json" : "remoção";
            int ok = JOptionPane.showConfirmDialog(this,
                    "Apagar este registro de " + tipo + "?\nEsta ação não pode ser desfeita.",
                    "Confirmar exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok != JOptionPane.YES_OPTION) return;

            try {
                saleService.deleteHistoryEntry(entry.getId());
                loadHistory(txtSearch.getText().trim());
                limparDetalhe();
                btnDelete.setEnabled(false);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao apagar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e)  { loadHistory(txtSearch.getText().trim()); }
            @Override public void removeUpdate(DocumentEvent e)  { loadHistory(txtSearch.getText().trim()); }
            @Override public void changedUpdate(DocumentEvent e) {}
        });
    }

    public void reload() {
        loadHistory(txtSearch.getText().trim());
    }

    private void loadHistory(String cpfFilter) {
        try {
            List<SaleHistoryEntry> all = saleService.findAllHistory();
            all = all.stream()
                    .filter(e -> !"REMOVAL".equals(e.getType()))
                    .collect(Collectors.toList());
            if (cpfFilter != null && !cpfFilter.isBlank()) {
                all = all.stream()
                        .filter(e -> e.getCustomerCpf() != null
                                && e.getCustomerCpf().contains(cpfFilter))
                        .collect(Collectors.toList());
            }

            all.sort((a, b) -> {
                if (a.getDate() == null || b.getDate() == null) return 0;
                return b.getDate().compareTo(a.getDate());
            });
            tableModel.setEntries(all);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar histórico: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDetalhe(SaleHistoryEntry entry) {
        if (entry == null) { limparDetalhe(); return; }

        lblDetailDate.setText(entry.getDate() != null ? entry.getDate().format(FMT) : "—");
        lblDetailType.setText(entry.getType());
        lblDetailType.setForeground("SALE".equals(entry.getType())
                ? PokeTheme.ACCENT_YELLOW : PokeTheme.ACCENT_RED);
        lblDetailCpf.setText(entry.getCustomerCpf());
        lblDetailName.setText(entry.getCustomerName());
        lblDetailPayment.setText(entry.getPaymentMethod());
        lblDetailTotal.setText("₽ " + (entry.getTotal() != null
                ? entry.getTotal().toPlainString() : "0"));

        if (entry.getItems() != null && !entry.getItems().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (SaleHistoryEntry.SaleHistoryItem item : entry.getItems()) {
                sb.append(String.format("%-18s x%d\n  ₽%-8s = ₽%s\n",
                        truncate(item.getItemName(), 18),
                        item.getQuantity(),
                        item.getUnitPrice().toPlainString(),
                        item.getSubtotal().toPlainString()));
            }
            sb.append("\n──────────────────────\n");
            sb.append(String.format("TOTAL: ₽%s", entry.getTotal().toPlainString()));
            txtDetailItems.setText(sb.toString());
            txtDetailItems.setCaretPosition(0);
        } else {
            txtDetailItems.setText("(sem itens)");
        }
    }

    private void limparDetalhe() {
        lblDetailDate.setText("—");
        lblDetailType.setText("—");
        lblDetailType.setForeground(PokeTheme.TEXT_PRIMARY);
        lblDetailCpf.setText("—");
        lblDetailName.setText("—");
        lblDetailPayment.setText("—");
        lblDetailTotal.setText("—");
        txtDetailItems.setText("");
    }

    private String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max - 1) + "…" : s;
    }

    private JLabel makeHeaderLabel(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(PokeTheme.getPixelFont(7f));
        l.setForeground(color);
        return l;
    }

    private JLabel makeDetailKey(String text) {
        JLabel l = new JLabel(text);
        l.setFont(PokeTheme.getPixelFont(7f));
        l.setForeground(PokeTheme.ACCENT_BLUE);
        return l;
    }

    private JLabel makeDetailValue(String text) {
        JLabel l = new JLabel(text);
        l.setFont(PokeTheme.getPixelFont(8f));
        l.setForeground(PokeTheme.TEXT_PRIMARY);
        return l;
    }
}