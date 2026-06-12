package com.pokemart.app.model.repository.impl;

import com.pokemart.app.model.entity.SaleHistoryEntry;

public class SaleHistoryRepository extends JsonFileRepositoryImpl<SaleHistoryEntry> {

    public SaleHistoryRepository() {
        super(SaleHistoryEntry.class, "history.json");
    }
}
