package com.scholarflow.business.model;

import java.util.Objects;
import java.util.UUID;

public final class Field {

    private final UUID id;
    private final String nameEn;
    private final String nameSk;
    private final String description;

    // Used for loading from database
    public Field(
        final UUID id,
        final String nameEn,
        final String nameSk,
        final String description
    ) {
        this.id = Objects.requireNonNull(id, "id cannot be null");
        this.nameEn = Objects.requireNonNull(nameEn, "nameEn cannot be null");
        this.nameSk = Objects.requireNonNull(nameSk, "nameSk cannot be null");
        this.description = description; // can be null
    }

    // Constructor for creating new field
    public Field(
        final String nameEn,
        final String nameSk,
        final String description
    ) {
        this(null, nameEn, nameSk, description);
    }

    public UUID id() {
        return id;
    }

    public String nameEn() {
        return nameEn;
    }

    public String nameSk() {
        return nameSk;
    }

    public String description() {
        return description;
    }

    public String getName(String language) {
        if ("sk".equalsIgnoreCase(language)) {
            return nameSk;
        }
        return nameEn;
    }

    public boolean isSame(Field other) {
        return other != null && Objects.equals(this.id, other.id);
    }

    @Override
    public String toString() {
        return String.format("Field{id=%s, nameEn='%s', nameSk='%s'}",
                id, nameEn, nameSk);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Field other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}