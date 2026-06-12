package com.pokemart.app.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleHistoryEntry {

    private String id;
    private String saleId;
    private String type;

    private String customerCpf;
    private String customerName;
    private String paymentMethod;

    private LocalDateTime date;
    private BigDecimal total;

    private List<SaleHistoryItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleHistoryItem {
        private String itemId;
        private String itemName;
        private String barcode;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}
