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
public class Item {
    private String id;
    private String barcode;
    private String name;
    private String category;
    private BigDecimal price;
    private int stock;
    private String imagePath;
}