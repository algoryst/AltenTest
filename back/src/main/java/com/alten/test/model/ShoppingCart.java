package com.alten.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart {
    private String userEmail;
    private List<ShoppingCartItem> items;

    public ShoppingCart(String userEmail) {
        this.userEmail = userEmail;
        this.items = new ArrayList<>();
    }
}
