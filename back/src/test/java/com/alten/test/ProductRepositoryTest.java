package com.alten.test;

import com.alten.test.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.alten.test.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

public class ProductRepositoryTest {

    private File file;
    private ObjectMapper mapper;
    private ProductRepository productRepository;
    private List<Product> initialProducts;

    @BeforeEach
    void setUp() throws IOException {
        file = new File("data/products.json");
        mapper = new ObjectMapper();
        productRepository = new ProductRepository(mapper);
        initialProducts = List.of(
                new Product(1L, "PROD-A", "Product A", "Description A", "imageA.jpg", "Category1", 10.0, 100, "REF-A", 1001L, "INSTOCK", 4.5, System.currentTimeMillis(), System.currentTimeMillis()),
                new Product(2L, "PROD-B", "Product B", "Description B", "imageB.jpg", "Category2", 20.0, 50, "REF-B", 1002L, "LOWSTOCK", 3.8, System.currentTimeMillis(), System.currentTimeMillis())
        );
        mapper.writeValue(file, initialProducts);
    }

    @Test
    void findAll_shouldReturnAllProducts() throws IOException {
        List<Product> products = productRepository.findAll();
        assertEquals(initialProducts.size(), products.size());
        assertTrue(products.containsAll(initialProducts));
    }

    @Test
    void findAll_shouldReturnEmptyListIfFileDoesNotExist() throws IOException {
        Files.deleteIfExists(file.toPath());
        List<Product> products = productRepository.findAll();
        assertTrue(products.isEmpty());
    }

    @Test
    void findById_shouldReturnProductIfExists() throws IOException {
        Optional<Product> product = productRepository.findById(1L);
        assertTrue(product.isPresent());
        assertEquals("Product A", product.get().getName());
        assertEquals("PROD-A", product.get().getCode());
    }

    @Test
    void findById_shouldReturnEmptyOptionalIfNotExists() throws IOException {
        Optional<Product> product = productRepository.findById(3L);
        assertTrue(product.isEmpty());
    }

    @Test
    void save_shouldSaveNewProductWithGeneratedIdAndTimestamps() throws IOException {
        Product newProduct = new Product(null, "PROD-C", "Product C", "Description C", "imageC.jpg", "Category1", 30.0, 25, "REF-C", 1003L, "INSTOCK", 4.2, 0, 0);
        Product savedProduct = productRepository.save(newProduct);

        assertNotNull(savedProduct.getId());
        assertNotNull(savedProduct.getCreatedAt());
        assertNotNull(savedProduct.getUpdatedAt());
        assertEquals("Product C", savedProduct.getName());
        assertEquals("PROD-C", savedProduct.getCode());

        List<Product> allProducts = productRepository.findAll();
        assertEquals(initialProducts.size() + 1, allProducts.size());
        assertTrue(allProducts.contains(savedProduct));
    }

    @Test
    void update_shouldUpdateExistingProductAndTimestamps() throws IOException {
        Product updatedProduct = new Product(null, "PROD-A-UPD", "Updated A", "Updated Description A", "imageA-upd.jpg", "Category2", 15.0, 120, "REF-A-UPD", 1001L, "LOWSTOCK", 4.8, 0, 0);
        Optional<Product> result = productRepository.update(1L, updatedProduct);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("Updated A", result.get().getName());
        assertEquals("PROD-A-UPD", result.get().getCode());
        assertEquals(15.0, result.get().getPrice());
        assertEquals(120, result.get().getQuantity());
        assertNotNull(result.get().getCreatedAt());
        assertNotNull(result.get().getUpdatedAt());
        assertTrue(result.get().getUpdatedAt() >= result.get().getCreatedAt());

        List<Product> allProducts = productRepository.findAll();
        assertEquals(initialProducts.size(), allProducts.size());
        assertTrue(allProducts.stream().anyMatch(p -> p.getId().equals(1L) && p.getName().equals("Updated A") && p.getCode().equals("PROD-A-UPD")));
    }

    @Test
    void update_shouldReturnEmptyOptionalIfProductNotFound() throws IOException {
        Product updatedProduct = new Product(null, "PROD-C-UPD", "Updated C", "Updated Description C", "imageC-upd.jpg", "Category3", 35.0, 30, "REF-C-UPD", 1003L, "OUTOFSTOCK", 4.0, 0, 0);
        Optional<Product> result = productRepository.update(3L, updatedProduct);
        assertTrue(result.isEmpty());

        List<Product> allProducts = productRepository.findAll();
        assertEquals(initialProducts.size(), allProducts.size());
        assertFalse(allProducts.stream().anyMatch(p -> p.getName().equals("Updated C")));
        assertFalse(allProducts.stream().anyMatch(p -> p.getCode().equals("PROD-C-UPD")));
    }

    @Test
    void delete_shouldDeleteExistingProduct() throws IOException {
        boolean deleted = productRepository.delete(1L);
        assertTrue(deleted);

        List<Product> allProducts = productRepository.findAll();
        assertEquals(initialProducts.size() - 1, allProducts.size());
        assertFalse(allProducts.stream().anyMatch(p -> p.getId().equals(1L)));
        assertFalse(allProducts.stream().anyMatch(p -> p.getName().equals("Product A")));
        assertFalse(allProducts.stream().anyMatch(p -> p.getCode().equals("PROD-A")));
    }

    @Test
    void delete_shouldReturnFalseIfProductNotFound() throws IOException {
        boolean deleted = productRepository.delete(3L);
        assertFalse(deleted);

        List<Product> allProducts = productRepository.findAll();
        assertEquals(initialProducts.size(), allProducts.size());
    }

    @Test
    void deleteAll_shouldClearAllProducts() throws IOException {
        productRepository.deleteAll();
        List<Product> allProducts = productRepository.findAll();
        assertTrue(allProducts.isEmpty());
    }

}