package com.pokemart.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleRequestDto {
    private String customerCpf;
    private String customerName;
    private String paymentMethod;
    private List<SaleItemDto> items;
}