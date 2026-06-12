package com.pokemart.app.view;

import com.pokemart.app.util.PokeTheme;
import com.pokemart.app.model.repository.impl.CashierRepository;
import com.pokemart.app.model.repository.impl.ItemRepository;
import com.pokemart.app.model.repository.impl.SaleHistoryRepository;
import com.pokemart.app.model.repository.impl.SaleRepository;
import com.pokemart.app.model.entity.Item;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class MainForm extends JFrame {

    private static final java.math.BigDecimal MAX_CAIXA = new java.math.BigDecimal("999999");
    private static final java.math.BigDecimal MIN_CAIXA = java.math.BigDecimal.ZERO;

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private MenuButton btnProdutos;
    private MenuButton btnVendas;
    private MenuButton btnCompras;
    private MenuButton btnHistorico;
    private JLabel lblCaixaValor;
    private java.math.BigDecimal totalCaixa = java.math.BigDecimal.ZERO;
    private ItemForm itemForm;
    private HistoryForm historyForm;
    private final CashierRepository cashierRepository = new CashierRepository();

    private float pokeballPulse = 0f;
    private boolean pulseDir = true;
    private Timer pulseTimer;

    public MainForm() {
        totalCaixa = cashierRepository.load();
        setTitle("Poke Mart — Zezin Edition");
        setSize(1366, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(PokeTheme.BACKGROUND);
        setLayout(new BorderLayout());
        initComponents((JPanel) getContentPane());
        startPulseAnimation();
    }

    private void startPulseAnimation() {
        pulseTimer = new Timer(50, e -> {
            pokeballPulse += pulseDir ? 0.05f : -0.05f;
            if (pokeballPulse >= 1f) { pokeballPulse = 1f; pulseDir = false; }
            if (pokeballPulse <= 0f) { pokeballPulse = 0f; pulseDir = true; }
            if (getContentPane().getComponentCount() > 0) {
                getContentPane().getComponent(0).repaint();
            }
        });
        pulseTimer.start();
    }

    private void initComponents(JPanel root) {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x252545),
                        0, getHeight(), new Color(0x1a1a35));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setColor(PokeTheme.ACCENT_BLUE);
                g2.fillRect(getWidth() - 2, 0, 2, getHeight());

                int cx = getWidth() / 2;
                int cy = 78;
                float pulse = pokeballPulse;
                int r = (int)(46 + pulse * 4);
                drawPokeball(g2, cx, cy, r, pulse);

                g2.dispose();
                super.paintComponent(g);
            }

            private void drawPokeball(Graphics2D g2, int cx, int cy, int r, float pulse) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(new Color(0, 0, 0, 60));
                g2.fillOval(cx - r + 4, cy - r + 6, r * 2, r * 2);

                g2.setColor(PokeTheme.ACCENT_RED);
                g2.fillArc(cx - r, cy - r, r * 2, r * 2, 0, 180);

                g2.setColor(new Color(0xf0f0f0));
                g2.fillArc(cx - r, cy - r, r * 2, r * 2, 180, 180);

                g2.setColor(new Color(0x111122));
                g2.fillRect(cx - r, cy - 4, r * 2, 8);

                g2.setColor(PokeTheme.ACCENT_BLUE);
                g2.fillRect(cx - r, cy - 4, r * 2, 2);

                g2.setColor(new Color(0xdddddd));
                g2.fill(new Ellipse2D.Float(cx - 12, cy - 12, 24, 24));

                Color btnColor = interpolateColor(
                        new Color(0x888888), new Color(0xffffff), pulse);
                g2.setColor(btnColor);
                g2.fill(new Ellipse2D.Float(cx - 8, cy - 8, 16, 16));

                g2.setColor(new Color(0x111122));
                g2.setStroke(new BasicStroke(2.5f));
                g2.draw(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
                g2.draw(new Ellipse2D.Float(cx - 12, cy - 12, 24, 24));
                g2.setStroke(new BasicStroke(1f));

                g2.setColor(new Color(255, 255, 255, 60));
                g2.fillArc(cx - r + 6, cy - r + 4, r - 8, r / 2, 20, 140);
            }

            private Color interpolateColor(Color a, Color b, float t) {
                int r = (int)(a.getRed()   + t * (b.getRed()   - a.getRed()));
                int gr = (int)(a.getGreen() + t * (b.getGreen() - a.getGreen()));
                int bl = (int)(a.getBlue()  + t * (b.getBlue()  - a.getBlue()));
                return new Color(r, gr, bl);
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new MigLayout("wrap 1, fillx, insets 0 8 10 8",
                "[grow, fill]", "158[]4[]5[]5[]5[]5[]18[]5[]push[]10[]"));

        JLabel lblTitle = new JLabel("POKE MART", SwingConstants.CENTER);
        lblTitle.setFont(PokeTheme.getPixelFont(9f));
        lblTitle.setForeground(PokeTheme.ACCENT_YELLOW);
        sidebar.add(lblTitle);

        JLabel lblSub = new JLabel("-- PALLET TOWN --", SwingConstants.CENTER);
        lblSub.setFont(PokeTheme.getPixelFont(6f));
        lblSub.setForeground(PokeTheme.ACCENT_BLUE);
        sidebar.add(lblSub, "gaptop 2");

        sidebar.add(makeSeparator());

        btnProdutos  = new MenuButton("PRODUTOS",  "Gerenciar itens", "BAG");
        btnVendas    = new MenuButton("VENDAS",    "PDV / Registrar", "SHOP");
        btnCompras   = new MenuButton("COMPRAS",   "Repor estoque",   "BOX");
        btnHistorico = new MenuButton("HISTORICO", "Notas / Registros","LOG");

        btnProdutos.addActionListener(e  -> selectMenu("PRODUTOS",  btnProdutos));
        btnVendas.addActionListener(e    -> selectMenu("VENDAS",    btnVendas));
        btnCompras.addActionListener(e   -> selectMenu("COMPRAS",   btnCompras));
        btnHistorico.addActionListener(e -> selectMenu("HISTORICO", btnHistorico));

        sidebar.add(btnProdutos,  "h 48!");
        sidebar.add(btnVendas,    "h 48!");
        sidebar.add(btnCompras,   "h 48!");
        sidebar.add(btnHistorico, "h 48!");

        sidebar.add(makeSeparator(), "gaptop 4");

        JPanel caixaPanel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x1a1a2e));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(PokeTheme.ACCENT_BLUE);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        caixaPanel.setOpaque(false);
        caixaPanel.setLayout(new MigLayout("wrap 1, insets 6 8 6 8", "[grow, fill]"));
        JLabel lblCaixaLabel = new JLabel("CAIXA");
        lblCaixaLabel.setFont(PokeTheme.getPixelFont(7f));
        lblCaixaLabel.setForeground(PokeTheme.ACCENT_BLUE);
        lblCaixaValor = new JLabel("₽ " + String.format("%,.2f", totalCaixa).replace(".", ","));
        lblCaixaValor.setFont(PokeTheme.getPixelFont(11f));
        lblCaixaValor.setForeground(PokeTheme.ACCENT_YELLOW);
        caixaPanel.add(lblCaixaLabel);
        caixaPanel.add(lblCaixaValor);
        sidebar.add(caixaPanel, "growx");

        JButton btnReset = new JButton("[DEV] RESET") {
            private boolean hov = false;
            { addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hov = true;  repaint(); }
                public void mouseExited (MouseEvent e) { hov = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(hov ? PokeTheme.ACCENT_RED : PokeTheme.ACCENT_RED_DARK);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(0xff6666));
                g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
                g2.drawLine(1, 1, getWidth()-2, 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btnReset.setFont(PokeTheme.getPixelFont(6f));
        btnReset.setForeground(PokeTheme.TEXT_PRIMARY);
        btnReset.setContentAreaFilled(false);
        btnReset.setFocusPainted(false);
        btnReset.setBorderPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> confirmarReset());
        sidebar.add(btnReset, "h 26!, gaptop 4");

        JPanel footer = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(0x1a1a2e));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(PokeTheme.ACCENT_BLUE);
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        footer.setOpaque(false);
        footer.setLayout(new MigLayout("insets 6 0 6 0, wrap 1", "[grow, fill]"));
        JLabel lblVer = new JLabel("v0.9 -- ZEZIN EDITION", SwingConstants.CENTER);
        lblVer.setFont(PokeTheme.getPixelFont(6f));
        lblVer.setForeground(PokeTheme.ACCENT_BLUE);
        footer.setPreferredSize(new Dimension(0, 28));
        footer.add(lblVer);
        sidebar.add(footer, "dock south");

        root.add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(PokeTheme.BACKGROUND);

        itemForm = new ItemForm();
        historyForm = new HistoryForm();
        SaleForm saleForm = new SaleForm(this);
        BuyForm buyForm = new BuyForm(this);

        contentPanel.add(itemForm,    "PRODUTOS");
        contentPanel.add(saleForm,    "VENDAS");
        contentPanel.add(buyForm,     "COMPRAS");
        contentPanel.add(historyForm, "HISTORICO");

        root.add(contentPanel, BorderLayout.CENTER);
        selectMenu("PRODUTOS", btnProdutos);
    }

    private JSeparator makeSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(0x3a3a5c));
        sep.setBackground(new Color(0x3a3a5c));
        return sep;
    }

    private void confirmarReset() {
        int ok = JOptionPane.showConfirmDialog(this,
                "RESET COMPLETO?\n\n" +
                        "- Estoque de todos os itens -> 1\n" +
                        "- Caixa -> P 100,00\n" +
                        "- sales.json limpo\n" +
                        "- history.json limpo\n\n" +
                        "Esta acao nao pode ser desfeita.",
                "[DEV] Confirmar Reset",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            ItemRepository itemRepo = new ItemRepository();
            for (Item item : itemRepo.findAll()) {
                item.setStock(1);
                itemRepo.save(item);
            }
            new SaleRepository().findAll()
                    .forEach(s -> new SaleRepository().deleteById(s.getId()));
            SaleHistoryRepository histRepo = new SaleHistoryRepository();
            histRepo.findAll().forEach(h -> histRepo.deleteById(h.getId()));

            totalCaixa = new java.math.BigDecimal("100");
            cashierRepository.save(totalCaixa);
            atualizarLblCaixa();
            refreshItemTable();
            if (historyForm != null) SwingUtilities.invokeLater(() -> historyForm.reload());

            JOptionPane.showMessageDialog(this,
                    "Reset concluido!\nCaixa: P 100,00 | Estoque: 1 un. por item.",
                    "[DEV] Reset OK", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erro durante reset: " + ex.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public java.math.BigDecimal getSaldoCaixa() { return totalCaixa; }

    public void acrescentarCaixa(java.math.BigDecimal valor) {
        java.math.BigDecimal novo = totalCaixa.add(valor);
        totalCaixa = novo.compareTo(MAX_CAIXA) > 0 ? MAX_CAIXA : novo;
        cashierRepository.save(totalCaixa);
        atualizarLblCaixa();
    }

    public void descontarCaixa(java.math.BigDecimal valor) {
        java.math.BigDecimal novo = totalCaixa.subtract(valor);
        totalCaixa = novo.compareTo(MIN_CAIXA) < 0 ? MIN_CAIXA : novo;
        cashierRepository.save(totalCaixa);
        atualizarLblCaixa();
    }

    private void atualizarLblCaixa() {
        String formatted = String.format("%,.2f", totalCaixa).replace(".", ",");
        SwingUtilities.invokeLater(() -> lblCaixaValor.setText("₽ " + formatted));
    }

    public void refreshItemTable() {
        if (itemForm != null)
            SwingUtilities.invokeLater(() -> itemForm.getController().onLoadAll());
    }

    public void refreshHistoryTable() {
        if (historyForm != null)
            SwingUtilities.invokeLater(() -> historyForm.reload());
    }

    private void selectMenu(String card, MenuButton active) {
        btnProdutos.setActive(false);
        btnVendas.setActive(false);
        btnCompras.setActive(false);
        btnHistorico.setActive(false);
        active.setActive(true);
        cardLayout.show(contentPanel, card);
    }

    private static class MenuButton extends JButton {
        private final String subtitle;
        private final String badge;
        private boolean active  = false;
        private boolean hovered = false;

        MenuButton(String text, String subtitle, String badge) {
            super(text);
            this.subtitle = subtitle;
            this.badge    = badge;
            setFont(PokeTheme.getPixelFont(8f));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(0, 14, 0, 0));
            setForeground(PokeTheme.TEXT_PRIMARY);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
            });
        }

        void setActive(boolean active) {
            this.active = active;
            setForeground(active ? PokeTheme.BACKGROUND : PokeTheme.TEXT_PRIMARY);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

            boolean lit = active || hovered;

            if (lit) {
                GradientPaint gp = new GradientPaint(
                        0, 0, PokeTheme.ACCENT_YELLOW,
                        getWidth(), 0, PokeTheme.ACCENT_YELLOW_DARK);
                g2.setPaint(gp);
            } else {
                g2.setColor(new Color(0x2a2a4a));
            }
            g2.fillRect(0, 0, getWidth(), getHeight());

            if (active) {
                g2.setColor(PokeTheme.TEXT_PRIMARY);
                g2.fillRect(0, 4, 4, getHeight() - 8);
            }

            g2.setColor(new Color(0x1a1a2e));
            g2.fillRect(0, getHeight() - 1, getWidth(), 1);

            g2.dispose();
            super.paintComponent(g);


            Graphics2D g3 = (Graphics2D) g.create();
            g3.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

            int bw = 28, bh = 14;
            int bx = getWidth() - bw - 6;
            int by = (getHeight() - bh) / 2;
            g3.setColor(lit ? new Color(0x33330000, true) : PokeTheme.ACCENT_BLUE_DARK);
            g3.fillRect(bx, by, bw, bh);
            g3.setColor(lit ? new Color(0x3a3a00) : PokeTheme.ACCENT_BLUE);
            g3.drawRect(bx, by, bw - 1, bh - 1);
            g3.setFont(PokeTheme.getPixelFont(5f));
            g3.setColor(lit ? new Color(0x3a3a00) : PokeTheme.TEXT_PRIMARY);
            FontMetrics fm = g3.getFontMetrics();
            int tx = bx + (bw - fm.stringWidth(badge)) / 2;
            int ty = by + (bh + fm.getAscent() - fm.getDescent()) / 2;
            g3.drawString(badge, tx, ty);

            g3.setFont(PokeTheme.getPixelFont(6f));
            g3.setColor(lit ? new Color(0x3a3a00) : PokeTheme.TEXT_DIM);
            g3.drawString(subtitle, 14, getHeight() - 10);
            g3.dispose();
        }
    }
}