package com.pokemart.app.model.service;

import com.pokemart.app.model.dto.SaleItemDto;
import com.pokemart.app.model.dto.SaleRequestDto;
import com.pokemart.app.model.entity.Item;
import com.pokemart.app.model.entity.Sale;
import com.pokemart.app.model.entity.SaleHistoryEntry;
import com.pokemart.app.model.entity.SaleItem;
import com.pokemart.app.model.repository.impl.ItemRepository;
import com.pokemart.app.model.repository.impl.SaleHistoryRepository;
import com.pokemart.app.model.repository.impl.SaleRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SaleService {

    private final ItemRepository itemRepository;
    private final SaleRepository saleRepository;
    private final SaleHistoryRepository historyRepository;

    public SaleService(ItemRepository itemRepository, SaleRepository saleRepository) {
        this.itemRepository = itemRepository;
        this.saleRepository = saleRepository;
        this.historyRepository = new SaleHistoryRepository();
    }

    public Sale completeSale(SaleRequestDto requestDto) {
        if (requestDto.getItems() == null || requestDto.getItems().isEmpty())
            throw new IllegalArgumentException("A venda precisa conter pelo menos um item.");

        Map<String, Integer> quantityMap = new LinkedHashMap<>();
        for (SaleItemDto dto : requestDto.getItems())
            quantityMap.merge(dto.getBarcode(), dto.getQuantity(), Integer::sum);

        BigDecimal total = BigDecimal.ZERO;
        List<SaleItem> finalSaleItems = new ArrayList<>();
        List<SaleHistoryEntry.SaleHistoryItem> historyItems = new ArrayList<>();
        List<Item> itemsToUpdate = new ArrayList<>();
        List<Item> catalog = itemRepository.findAll();

        for (Map.Entry<String, Integer> entry : quantityMap.entrySet()) {
            String barcode = entry.getKey();
            int qty = entry.getValue();

            Item item = catalog.stream()
                    .filter(i -> barcode.equals(i.getBarcode()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Item não encontrado: " + barcode));

            if (item.getStock() < qty)
                throw new IllegalStateException("Estoque insuficiente para: " + item.getName()
                        + ". Disponível: " + item.getStock() + " un.");

            BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(qty));
            total = total.add(subtotal);

            finalSaleItems.add(SaleItem.builder()
                    .itemId(item.getId()).quantity(qty)
                    .unitPrice(item.getPrice()).subtotal(subtotal).build());

            historyItems.add(SaleHistoryEntry.SaleHistoryItem.builder()
                    .itemId(item.getId()).itemName(item.getName()).barcode(item.getBarcode())
                    .quantity(qty).unitPrice(item.getPrice()).subtotal(subtotal).build());

            item.setStock(item.getStock() - qty);
            itemsToUpdate.add(item);
        }

        for (Item updated : itemsToUpdate) itemRepository.save(updated);

        Sale sale = Sale.builder()
                .customerCpf(requestDto.getCustomerCpf())
                .customerName(requestDto.getCustomerName())
                .paymentMethod(requestDto.getPaymentMethod())
                .date(LocalDateTime.now())
                .total(total).items(finalSaleItems).build();
        saleRepository.save(sale);

        historyRepository.save(SaleHistoryEntry.builder()
                .saleId(sale.getId())
                .type("SALE")
                .customerCpf(requestDto.getCustomerCpf())
                .customerName(requestDto.getCustomerName())
                .paymentMethod(requestDto.getPaymentMethod())
                .date(sale.getDate()).total(total).items(historyItems).build());

        return sale;
    }

    public void deleteHistoryEntry(String historyId) {
        historyRepository.findById(historyId).ifPresent(entry -> {
            if (entry.getSaleId() != null && !entry.getSaleId().isBlank())
                saleRepository.deleteById(entry.getSaleId());
            historyRepository.deleteById(historyId);
        });
    }

    public List<SaleHistoryEntry> findAllHistory() {
        return historyRepository.findAll();
    }

    public java.math.BigDecimal completePurchase(List<SaleItemDto> purchaseItems) {
        if (purchaseItems == null || purchaseItems.isEmpty())
            throw new IllegalArgumentException("A compra precisa conter pelo menos um item.");

        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        List<SaleHistoryEntry.SaleHistoryItem> historyItems = new ArrayList<>();
        List<Item> catalog = itemRepository.findAll();

        java.math.BigDecimal PURCHASE_FACTOR = new java.math.BigDecimal("0.60");

        for (SaleItemDto dto : purchaseItems) {
            Item item = catalog.stream()
                    .filter(i -> dto.getBarcode().equals(i.getBarcode()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Item não encontrado: " + dto.getBarcode()));

            java.math.BigDecimal costPrice = item.getPrice()
                    .multiply(PURCHASE_FACTOR)
                    .setScale(2, java.math.RoundingMode.HALF_UP);
            java.math.BigDecimal subtotal = costPrice
                    .multiply(java.math.BigDecimal.valueOf(dto.getQuantity()));
            total = total.add(subtotal);

            historyItems.add(SaleHistoryEntry.SaleHistoryItem.builder()
                    .itemId(item.getId()).itemName(item.getName()).barcode(item.getBarcode())
                    .quantity(dto.getQuantity()).unitPrice(costPrice).subtotal(subtotal)
                    .build());

            item.setStock(item.getStock() + dto.getQuantity());
            itemRepository.save(item);
        }

        historyRepository.save(SaleHistoryEntry.builder()
                .saleId(null).type("BUY")
                .customerCpf("—").customerName("Compra da loja").paymentMethod("—")
                .date(LocalDateTime.now()).total(total).items(historyItems)
                .build());

        return total;
    }

    public void recordRemoval(Item item, int quantity) {
        BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(quantity));
        historyRepository.save(SaleHistoryEntry.builder()
                .saleId(null)
                .type("REMOVAL")
                .customerCpf("—").customerName("Remoção avulsa").paymentMethod("—")
                .date(LocalDateTime.now()).total(subtotal)
                .items(List.of(SaleHistoryEntry.SaleHistoryItem.builder()
                        .itemId(item.getId()).itemName(item.getName()).barcode(item.getBarcode())
                        .quantity(quantity).unitPrice(item.getPrice()).subtotal(subtotal).build()))
                .build());
    }
}