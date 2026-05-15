package com.scholarflow.data.connection;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DatabaseConfig {
    private final String filename;

    private volatile Properties properties;

    public DatabaseConfig(final String filename) {
        this.filename = filename;
    }

    public String url() {
        return this.props().getProperty("db.url");
    }

    public String username() {
        return this.props().getProperty("db.username");
    }

    public String password() {
        return this.props().getProperty("db.password");
    }

    // Double-Checked Locking
    private Properties props() {
        if (this.properties == null) {
            synchronized (this) {
                if (this.properties == null) {
                    this.properties = this.load();
                }
            }
        }
        return this.properties;
    }

    private Properties load() {
        final Properties loaded = new Properties();
        try (InputStream input = getClass()
                .getClassLoader()
                .getResourceAsStream(this.filename)) {
            
            if (input == null) {
                throw new IllegalStateException("File not found...");
            }
            loaded.load(input);
        } catch (IOException ex) {
            throw new IllegalStateException("There is no able to read file...", ex);
        }
        return loaded;
    }
}