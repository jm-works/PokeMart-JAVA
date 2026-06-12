package com.pokemart.app;

import com.pokemart.app.view.MainForm;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            MainForm mainForm = new MainForm();
            mainForm.setVisible(true);
        });
    }
}