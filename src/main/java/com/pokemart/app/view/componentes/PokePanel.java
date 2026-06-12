package com.pokemart.app.view.componentes;

import com.pokemart.app.util.PokeTheme;

import javax.swing.*;
import java.awt.*;

public class PokePanel extends JPanel {
    private final String title;

    public PokePanel() {
        this(null);
    }

    public PokePanel(String title) {
        this.title = title;
        setOpaque(false);
        int topPadding = (title != null && !title.isEmpty()) ? 30 : 15;
        setBorder(BorderFactory.createEmptyBorder(topPadding, 15, 15, 15));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        int w = getWidth();
        int h = getHeight();

        g2.setColor(PokeTheme.ACCENT_BLUE);
        g2.fillRect(0, 0, w, h);

        g2.setColor(PokeTheme.PANEL_BG);
        g2.fillRect(2, 2, w - 4, h - 4);

        g2.setColor(PokeTheme.TEXT_PRIMARY);
        g2.fillRect(4, 4, w - 8, h - 8);

        g2.setColor(PokeTheme.PANEL_BG);
        g2.fillRect(5, 5, w - 10, h - 10);

        if (title != null && !title.isEmpty()) {
            g2.setFont(PokeTheme.getPixelFont(8f));
            g2.setColor(PokeTheme.TEXT_PRIMARY);
            g2.drawString(title, 15, 20);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}