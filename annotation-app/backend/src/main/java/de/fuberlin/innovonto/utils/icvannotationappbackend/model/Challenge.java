package de.fuberlin.innovonto.utils.icvannotationappbackend.model;

import com.fasterxml.jackson.annotation.JsonSetter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Challenge {
    @Id
    private String id;
    @Column(length = 10_000)
    private String description;

    //hibernate
    public Challenge() {
    }

    public Challenge(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    @JsonSetter("@id")
    public void setIdFromJson(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}