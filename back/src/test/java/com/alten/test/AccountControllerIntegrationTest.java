package com.alten.test;




import com.alten.test.model.User;
import com.alten.test.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class AccountControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserRepository userRepository;

    private final File userFile = new File("data/users.json");
    private User testUser;

    @BeforeEach
    public void setUp() throws IOException {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs(); // Create the directory and any necessary parent directories
        }
        // Clean up the file storage before each test
        if (userFile.exists()) {
            userFile.delete(); // Remove the existing file
        }

        // Create a new empty file for the tests
        userFile.createNewFile();

        // Prepare a test user
        testUser = new User("test@example.com", "password123");

        // Ensure the file is clear before starting each test
        userRepository.deleteAll();
    }

    @Test
    public void createAccount_whenValidUser_thenCreateAccount() {
        webTestClient.post()
                .uri("/account")
                .bodyValue(testUser)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .value(user -> {
                    assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
                    assertThat(user.getPassword()).isEqualTo(testUser.getPassword()); // In production, password should be hashed
                });
    }

    @Test
    public void createAccount_whenEmailAlreadyExists_thenReturnBadRequest() throws IOException {
        // Create the user initially to simulate the case where the email already exists
        userRepository.save(testUser);

        webTestClient.post()
                .uri("/account")
                .bodyValue(testUser)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void generateToken_whenValidCredentials_thenReturnToken() throws IOException {
        // Save the user to be used for login
        userRepository.save(testUser);

        User credentials = new User(testUser.getEmail(), testUser.getPassword());

        webTestClient.post()
                .uri("/account/token")
                .bodyValue(credentials)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(token -> {
                    assertThat(token).isNotEmpty();
                });
    }

    @Test
    public void generateToken_whenInvalidCredentials_thenReturnUnauthorized() throws IOException {
        // Save the user to be used for login
        userRepository.save(testUser);

        User invalidCredentials = new User(testUser.getEmail(), "wrongpassword");

        webTestClient.post()
                .uri("/account/token")
                .bodyValue(invalidCredentials)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    public void generateToken_whenUserNotFound_thenReturnNotFound() {
        User nonExistentUser = new User("nonexistent@example.com", "password");

        webTestClient.post()
                .uri("/account/token")
                .bodyValue(nonExistentUser)
                .exchange()
                .expectStatus().isNotFound();
    }
}

