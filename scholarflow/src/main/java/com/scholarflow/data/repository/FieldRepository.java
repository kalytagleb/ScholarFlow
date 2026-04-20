package com.scholarflow.data.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.business.model.Field;

public interface FieldRepository {
    Optional<Field> findById(UUID id);
    List<Field> findAll();
    Field save(Field field);
    void update(Field field);
    void delete(UUID uuid); 
}