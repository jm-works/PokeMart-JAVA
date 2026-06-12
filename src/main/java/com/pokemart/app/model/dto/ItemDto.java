package com.pokemart.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDto {
    private String barcode;
    private String name;
    private String category;
    private BigDecimal price;
    private int stock;
    private String imagePath;
}