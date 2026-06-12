package com.pokemart.app.controller;

import com.pokemart.app.model.dto.ItemDto;
import com.pokemart.app.model.entity.Item;
import com.pokemart.app.model.service.ItemService;
import com.pokemart.app.view.ItemForm;
import com.pokemart.app.view.dialogs.ItemDialog;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.util.List;

public class ItemController {

    private final ItemForm view;
    private final ItemService itemService;

    public ItemController(ItemForm view) {
        this.view = view;
        this.itemService = new ItemService();
    }

    public void onSave(ItemDialog dialog) {
        try {
            ItemDto dto = montarDtoDoDialog(dialog);
            itemService.save(dto);

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(dialog, "Item salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                onLoadAll();
            });
        } catch (NumberFormatException ex) {
            showError(dialog, "O Preço e o Estoque devem ser valores numéricos válidos.");
        } catch (Exception e) {
            showError(dialog, e.getMessage());
        }
    }

    public void onUpdate(String id, ItemDialog dialog) {
        try {
            ItemDto dto = montarDtoDoDialog(dialog);
            itemService.update(id, dto);

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(dialog, "Item atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                onLoadAll();
            });
        } catch (NumberFormatException ex) {
            showError(dialog, "O Preço e o Estoque devem ser valores numéricos válidos.");
        } catch (Exception e) {
            showError(dialog, e.getMessage());
        }
    }

    public void onDelete(String id) {
        try {
            itemService.delete(id);

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(view, "Item removido com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                onLoadAll();
            });
        } catch (Exception e) {
            showError(view, e.getMessage());
        }
    }

    public void onLoadAll() {
        try {
            List<Item> items = itemService.findAll();
            SwingUtilities.invokeLater(() -> {
                view.getTableModel().setItems(items);
            });
        } catch (Exception e) {
            showError(view, "Erro ao carregar itens: " + e.getMessage());
        }
    }

    private ItemDto montarDtoDoDialog(ItemDialog dialog) throws NumberFormatException {
        String barcode = dialog.getTxtBarcode().trim();
        String name = dialog.getTxtName().trim();
        String category = dialog.getTxtCategory().trim();
        String priceText = dialog.getTxtPrice().trim().replace(",", ".");
        String stockText = dialog.getTxtStock().trim();

        BigDecimal price = new BigDecimal(priceText);
        int stock = Integer.parseInt(stockText);

        return new ItemDto(barcode, name, category, price, stock, dialog.getCurrentImagePath());
    }

    private void showError(java.awt.Component parent, String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(parent, message, "Erro", JOptionPane.ERROR_MESSAGE)
        );
    }
}