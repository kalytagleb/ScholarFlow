package com.scholarflow.business.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.scholarflow.data.repository.FieldRepository;
import com.scholarflow.business.model.Field;

public final class FieldService {
    private final FieldRepository repository;

    public FieldService(final FieldRepository repository) {
        this.repository = repository;
    }

    public Field createField(final String nameEn, final String nameSk, final String description) {
        if (this.repository.findByName(nameEn, nameSk).isPresent()) {
            throw new IllegalArgumentException(
                String.format("Field with name '%s' or '%s' already exists", nameEn, nameSk)
            );
        }

        final Field field = new Field(nameEn, nameSk, description);
        return this.repository.save(field);
    }

    public Optional<Field> findById(final UUID id) {
        return repository.findById(id);
    }

    public List<Field> findAll() {
        return repository.findAll();
    }

    public Field updateField(final UUID id, final String nameEn, final String nameSk, final String description) {
        Objects.requireNonNull(id, "Field ID is required for update");

        final Field existing = this.repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Field not found with ID: " + id));

        if (!existing.nameEn().equalsIgnoreCase(nameEn) || !existing.nameSk().equalsIgnoreCase(nameSk)) {
            if (this.repository.findByName(nameEn, nameSk).isPresent()) {
                throw new IllegalArgumentException("Another field already uses these names");
            }
        }

        final Field updated = new Field(id, nameEn, nameSk, description);

        this.repository.update(updated);
        return updated;
    }

    public void deleteField(final UUID id) {
        this.repository.delete(Objects.requireNonNull(id));
    }

    public Optional<Field> getById(final UUID id) {
        return this.repository.findById(id);
    }

    public List<Field> getAllFields() {
        return this.repository.findAll();
    }
}
