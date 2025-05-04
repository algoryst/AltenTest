package com.alten.test.repository;

import com.alten.test.model.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductRepository extends JsonFileRepository<Product> {

    private final AtomicLong idGenerator = new AtomicLong(System.currentTimeMillis());

    public ProductRepository() {
        super("data/products.json", new TypeReference<List<Product>>() {});
    }

    public ProductRepository(ObjectMapper objectMapper) {
        super("data/products.json", new TypeReference<List<Product>>() {});
        this.mapper = objectMapper;
    }

    public Optional<Product> findById(Long id) throws IOException {
        return findAll().stream().filter(p -> p.getId().equals(id)).findFirst();
    }

    public Product save(Product product) throws IOException {
        List<Product> products = findAll();
        product.setId(idGenerator.incrementAndGet());
        product.setCreatedAt(System.currentTimeMillis());
        product.setUpdatedAt(System.currentTimeMillis());
        products.add(product);
        saveAll(products);
        return product;
    }

    public Optional<Product> update(Long id, Product updatedProduct) throws IOException {
        List<Product> products = findAll();
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.getId().equals(id)) {
                updatedProduct.setId(id);
                updatedProduct.setCreatedAt(p.getCreatedAt());
                updatedProduct.setUpdatedAt(System.currentTimeMillis());
                products.set(i, updatedProduct);
                saveAll(products);
                return Optional.of(updatedProduct);
            }
        }
        return Optional.empty();
    }

    public boolean delete(Long id) throws IOException {
        List<Product> products = findAll();
        boolean removed = products.removeIf(p -> p.getId().equals(id));
        if (removed) saveAll(products);
        return removed;
    }
}


