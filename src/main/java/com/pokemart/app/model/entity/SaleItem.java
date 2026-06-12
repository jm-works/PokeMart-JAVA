package com.pokemart.app.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItem {
    private String itemId;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}