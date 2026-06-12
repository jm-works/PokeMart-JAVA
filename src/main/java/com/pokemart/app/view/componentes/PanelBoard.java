package com.pokemart.app.view.componentes;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;

public class PanelBoard extends javax.swing.JPanel {

    private Color color1;
    private Color color2;

    public PanelBoard() {
        color1 = getBackground();
        color2 = getBackground();
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
        super.paintComponent(g);
    }

    public Color getColor1() { return color1; }
    public void setColor1(Color color1) { this.color1 = color1; }
    public Color getColor2() { return color2; }
    public void setColor2(Color color2) { this.color2 = color2; }
}