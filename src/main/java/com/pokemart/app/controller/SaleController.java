package com.pokemart.app.controller;

import com.pokemart.app.model.dto.SaleItemDto;
import com.pokemart.app.model.dto.SaleRequestDto;
import com.pokemart.app.model.entity.Item;
import com.pokemart.app.model.entity.Sale;
import com.pokemart.app.model.repository.impl.ItemRepository;
import com.pokemart.app.model.repository.impl.SaleRepository;
import com.pokemart.app.model.service.SaleService;
import com.pokemart.app.model.tabela.modelo.SaleTableModel;
import com.pokemart.app.view.MainForm;
import com.pokemart.app.view.SaleForm;
import com.pokemart.app.view.dialogs.AddItemDialog;
import com.pokemart.app.view.dialogs.CustomerDialog;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SaleController implements ItemAdder {

    private final SaleForm view;
    private final MainForm mainForm;
    private final SaleService saleService;
    private final ItemRepository itemRepository;

    public SaleController(SaleForm view, MainForm mainForm) {
        this.view = view;
        this.mainForm = mainForm;
        this.itemRepository = new ItemRepository();
        this.saleService = new SaleService(itemRepository, new SaleRepository());
    }

    public void onOpenAddItemDialog() {
        List<Item> items = itemRepository.findAll();
        AddItemDialog d = new AddItemDialog(
                SwingUtilities.getWindowAncestor(view), this, items);
        d.setVisible(true);
    }

    public void onOpenCustomerDialog() {
        if (view.getTableModel().getRowCount() == 0) {
            showError("Adicione ao menos um item antes de concluir.");
            return;
        }
        CustomerDialog d = new CustomerDialog(
                SwingUtilities.getWindowAncestor(view), this);
        d.setVisible(true);
    }

    public void onAddItem(String barcode, int quantity) {
        try {
            if (barcode == null || barcode.trim().isEmpty())
                throw new IllegalArgumentException("Código de barras não pode ser vazio.");
            if (quantity <= 0)
                throw new IllegalArgumentException("A quantidade deve ser maior que zero.");

            Optional<Item> found = itemRepository.findAll().stream()
                    .filter(i -> i.getBarcode().equals(barcode.trim()))
                    .findFirst();

            if (found.isEmpty())
                throw new IllegalArgumentException("Item não encontrado: " + barcode);

            Item item = found.get();

            SwingUtilities.invokeLater(() -> {
                view.getTableModel().addRow(item, quantity);
                view.atualizarTotal();
                view.atualizarPreview(item);
            });

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    public void onComplete(String cpf, String name, String paymentMethod, CustomerDialog dialog) {
        try {
            List<SaleItemDto> items = new ArrayList<>();
            for (SaleTableModel.SaleRow row : view.getTableModel().getRows()) {
                items.add(new SaleItemDto(row.item.getBarcode(), row.quantity));
            }

            SaleRequestDto requestDto = new SaleRequestDto(cpf, name, paymentMethod, items);
            Sale sale = saleService.completeSale(requestDto);

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(dialog,
                        "Venda concluída! Total: ₽ " + sale.getTotal().toPlainString(),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                if (mainForm != null) {
                    mainForm.acrescentarCaixa(sale.getTotal());
                    mainForm.refreshItemTable();
                    mainForm.refreshHistoryTable();
                }
                onClear();
            });

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    public void onRemoveItem(int rowIndex) {
        SaleTableModel.SaleRow row = view.getTableModel().getRowAt(rowIndex);
        if (row == null) return;

        try {
            saleService.recordRemoval(row.item, row.quantity);
        } catch (Exception e) {
            System.err.println("Aviso: falha ao registrar remoção no histórico — " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            view.getTableModel().removeRow(rowIndex);
            view.atualizarTotal();
            view.limparPreview();
        });
    }

    public void onClear() {
        SwingUtilities.invokeLater(() -> {
            view.getTableModel().clear();
            view.atualizarTotal();
            view.limparCampos();
            view.limparPreview();
        });
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(view, message, "Atenção", JOptionPane.ERROR_MESSAGE)
        );
    }
}