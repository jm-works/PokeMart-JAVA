package com.pokemart.app.view.componentes;

import com.pokemart.app.util.PokeTheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PokeButton extends JButton {
    private boolean hovered = false;

    public PokeButton(String text) {
        super(text);
        setFont(PokeTheme.getPixelFont(7f));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(PokeTheme.TEXT_PRIMARY);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovered = true;
                setForeground(PokeTheme.BACKGROUND);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovered = false;
                setForeground(PokeTheme.TEXT_PRIMARY);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        if (hovered) {
            g2.setColor(PokeTheme.ACCENT_YELLOW);
        } else {
            g2.setColor(PokeTheme.ACCENT_BLUE);
        }

        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(PokeTheme.BACKGROUND);
        g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        g2.dispose();
        super.paintComponent(g);
    }
}