package com.alten.test.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class User {

    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;

    @JsonProperty("username")
    private String username;

    public User(String email, String password) {
        this.password = password;
        this.email = email;
    }

    @JsonProperty("firstname")
    private String firstname;
}
