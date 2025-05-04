package com.alten.test.repository;


import com.alten.test.model.Wishlist;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Repository
public class WishlistRepository extends JsonFileRepository<Wishlist> {

    public WishlistRepository() {
        super("data/wishlists.json", new TypeReference<List<Wishlist>>() {});
    }

    public Optional<Wishlist> findByUserEmail(String userEmail) throws IOException {
        return findAll().stream().filter(w -> w.getUserEmail().equals(userEmail)).findFirst();
    }

    public void save(Wishlist wishlist) throws IOException {
        List<Wishlist> wishlists = findAll();
        wishlists.add(wishlist);
        saveAll(wishlists);
    }

    public void update(Wishlist wishlist) throws IOException {
        List<Wishlist> wishlists = findAll();
        wishlists.removeIf(existing -> existing.getUserEmail().equals(wishlist.getUserEmail()));
        wishlists.add(wishlist);
        saveAll(wishlists);
    }
}


