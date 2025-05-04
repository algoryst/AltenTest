package com.alten.test.controller;


import com.alten.test.model.Product;
import com.alten.test.repository.ProductRepository;
import com.alten.test.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository repository;
    private final JwtService jwtService;

    public ProductController(ProductRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(HttpServletRequest request) throws IOException {
        jwtService.validateToken(request); // Throw if invalid
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id, HttpServletRequest request) throws IOException {
        jwtService.validateToken(request);
        Optional<Product> product = repository.findById(id);
        return product.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product, HttpServletRequest request) throws IOException {
        jwtService.validateAdmin(request);
        Product saved = repository.save(product);
        return ResponseEntity.ok(saved);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product updated, HttpServletRequest request) throws IOException {
        jwtService.validateAdmin(request);
        Optional<Product> result = repository.update(id, updated);
        return result.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, HttpServletRequest request) throws IOException {
        jwtService.validateAdmin(request);
        boolean deleted = repository.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}

