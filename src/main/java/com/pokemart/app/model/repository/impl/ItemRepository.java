package com.pokemart.app.model.repository.impl;

import com.pokemart.app.model.entity.Item;

public class ItemRepository extends JsonFileRepositoryImpl<Item> {

    public ItemRepository() {
        super(Item.class, "items.json");
    }
}