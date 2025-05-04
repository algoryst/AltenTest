package com.alten.test.repository;

import com.alten.test.model.ShoppingCart;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Repository
public class ShoppingCartRepository extends JsonFileRepository<ShoppingCart> {

    public ShoppingCartRepository() {
        super("data/shoppingCarts.json", new TypeReference<List<ShoppingCart>>() {});
    }

    public Optional<ShoppingCart> findByUserEmail(String userEmail) throws IOException {
        return findAll().stream().filter(cart -> cart.getUserEmail().equals(userEmail)).findFirst();
    }

    public void save(ShoppingCart cart) throws IOException {
        List<ShoppingCart> carts = findAll();
        carts.add(cart);
        saveAll(carts);
    }

    public void update(ShoppingCart cart) throws IOException {
        List<ShoppingCart> carts = findAll();
        carts.removeIf(existingCart -> existingCart.getUserEmail().equals(cart.getUserEmail()));
        carts.add(cart);
        saveAll(carts);
    }
}


