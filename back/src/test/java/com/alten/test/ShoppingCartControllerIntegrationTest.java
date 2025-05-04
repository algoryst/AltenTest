package com.alten.test;

import com.alten.test.model.ShoppingCart;
import com.alten.test.model.ShoppingCartItem;
import com.alten.test.repository.ShoppingCartRepository;
import com.alten.test.security.JWTUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {
        "logging.level.org.springframework.web=DEBUG",
        "logging.level.org.springframework.security=DEBUG",
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShoppingCartControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ShoppingCartRepository repository;

    @Autowired
    private JWTUtil jwtUtil;

    private String userToken;

    @BeforeAll
    void setupTokens() {
        userToken = "Bearer " + jwtUtil.generateToken("user@example.com");
    }

    @BeforeEach
    void cleanUp() throws IOException {
        repository.deleteAll();
    }

    @Test
    public void addToCart_whenValidRequest_thenReturnOk() {
        ShoppingCartItem item = new ShoppingCartItem(1L, 2);  // Example productId = 1 and quantity = 2

        webTestClient.post()
                .uri("/shopping-cart/add")
                .header("Authorization", userToken)
                .bodyValue(item)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void removeFromCart_whenItemExists_thenReturnOk() {
        ShoppingCartItem itemToRemove = new ShoppingCartItem(1L, 2);  // Example productId = 1

        // Add an item to the cart first
        webTestClient.post()
                .uri("/shopping-cart/add")
                .header("Authorization", userToken)
                .bodyValue(itemToRemove)
                .exchange()
                .expectStatus().isOk();

        // Now remove the same item
        webTestClient.post()
                .uri("/shopping-cart/remove")
                .header("Authorization", userToken)
                .bodyValue(itemToRemove)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void getShoppingCart_whenItemsExist_thenReturnShoppingCart() {
        ShoppingCartItem item = new ShoppingCartItem(1L, 2);  // Example productId = 1 and quantity = 2

        // Add an item to the cart first
        webTestClient.post()
                .uri("/shopping-cart/add")
                .header("Authorization", userToken)
                .bodyValue(item)
                .exchange()
                .expectStatus().isOk();

        // Retrieve the shopping cart
        webTestClient.get()
                .uri("/shopping-cart")
                .header("Authorization", userToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ShoppingCart.class)
                .value(cart -> {
                    assertThat(cart.getUserEmail()).isEqualTo("user@example.com");  // Replace with the correct email
                    assertThat(cart.getItems()).hasSize(1);
                    assertThat(cart.getItems().get(0).getProductId()).isEqualTo(1L);
                    assertThat(cart.getItems().get(0).getQuantity()).isEqualTo(2);
                });
    }

    @Test
    public void getShoppingCart_whenNoItems_thenReturnNotFound() {
        webTestClient.get()
                .uri("/shopping-cart")
                .header("Authorization", userToken)
                .exchange()
                .expectStatus().isNotFound();
    }
}


