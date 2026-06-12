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
public class Sale {
    private String id;
    private String customerCpf;
    private String customerName;
    private LocalDateTime date;
    private BigDecimal total;
    private String paymentMethod;
    private List<SaleItem> items;
}