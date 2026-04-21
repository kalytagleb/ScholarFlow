package com.scholarflow.business.model;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public final class Field {

    private static final Pattern NAME_PATTERN = 
        Pattern.compile("^[\\\\p{L}]+(?:[- ][\\\\p{L}]+)*$");

    private final Optional<UUID> id;
    private final String nameEn;
    private final String nameSk;
    private final Optional<String> description;

    // Used for loading from database
    public Field(
        UUID id,
        String nameEn,
        String nameSk,
        String description
    ) {
        this.id = Optional.of(Objects.requireNonNull(id));
        this.nameEn = validate(nameEn, "English name");
        this.nameSk = validate(nameSk, "Slovak name");
        this.description = Optional.ofNullable(description); // can be null
    }

    // Constructor for creating new field
    public Field(
        String nameEn,
        String nameSk,
        String description
    ) {
        this.id = Optional.empty();
        this.nameEn = validate(nameEn, "English name");
        this.nameSk = validate(nameSk, "Slovak name");
        this.description = Optional.ofNullable(description);
    }

    private String validate(String name, String label) {
        Objects.requireNonNull(name, label + " cannot be null");
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException(
                String.format("%s '%s' is invalid.", label, name)
            );
        }
        return name;
    }

    public Optional<UUID> id() {
        return id;
    }

    public String nameEn() {
        return nameEn;
    }

    public String nameSk() {
        return nameSk;
    }

    public Optional<String> description() {
        return description;
    }

    public String localizedName(String language) {
        return "sk".equalsIgnoreCase(language) ? nameSk : nameEn;
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