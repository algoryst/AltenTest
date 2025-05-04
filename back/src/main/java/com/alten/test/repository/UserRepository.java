package com.alten.test.repository;


import com.alten.test.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//tout les répo ont des comportement qui peuvent etre factorisé
@Repository
public class UserRepository extends JsonFileRepository<User> {

    public UserRepository() {
        super("data/users.json", new TypeReference<List<User>>() {});
    }

    public Optional<User> findByEmail(String email) throws IOException {
        return findAll().stream().filter(u -> u.getEmail().equals(email)).findFirst();
    }

    public boolean existsByEmail(String email) throws IOException {
        return findByEmail(email).isPresent();
    }

    public void save(User user) {
        try {
            List<User> users = findAll();
            users.add(user);
            saveAll(users);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        }
    }
}


