package com.scholarflow.business.service;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Translator {
    private final String language;
    private final ResourceBundle bundle;

    public Translator(final String language) {
        this.language = language;
        this.bundle = ResourceBundle.getBundle("localization.messages", Locale.of(language));
    }

    public String translate(String key) {
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        }

        System.err.println("WARNING -> " + key);
        return "!" + key + "!";
    }

    public String currentLanguage() {
        return this.language;
    }
}
