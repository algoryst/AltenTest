package com.alten.test;

import com.alten.test.model.ShoppingCart;
import com.alten.test.model.ShoppingCartItem;
import com.alten.test.model.Wishlist;
import com.alten.test.model.WishlistItem;
import com.alten.test.repository.ShoppingCartRepository;
import com.alten.test.repository.WishlistRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(properties = {
        "logging.level.org.springframework.web=DEBUG",
        "logging.level.org.springframework.security=DEBUG",
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WishListControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private JWTUtil jwtUtil;

    private final String userEmail = "wishlist@example.com";
    private String jwtToken;

    @BeforeEach
    void setUp() {
        wishlistRepository.deleteAll();
        jwtToken = "Bearer " + jwtUtil.generateToken(userEmail);
    }

    @Test
    void addToWishlist_shouldAddSuccessfully() throws IOException {
        WishlistItem item = new WishlistItem(101L);

        webTestClient.post()
                .uri("/wishlist/add")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .bodyValue(item)
                .exchange()
                .expectStatus().isOk();

        Optional<Wishlist> wishlist = wishlistRepository.findByUserEmail(userEmail);
        assertTrue(wishlist.isPresent());
        assertEquals(1, wishlist.get().getItems().size());
        assertEquals(item.getProductId(), wishlist.get().getItems().get(0).getProductId());
    }

    @Test
    void removeFromWishlist_shouldRemoveSuccessfully() throws IOException {
        Wishlist wishlist = new Wishlist(userEmail);
        WishlistItem item = new WishlistItem(202L);
        wishlist.getItems().add(item);
        wishlistRepository.update(wishlist);

        webTestClient.post()
                .uri("/wishlist/remove")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .bodyValue(item)
                .exchange()
                .expectStatus().isOk();

        Optional<Wishlist> updated = wishlistRepository.findByUserEmail(userEmail);
        assertTrue(updated.isPresent());
        assertTrue(updated.get().getItems().isEmpty());
    }

    @Test
    void getWishlist_shouldReturnWishlist() throws IOException {
        Wishlist wishlist = new Wishlist(userEmail);
        WishlistItem item = new WishlistItem(303L);
        wishlist.getItems().add(item);
        wishlistRepository.update(wishlist);

        webTestClient.get()
                .uri("/wishlist")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Wishlist.class)
                .value(w -> {
                    assertEquals(userEmail, w.getUserEmail());
                    assertEquals(1, w.getItems().size());
                    assertEquals(item.getProductId(), w.getItems().get(0).getProductId());
                });
    }

    @Test
    void getWishlist_shouldReturnNotFoundWhenEmpty() {
        webTestClient.get()
                .uri("/wishlist")
                .header(HttpHeaders.AUTHORIZATION, jwtToken)
                .exchange()
                .expectStatus().isNotFound();
    }
}


