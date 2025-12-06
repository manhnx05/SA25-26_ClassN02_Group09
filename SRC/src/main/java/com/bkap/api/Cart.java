package com.bkap.api;

import java.util.HashMap;
import java.util.Map;

import com.bkap.entity.Product;

public class Cart {

	private Map<Long, Integer> items = new HashMap<>();

    public void addItem(Product product) {
        Long id = product.getId();
        items.put(id, items.getOrDefault(id, 0) + 1);
    }

    public int getTotalItems() {
        return items.values().stream().mapToInt(i -> i).sum();
    }

    public Map<Long, Integer> getItems() {
        return items;
    }
}
