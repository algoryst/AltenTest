package com.alten.test.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {
    private String userEmail;
    private List<WishlistItem> items;

    public Wishlist(String userEmail) {
        this.userEmail = userEmail;
        this.items = new ArrayList<>();
    }
}
