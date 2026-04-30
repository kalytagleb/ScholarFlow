package com.scholarflow.business.service;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Translator {
    private final ResourceBundle bundle;

    public Translator(String language) {
        this.bundle = ResourceBundle.getBundle("localization.messages", Locale.of(language));
    }

    public String translate(String key) {
        return bundle.getString(key);
    }
}
