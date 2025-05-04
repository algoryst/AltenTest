package com.alten.test;

import com.alten.test.model.Product;
import com.alten.test.repository.ProductRepository;
import com.alten.test.security.JWTUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

@TestPropertySource(properties = {
        "logging.level.org.springframework.web=DEBUG",
        "logging.level.org.springframework.security=DEBUG",
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository repository;

    @Autowired
    private JWTUtil jwtUtil;

    private String userToken;
    private String adminToken;

    @BeforeAll
    void setupTokens() {
        userToken = "Bearer " + jwtUtil.generateToken("user@example.com");
        adminToken = "Bearer " + jwtUtil.generateToken("admin@admin.com");
    }

    @BeforeEach
    void cleanUp() throws IOException {
        repository.deleteAll();
    }

    private Product createSampleProduct() {
        long now = System.currentTimeMillis();
        return new Product(
                null,
                "P001",
                "Test Product",
                "Test Description",
                "image.png",
                "Electronics",
                49.99,
                100,
                "REF-001",
                1L,
                "INSTOCK",
                4.5,
                now,
                now
        );
    }

    @Test
    void getAllProducts_withValidToken_shouldReturnEmptyList() {
        webTestClient.get()
                .uri("/products")
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class)
                .hasSize(0);
    }

    @Test
    void createProduct_withAdminToken_shouldSucceed() {
        Product product = createSampleProduct();

        webTestClient.post()
                .uri("/products")
                .header(HttpHeaders.AUTHORIZATION, adminToken)
                .bodyValue(product)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNumber()
                .jsonPath("$.name").isEqualTo("Test Product")
                .jsonPath("$.category").isEqualTo("Electronics");
    }

    @Test
    void getProductById_withValidToken_shouldReturnProduct() throws IOException {
        Product saved = repository.save(createSampleProduct());

        webTestClient.get()
                .uri("/products/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(saved.getId().longValue())
                .jsonPath("$.code").isEqualTo("P001");
    }

    @Test
    void updateProduct_withAdminToken_shouldModifyProduct() throws IOException {
        Product saved = repository.save(createSampleProduct());

        Product update = new Product(
                null,
                "P001-U",
                "Updated Name",
                "Updated Desc",
                "updated.png",
                "UpdatedCat",
                59.99,
                50,
                "REF-002",
                2L,
                "LOWSTOCK",
                3.5,
                saved.getCreatedAt(),
                System.currentTimeMillis()
        );

        webTestClient.patch()
                .uri("/products/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, adminToken)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Updated Name")
                .jsonPath("$.category").isEqualTo("UpdatedCat")
                .jsonPath("$.price").isEqualTo(59.99);
    }

    @Test
    void deleteProduct_withAdminToken_shouldSucceed() throws IOException {
        Product saved = repository.save(createSampleProduct());

        webTestClient.delete()
                .uri("/products/" + saved.getId())
                .header(HttpHeaders.AUTHORIZATION, adminToken)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void createProduct_withUserToken_shouldReturnForbidden() {
        Product product = createSampleProduct();

        webTestClient.post()
                .uri("/products")
                .header(HttpHeaders.AUTHORIZATION, userToken)
                .bodyValue(product)
                .exchange()
                .expectStatus().isForbidden();
    }
}


