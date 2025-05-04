package com.alten.test.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("image")
    private String image;

    @JsonProperty("category")
    private String category;

    @JsonProperty("price")
    private double price;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("internalReference")
    private String internalReference;

    @JsonProperty("shellId")
    private Long shellId;

    @JsonProperty("inventoryStatus")
    private String inventoryStatus; // "INSTOCK", "LOWSTOCK", "OUTOFSTOCK"

    @JsonProperty("rating")
    private double rating;

    @JsonProperty("createdAt")
    private long createdAt;

    @JsonProperty("updatedAt")
    private long updatedAt;
}

