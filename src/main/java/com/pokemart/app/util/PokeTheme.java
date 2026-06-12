package com.pokemart.app.util;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.InputStream;

public class PokeTheme {

    public static final Color BACKGROUND    = new Color(0x1a1a2e);
    public static final Color PANEL_BG      = new Color(0x2e2e4e);
    public static final Color PANEL_LIGHT   = new Color(0x3a3a5c);
    public static final Color TEXT_PRIMARY  = new Color(0xe8f4e8);
    public static final Color TEXT_DIM      = new Color(0x8888aa);
    public static final Color ACCENT_BLUE   = new Color(0x5b8dd9);
    public static final Color ACCENT_BLUE_DARK = new Color(0x3a6bb0);
    public static final Color ACCENT_YELLOW = new Color(0xf0a500);
    public static final Color ACCENT_YELLOW_DARK = new Color(0xb87800);
    public static final Color ACCENT_RED    = new Color(0xcc3333);
    public static final Color ACCENT_RED_DARK = new Color(0x992222);
    public static final Color ACCENT_GREEN  = new Color(0x44bb44);
    public static final Color ACCENT_GREEN_DARK = new Color(0x228822);

    private static Font pixelFont;

    public static Font getPixelFont(float size) {
        if (pixelFont == null) {
            try (InputStream is = PokeTheme.class.getResourceAsStream("/fonts/PressStart2P-Regular.ttf")) {
                pixelFont = (is != null)
                        ? Font.createFont(Font.TRUETYPE_FONT, is)
                        : new Font("Courier New", Font.BOLD, 10);
            } catch (Exception e) {
                pixelFont = new Font("Courier New", Font.BOLD, 10);
            }
        }
        return pixelFont.deriveFont(size);
    }

    public static Border battleBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(TEXT_PRIMARY, 2),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BACKGROUND, 2),
                        BorderFactory.createLineBorder(TEXT_PRIMARY, 1)
                )
        );
    }

    public static Border pokeBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_BLUE, 2),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        );
    }

    public static void styleLabel(JLabel label) {
        label.setFont(getPixelFont(8f));
        label.setForeground(TEXT_PRIMARY);
    }

    public static void styleTextField(JTextField txt) {
        txt.setBackground(PANEL_BG);
        txt.setForeground(TEXT_PRIMARY);
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_BLUE, 1),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        txt.setFont(new Font("Courier New", Font.BOLD, 11));
        txt.setCaretColor(ACCENT_YELLOW);
    }

    public static JButton makeButton(String text, String variant) {
        Color base, hover, border;
        switch (variant) {
            case "primary" -> { base = ACCENT_BLUE;   hover = ACCENT_BLUE_DARK;   border = TEXT_PRIMARY; }
            case "success" -> { base = ACCENT_GREEN;  hover = ACCENT_GREEN_DARK;  border = TEXT_PRIMARY; }
            case "danger"  -> { base = ACCENT_RED;    hover = ACCENT_RED_DARK;    border = TEXT_PRIMARY; }
            default        -> { base = PANEL_LIGHT;   hover = PANEL_BG;           border = ACCENT_BLUE;  }
        }

        Color finalBase = base, finalHover = hover, finalBorder = border;

        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true;  repaint(); }
                    public void mouseExited (java.awt.event.MouseEvent e) { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g2.setColor(hovered ? finalHover : finalBase);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(finalBorder);
                g2.drawRect(0, 0, getWidth()-1, getHeight()-1);
                g2.setColor(hovered ? finalBase : finalHover);
                g2.drawLine(1, 1, getWidth()-2, 1);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(getPixelFont(7f));
        btn.setForeground(TEXT_PRIMARY);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        return btn;
    }

    public static JPanel makeBattlePanel() {
        return new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0x0e1a30),
                        getWidth(), getHeight(), new Color(0x1a1040));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(0x334466));
                int[] xs = {20, 60, 110, 180, 250, 40, 140, 200};
                int[] ys = {15, 40, 20, 35, 10, 60, 55, 25};
                for (int i = 0; i < xs.length; i++) {
                    if (xs[i] < getWidth() && ys[i] < getHeight()) {
                        g2.fillRect(xs[i], ys[i], 2, 2);
                    }
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
    }
}