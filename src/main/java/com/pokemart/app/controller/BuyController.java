package com.pokemart.app.controller;

import com.pokemart.app.model.dto.SaleItemDto;
import com.pokemart.app.model.entity.Item;
import com.pokemart.app.model.repository.impl.ItemRepository;
import com.pokemart.app.model.repository.impl.SaleRepository;
import com.pokemart.app.model.service.SaleService;
import com.pokemart.app.model.tabela.modelo.SaleTableModel;
import com.pokemart.app.view.BuyForm;
import com.pokemart.app.view.MainForm;
import com.pokemart.app.view.dialogs.AddItemDialog;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BuyController implements ItemAdder {

    private static final BigDecimal MAX_CAIXA = new BigDecimal("999999");

    private final BuyForm view;
    private final MainForm mainForm;
    private final SaleService saleService;
    private final ItemRepository itemRepository;

    public BuyController(BuyForm view, MainForm mainForm) {
        this.view = view;
        this.mainForm = mainForm;
        this.itemRepository = new ItemRepository();
        this.saleService = new SaleService(itemRepository, new SaleRepository());
    }

    public void onOpenAddItemDialog() {
        List<Item> items = itemRepository.findAll();
        AddItemDialog d = new AddItemDialog(SwingUtilities.getWindowAncestor(view), this, items);
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

            BigDecimal costPrice = item.getPrice()
                    .multiply(new BigDecimal("0.60"))
                    .setScale(2, java.math.RoundingMode.HALF_UP);

            Item itemWithCostPrice = Item.builder()
                    .id(item.getId()).barcode(item.getBarcode()).name(item.getName())
                    .category(item.getCategory()).price(costPrice)
                    .stock(item.getStock()).imagePath(item.getImagePath())
                    .build();

            BigDecimal custo = costPrice.multiply(BigDecimal.valueOf(quantity));
            BigDecimal novoSaldo = mainForm.getSaldoCaixa().subtract(custo);
            if (novoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalStateException(
                        "Saldo insuficiente no caixa para esta compra.\nSaldo atual: P "
                                + mainForm.getSaldoCaixa().toPlainString());
            }

            SwingUtilities.invokeLater(() -> {
                view.getTableModel().addRow(itemWithCostPrice, quantity);
                view.atualizarTotal();
                view.atualizarPreview(itemWithCostPrice);
            });

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    public void onComplete() {
        try {
            if (view.getTableModel().getRowCount() == 0)
                throw new IllegalArgumentException("Adicione ao menos um item antes de confirmar.");

            List<SaleItemDto> items = new ArrayList<>();
            for (SaleTableModel.SaleRow row : view.getTableModel().getRows())
                items.add(new SaleItemDto(row.item.getBarcode(), row.quantity));

            BigDecimal total = view.getTableModel().getTotal();
            BigDecimal novoSaldo = mainForm.getSaldoCaixa().subtract(total);
            if (novoSaldo.compareTo(BigDecimal.ZERO) < 0)
                throw new IllegalStateException(
                        "Saldo insuficiente no caixa.\nSaldo atual: ₽ "
                                + mainForm.getSaldoCaixa().toPlainString()
                                + "\nTotal da compra: ₽ " + total.toPlainString());

            BigDecimal gasto = saleService.completePurchase(items);

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(view,
                        "Compra registrada! Total gasto: ₽ " + gasto.toPlainString(),
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                mainForm.descontarCaixa(gasto);
                mainForm.refreshItemTable();
                mainForm.refreshHistoryTable();
                onClear();
            });

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    public void onRemoveItem(int rowIndex) {
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
            view.limparPreview();
        });
    }

    private void showError(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(view, message, "Atenção", JOptionPane.ERROR_MESSAGE));
    }
}