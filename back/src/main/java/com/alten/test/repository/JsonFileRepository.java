package com.alten.test.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class JsonFileRepository<T> {

    protected final File file;
    protected ObjectMapper mapper;
    protected final TypeReference<List<T>> typeRef;

    protected JsonFileRepository(String filePath, TypeReference<List<T>> typeRef) {
        this.file = new File(filePath);
        this.mapper = new ObjectMapper();
        this.typeRef = typeRef;
    }

    public List<T> findAll() throws IOException {
        if (!file.exists()) return new ArrayList<>();
        return mapper.readValue(file, typeRef);
    }

    public void saveAll(List<T> items) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, items);
    }

    public void deleteAll() {
        try {
            saveAll(new ArrayList<>());
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete items: " + e.getMessage(), e);
        }
    }
}

