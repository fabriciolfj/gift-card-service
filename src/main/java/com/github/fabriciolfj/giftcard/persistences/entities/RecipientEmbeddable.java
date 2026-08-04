package com.github.fabriciolfj.giftcard.persistences.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;


@Embeddable
public class RecipientEmbeddable {

    @Column(name = "recipient_name", length = 120, updatable = false)
    private String name;

    @Column(name = "recipient_email", length = 255, updatable = false)
    private String email;

    @Column(name = "recipient_message", length = 280, updatable = false)
    private String message;

    protected RecipientEmbeddable() {
    }

    RecipientEmbeddable(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMessage() { return message; }


    boolean isEmpty() {
        return name == null && email == null && message == null;
    }

    @Override
    public String toString() {
        return "RecipientEmbeddable{hasEmail=%s}".formatted(email != null);
    }
}