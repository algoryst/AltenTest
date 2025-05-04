package com.alten.test.controller;

import com.alten.test.model.ShoppingCart;
import com.alten.test.model.ShoppingCartItem;
import com.alten.test.repository.ShoppingCartRepository;
import com.alten.test.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/shopping-cart")
public class ShoppingCartController {

    private final ShoppingCartRepository shoppingCartRepository;
    private final JwtService jwtService;

    public ShoppingCartController(ShoppingCartRepository shoppingCartRepository, JwtService jwtService) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.jwtService = jwtService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addToCart(HttpServletRequest request, @RequestBody ShoppingCartItem item) throws IOException {
        String userEmail = jwtService.validateToken(request);
        Optional<ShoppingCart> optionalCart = shoppingCartRepository.findByUserEmail(userEmail);
        ShoppingCart cart = optionalCart.orElse(new ShoppingCart(userEmail));
        cart.getItems().add(item);
        shoppingCartRepository.update(cart);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<Void> removeFromCart(HttpServletRequest request, @RequestBody ShoppingCartItem item) throws IOException {
        String userEmail = jwtService.validateToken(request);
        Optional<ShoppingCart> optionalCart = shoppingCartRepository.findByUserEmail(userEmail);
        if (optionalCart.isPresent()) {
            ShoppingCart cart = optionalCart.get();
            cart.getItems().removeIf(cartItem -> cartItem.getProductId().equals(item.getProductId()));
            shoppingCartRepository.update(cart);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<ShoppingCart> getShoppingCart(HttpServletRequest request) throws IOException {
        String userEmail = jwtService.validateToken(request);
        Optional<ShoppingCart> cart = shoppingCartRepository.findByUserEmail(userEmail);
        return cart.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
