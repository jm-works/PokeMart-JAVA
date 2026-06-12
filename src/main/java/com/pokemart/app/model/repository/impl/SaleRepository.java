package com.pokemart.app.model.repository.impl;

import com.pokemart.app.model.entity.Sale;

public class SaleRepository extends JsonFileRepositoryImpl<Sale> {

    public SaleRepository() {
        super(Sale.class, "sales.json");
    }
}