package com.alten.test.controller;

import com.alten.test.model.Wishlist;
import com.alten.test.model.WishlistItem;
import com.alten.test.repository.WishlistRepository;
import com.alten.test.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistRepository wishlistRepository;
    private final JwtService jwtService;

    public WishlistController(WishlistRepository wishlistRepository, JwtService jwtService) {
        this.wishlistRepository = wishlistRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addToWishlist(HttpServletRequest request, @RequestBody WishlistItem item) throws IOException {
        String userEmail = jwtService.validateToken(request);
        Optional<Wishlist> optionalWishlist = wishlistRepository.findByUserEmail(userEmail);
        Wishlist wishlist = optionalWishlist.orElse(new Wishlist(userEmail));
        wishlist.getItems().add(item);
        wishlistRepository.update(wishlist);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<Void> removeFromWishlist(HttpServletRequest request, @RequestBody WishlistItem item) throws IOException {
        String userEmail = jwtService.validateToken(request);
        Optional<Wishlist> optionalWishlist = wishlistRepository.findByUserEmail(userEmail);
        if (optionalWishlist.isPresent()) {
            Wishlist wishlist = optionalWishlist.get();
            wishlist.getItems().removeIf(wishlistItem -> wishlistItem.getProductId().equals(item.getProductId()));
            wishlistRepository.update(wishlist);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Wishlist> getWishlist(HttpServletRequest request) throws IOException {
        String userEmail = jwtService.validateToken(request);
        Optional<Wishlist> wishlist = wishlistRepository.findByUserEmail(userEmail);
        return wishlist.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
